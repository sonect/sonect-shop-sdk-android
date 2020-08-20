package ch.sonect.sdk.shop.integrationapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ch.sonect.common.extension.afterTextChanged
import ch.sonect.sdk.shop.SonectSDK
import kotlinx.android.synthetic.main.activity_main.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { CacheManager(this) }

    // Id should be some value unique and constant for single user
    var _merchantId = ""
    var _clientId = ""
    var _clientSecret = ""
    var _deviceId = ""
    var _hmacKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnStartSdkFragment.setOnClickListener {
            _merchantId = etMerchantId.text.toString()
            _clientId = etClientId.text.toString()
            _clientSecret = etClientSecret.text.toString()
            _deviceId = etDeviceId.text.toString()
            _hmacKey = etHmacKey.text.toString()

            val signature = calculateSignature(_merchantId)

            SdkWrapperActivity.start(
                this,
                chkLight.isChecked,
                _merchantId,
                chkScandit.isChecked,
                getTokenSDK(),
                getSelectedEnviroment(),
                signature,
                if (_deviceId.isBlank()) null else _deviceId
            )
        }

        _merchantId = getDefaultMerchantId()
        etMerchantId.setText(_merchantId)
        etMerchantId.addTextChangedListener(afterTextChanged {
            sharedPreferences.merchantId = it.toString()
        })

        _clientId = getClientId()
        etClientId.setText(_clientId)
        etClientId.addTextChangedListener(afterTextChanged {
            sharedPreferences.clientId = it.toString()
        })

        _clientSecret = getClientSecret()
        etClientSecret.setText(_clientSecret)
        etClientSecret.addTextChangedListener(afterTextChanged {
            sharedPreferences.clientSecret = it.toString()
        })

        _hmacKey = getHmacKey()
        etHmacKey.setText(_hmacKey)
        etHmacKey.addTextChangedListener(afterTextChanged {
            sharedPreferences.hmacKey = it.toString()
        })

        _deviceId = getDeviceId()
        etDeviceId.setText(_deviceId)
        etDeviceId.addTextChangedListener(afterTextChanged {
            sharedPreferences.deviceId = it.toString()
        })

        when (sharedPreferences.envKey) {
            "DEV" -> chkDev.isChecked = true
            "TEST" -> chkTest.isChecked = true
            "PROD" -> chkProd.isChecked = true
        }

        groupEnviroment.setOnCheckedChangeListener { group, checkedId ->
            if (chkDev.isChecked) sharedPreferences.envKey = "DEV"
            if (chkTest.isChecked) sharedPreferences.envKey = "TEST"
            if (chkProd.isChecked) sharedPreferences.envKey = "PROD"
        }

        copyAction.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("test info", sharedPreferences.copiedInfo())
            clipboard.primaryClip = clip
            Toast.makeText(applicationContext, "Info copied to clipboard!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getTokenSDK(): String {
        return Base64.encodeToString(
            "${_clientId}:${_clientSecret}".toByteArray(),
            Base64.DEFAULT
        )
            .replace("\n", "")
    }

    private fun getClientId(): String {
        return sharedPreferences.clientId ?: ""
    }

    private fun getClientSecret(): String {
        return sharedPreferences.clientSecret ?: ""
    }

    private fun getHmacKey(): String {
        return sharedPreferences.hmacKey ?: ""
    }

    private fun getDefaultMerchantId(): String {
        return sharedPreferences.merchantId ?: ""
    }

    private fun getDeviceId(): String {
        return sharedPreferences.deviceId ?: ""
    }

    private fun getSelectedEnviroment(): SonectSDK.Config.Enviroment {
        when (sharedPreferences.envKey) {
            "DEV" -> return SonectSDK.Config.Enviroment.DEV
            "TEST" -> return SonectSDK.Config.Enviroment.STAGING
            "PROD" -> return SonectSDK.Config.Enviroment.PRODUCTION
            else -> throw IllegalStateException("Environment have not been selected yet")
        }
    }

    private fun calculateSignature(uid: String): String {
        val hmacString = "${_clientId}:$packageName:$uid"
        return Base64.encodeToString(createHmac(hmacString.toByteArray()), Base64.DEFAULT).trim()
    }

    fun createHmac(data: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(_hmacKey.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)

        val hmac = mac.doFinal(data)
        return hmac
    }
}