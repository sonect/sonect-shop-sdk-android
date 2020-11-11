package ch.sonect.sdk.shop.integrationapp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import ch.sonect.sdk.shop.SonectSDK
import java.io.Serializable

class ConfigRepository(private val preferences: SharedPreferences) {

    private var selectedEnv: SonectSDK.Config.Enviroment

    init {
        val storedConfig = get()
        val env = (storedConfig.find { it is Config.Environment } as? Config.Environment)?.env ?: SonectSDK.Config.Enviroment.DEV
        selectedEnv = env
    }

    fun clear(): Set<Config> {
        preferences.edit().clear().apply()
        return get()
    }

    fun get(): Set<Config> = setOf(
        Config.MerchantId(preferences.getString(Config.MerchantId::class.java.name + selectedEnv, "") ?: ""),
        Config.ClientId(preferences.getString(Config.ClientId::class.java.name + selectedEnv, "") ?: ""),
        Config.ClientSecret(preferences.getString(Config.ClientSecret::class.java.name + selectedEnv, "") ?: ""),
        Config.DeviceId(preferences.getString(Config.DeviceId::class.java.name + selectedEnv, "") ?: ""),
        Config.HmacKey(preferences.getString(Config.HmacKey::class.java.name + selectedEnv, "") ?: ""),
        Config.Theme(preferences.getBoolean(Config.Theme::class.java.name + selectedEnv, false)),
        Config.Scanner(preferences.getBoolean(Config.Scanner::class.java.name + selectedEnv, true)),
        Config.Environment(preferences.getString(Config.Environment::class.java.name, "").toEnv())
    )

    fun save(config: Set<Config>) {
        val edit = preferences.edit()
        config.forEach { type ->
            when (type) {
                is Config.MerchantId -> edit.putString(Config.MerchantId::class.java.name + selectedEnv, type.value)
                is Config.ClientId -> edit.putString(Config.ClientId::class.java.name + selectedEnv, type.value)
                is Config.ClientSecret -> edit.putString(Config.ClientSecret::class.java.name + selectedEnv, type.value)
                is Config.DeviceId -> edit.putString(Config.DeviceId::class.java.name + selectedEnv, type.value)
                is Config.HmacKey -> edit.putString(Config.HmacKey::class.java.name + selectedEnv, type.value)
                is Config.Theme -> edit.putBoolean(Config.Theme::class.java.name + selectedEnv, type.isLight)
                is Config.Scanner -> edit.putBoolean(Config.Scanner::class.java.name + selectedEnv, type.isScandit)
                is Config.Environment -> edit.putString(Config.Environment::class.java.name, type.env.name)
            }
        }
        edit.apply()
    }

    fun setupEnvironment(env: SonectSDK.Config.Enviroment) {
        selectedEnv = env
    }

}

@SuppressLint("DefaultLocale")
private fun String?.toEnv(): SonectSDK.Config.Enviroment {
    val result = SonectSDK.Config.Enviroment.values().find {
        it.name.equals(this, ignoreCase = true)
    }
    return result ?: SonectSDK.Config.Enviroment.DEV
}

sealed class Config {
    data class MerchantId(val value: String): Config(), Serializable
    data class ClientId(val value: String): Config(), Serializable
    data class ClientSecret(val value: String): Config(), Serializable
    data class DeviceId(val value: String): Config(), Serializable
    data class HmacKey(val value: String): Config(), Serializable
    data class Theme(val isLight: Boolean): Config(), Serializable
    data class Scanner(val isScandit: Boolean): Config(), Serializable
    data class Environment(val env: SonectSDK.Config.Enviroment): Config(), Serializable
}