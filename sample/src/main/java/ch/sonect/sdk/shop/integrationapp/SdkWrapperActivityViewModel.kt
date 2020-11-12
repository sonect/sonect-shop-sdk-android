package ch.sonect.sdk.shop.integrationapp

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class SdkWrapperActivityViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    val state: LiveData<DataState>
        get() = _state

    private val _state: MutableLiveData<DataState> = MutableLiveData()

    init {
        _state.value = DataState.LoadedConfig(configRepository.get(), calculateTokenSdk(), calculateSignature())
    }

    private fun getClientId(): String? = (configRepository.get().find { it is Config.ClientId } as? Config.ClientId)?.value

    private fun getClientSecret(): String? = (configRepository.get().find { it is Config.ClientSecret } as? Config.ClientSecret)?.value

    private fun getHmacKey(): String? = (configRepository.get().find { it is Config.HmacKey } as? Config.HmacKey)?.value

    private fun calculateTokenSdk(): String {
        return Base64.encodeToString(
            "${getClientId()}:${getClientSecret()}".toByteArray(),
            Base64.DEFAULT
        )
            .replace("\n", "")
    }

    private fun calculateSignature(): String {
        val merchantId = (configRepository.get().find { it is Config.MerchantId } as? Config.MerchantId)?.value ?: ""
        val hmacString = "${getClientId()}:${configRepository.getAppName()}:$merchantId"
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