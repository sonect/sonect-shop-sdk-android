package ch.sonect.sdk.shop.integrationapp.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import ch.sonect.sdk.domain.shop.model.Shop
import ch.sonect.sdk.shop.SonectSDK
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ConfigRepository(private val context: Context) {

    private var selectedEnv: SonectSDK.Config.Enviroment
    private val preferences = context.getSharedPreferences("SampleApp", AppCompatActivity.MODE_PRIVATE)

    init {
        val storedConfig = getConfig()
        val env = (storedConfig.find { it is Config.Environment } as? Config.Environment)?.env ?: SonectSDK.Config.Enviroment.DEV
        selectedEnv = env
    }

    fun getConfig(): Set<Config> = setOf(
        Config.Environment(preferences.getString(Config.Environment::class.java.name, "").toEnv()),
        Config.MerchantId(preferences.getString(Config.MerchantId::class.java.name + selectedEnv, "") ?: ""),
        Config.ClientId(preferences.getString(Config.ClientId::class.java.name + selectedEnv, "") ?: ""),
        Config.ClientSecret(preferences.getString(Config.ClientSecret::class.java.name + selectedEnv, "") ?: ""),
        Config.DeviceId(preferences.getString(Config.DeviceId::class.java.name + selectedEnv, "") ?: ""),
        Config.HmacKey(preferences.getString(Config.HmacKey::class.java.name + selectedEnv, "") ?: ""),
        Config.Theme(preferences.getBoolean(Config.Theme::class.java.name + selectedEnv, false)),
        Config.Scanner(preferences.getBoolean(Config.Scanner::class.java.name + selectedEnv, true)),
        Config.RandomShop(preferences.getBoolean(Config.RandomShop::class.java.name + selectedEnv, false)),
    )

    fun save(config: Set<Config>) {
        val edit = preferences.edit()
        config.forEach { type ->
            when (type) {
                is Config.Environment -> edit.putString(Config.Environment::class.java.name, (type.env as Enum<*>).name)
                is Config.MerchantId -> edit.putString(Config.MerchantId::class.java.name + selectedEnv, type.value)
                is Config.ClientId -> edit.putString(Config.ClientId::class.java.name + selectedEnv, type.value)
                is Config.ClientSecret -> edit.putString(Config.ClientSecret::class.java.name + selectedEnv, type.value)
                is Config.DeviceId -> edit.putString(Config.DeviceId::class.java.name + selectedEnv, type.value)
                is Config.HmacKey -> edit.putString(Config.HmacKey::class.java.name + selectedEnv, type.value)
                is Config.Theme -> edit.putBoolean(Config.Theme::class.java.name + selectedEnv, type.isLight)
                is Config.Scanner -> edit.putBoolean(Config.Scanner::class.java.name + selectedEnv, type.isScandit)
                is Config.RandomShop -> edit.putBoolean(Config.RandomShop::class.java.name + selectedEnv, type.isRandomShopOnStart)
            }
        }
        edit.apply()
    }

    fun setupEnvironment(env: SonectSDK.Config.Enviroment) {
        selectedEnv = env
    }

    fun getAppName(): String = context.packageName

    fun getSdkToken(): String {
        val clientId = getConfig().getSpecificConfig<Config.ClientId>().value
        val clientSecret = getConfig().getSpecificConfig<Config.ClientSecret>().value
        return Base64.encodeToString(
            "${clientId}:${clientSecret}".toByteArray(),
            Base64.DEFAULT
        )
            .replace("\n", "")
    }

    fun getSignature(): String {
        val merchantId = getConfig().getSpecificConfig<Config.MerchantId>().value
        val clientId = getConfig().getSpecificConfig<Config.ClientId>().value
        val hmacString = "${clientId}:${getAppName()}:$merchantId"
        return Base64.encodeToString(createHmac(hmacString.toByteArray()), Base64.DEFAULT).trim()
    }

    private fun createHmac(data: ByteArray): ByteArray {
        val hmacKey = getConfig().getSpecificConfig<Config.HmacKey>().value
        val keySpec = SecretKeySpec(hmacKey.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)

        val hmac = mac.doFinal(data)
        return hmac
    }

}

@SuppressLint("DefaultLocale")
private fun String?.toEnv(): SonectSDK.Config.Enviroment {
    val result = SonectSDK.Config.Enviroment.values().find {
        (it as Enum<*>).name.equals(this, ignoreCase = true)
    }
    return result ?: SonectSDK.Config.Enviroment.DEV
}

inline fun <reified T> Set<Config>.getSpecificConfig() : T {
    return find { it is T } as T
}

sealed class Config {
    data class MerchantId(val value: String): Config()
    data class ClientId(val value: String): Config()
    data class ClientSecret(val value: String): Config()
    data class DeviceId(val value: String): Config()
    data class HmacKey(val value: String): Config()
    data class Theme(val isLight: Boolean): Config()
    data class Scanner(val isScandit: Boolean): Config()
    data class Environment(val env: SonectSDK.Config.Enviroment): Config()
    data class RandomShop(val isRandomShopOnStart: Boolean): Config()
}