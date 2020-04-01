package ch.sonect.sdk.shop.integrationapp

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import ch.sonect.sdk.shop.SonectSDK
import kotlinx.android.synthetic.main.activity_main.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    // Id should be some value unique and constant for single user
    var _merchantId = ""
    var _clientId = ""
    var _clientSecret = ""
    var _deviceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnStartSdkFragment.setOnClickListener {
            _merchantId = etMerchantId.text.toString()

            val signature = calculateSignature(_merchantId)

            SdkWrapperActivity.start(
                this,
                chkLight.isChecked,
                _merchantId,
                chkScandit.isChecked,
                getTokenSDK(),
                getSelectedEnviroment(),
                signature
            )
        }

        _merchantId = getDefaultMerchantId()
        etMerchantId.setText(_merchantId)

        _clientId = getClientId()
        etClientId.setText(_clientId)

        _clientSecret = getClientSecret()
        etClientSecret.setText(_clientSecret)

        _deviceId = "1"
        etDeviceId.setText(_deviceId)

        groupEnviroment.setOnCheckedChangeListener { group, checkedId ->
            _merchantId = getDefaultMerchantId()
            etMerchantId.setText(_merchantId)
        }

    }

    private fun getTokenSDK(): String {
        return Base64.encodeToString(
            "${_clientId}:${_clientSecret}".toByteArray(),
            Base64.DEFAULT
        )
            .replace("\n", "")
    }

    fun getClientId(): String {
        return when (getSelectedEnviroment()) {
            SonectSDK.Config.Enviroment.DEV -> "5c323120-5027-11e8-ad3f-7be7c251fc61"
            SonectSDK.Config.Enviroment.STAGING -> "08828a10-bdaf-11e9-be4c-5db5328cafa4"
            SonectSDK.Config.Enviroment.PRODUCTION -> ""
        }
    }

    fun getClientSecret(): String {
        return when (getSelectedEnviroment()) {
            SonectSDK.Config.Enviroment.DEV -> "b64407b409abbc4269771cbd1f7c28dbd498270defff3a606f5f4f2d27a4e07a"
            SonectSDK.Config.Enviroment.STAGING -> "c999d5adab9b065b166bce6e58b84050349088ab8e7948248088068c7c534f60"
            SonectSDK.Config.Enviroment.PRODUCTION -> ""
        }
    }

    fun getHmacKey(): String {
        return when (getSelectedEnviroment()) {
            SonectSDK.Config.Enviroment.DEV -> "0a4f1c697751b6a3fbf533eeb81752426928acfe202bdd256a76d1a205907d70"
            SonectSDK.Config.Enviroment.STAGING -> ""
            SonectSDK.Config.Enviroment.PRODUCTION -> ""
        }
    }

    fun getDefaultMerchantId(): String {
        return when (getSelectedEnviroment()) {
            SonectSDK.Config.Enviroment.DEV -> "800801"
            SonectSDK.Config.Enviroment.STAGING -> "A1MrFAOjZ24YQJHexSrlC3yskOOuGS"
            SonectSDK.Config.Enviroment.PRODUCTION -> ""
        }
    }

    private fun getSelectedEnviroment(): SonectSDK.Config.Enviroment {
        if (chkDev.isChecked) return SonectSDK.Config.Enviroment.DEV
        if (chkTest.isChecked) return SonectSDK.Config.Enviroment.STAGING
        if (chkProd.isChecked) return SonectSDK.Config.Enviroment.PRODUCTION
        throw IllegalStateException("Environment have not been selected yet")
    }

    private fun calculateSignature(uid: String): String {
        val hmacString = "${getClientId()}:$packageName:$uid"
        return Base64.encodeToString(createHmac(hmacString.toByteArray()), Base64.DEFAULT).trim()
    }

    fun createHmac(data: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(getHmacKey().toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)

        val hmac = mac.doFinal(data)
        return hmac
    }
}