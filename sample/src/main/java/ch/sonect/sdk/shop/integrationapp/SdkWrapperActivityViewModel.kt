package ch.sonect.sdk.shop.integrationapp

import android.app.Application
import android.content.Context
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.sonect.sdk.shop.SonectSDK
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class SdkWrapperActivityViewModel(private val app: Application) : AndroidViewModel(app) {
    val state: LiveData<DataState>
        get() = _state

    private val configRepository: ConfigRepository = ConfigRepository(app.getSharedPreferences("SampleApp", Context.MODE_PRIVATE))

    private val _state: MutableLiveData<DataState> = MutableLiveData()

    private fun getClientId(): String? = (configRepository.get().find { it is Config.ClientId } as? Config.ClientId)?.value

    private fun getClientSecret(): String? = (configRepository.get().find { it is Config.ClientSecret } as? Config.ClientSecret)?.value

    private fun getHmacKey(): String? = (configRepository.get().find { it is Config.HmacKey } as? Config.HmacKey)?.value

    init {
        _state.value = DataState.LoadedConfig(configRepository.get(), calculateTokenSdk(), calculateSignature())
    }

    private fun calculateTokenSdk(): String {
        return Base64.encodeToString(
            "${getClientId()}:${getClientSecret()}".toByteArray(),
            Base64.DEFAULT
        )
            .replace("\n", "")
    }

    private fun calculateSignature(): String {
        val merchantId = (configRepository.get().find { it is Config.MerchantId } as? Config.MerchantId)?.value ?: ""
        val hmacString = "${getClientId()}:${app.packageName}:$merchantId"
        return Base64.encodeToString(createHmac(hmacString.toByteArray()), Base64.DEFAULT).trim()
    }

    private fun createHmac(data: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(getHmacKey()?.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)

        val hmac = mac.doFinal(data)
        return hmac
    }

    sealed class DataState {
        data class LoadedConfig(val data: Set<Config>, val token: String, val signature: String) : DataState()
    }
}