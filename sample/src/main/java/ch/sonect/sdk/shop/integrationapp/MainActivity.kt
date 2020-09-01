package ch.sonect.sdk.shop.integrationapp

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import ch.sonect.common.extension.afterTextChanged
import ch.sonect.sdk.shop.SonectSDK
import ch.sonect.sdk.shop.integrationapp.data.TestInfo
import kotlinx.android.synthetic.main.activity_main.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()
    private lateinit var testInfo: TestInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnStartSdkFragment.setOnClickListener {
            val signature = calculateSignature(testInfo.merchantId.orEmpty())
            viewModel.saveCurrentInfo(testInfo)

            SdkWrapperActivity.start(
                this,
                chkLight.isChecked,
                testInfo.merchantId.orEmpty(),
                chkScandit.isChecked,
                getTokenSDK(),
                testInfo.envKey,
                signature,
                if (testInfo.merchantId.isNullOrBlank()) null else testInfo.merchantId
            )
        }

        initObservers()
        viewModel.setContext(applicationContext)

        etMerchantId.addTextChangedListener(afterTextChanged {
            testInfo.merchantId = it.toString()
        })

        etClientId.addTextChangedListener(afterTextChanged {
            testInfo.clientId = it.toString()
        })

        etClientSecret.addTextChangedListener(afterTextChanged {
            testInfo.clientSecret = it.toString()
        })

        etHmacKey.addTextChangedListener(afterTextChanged {
            testInfo.hmacKey = it.toString()
        })

        etDeviceId.addTextChangedListener(afterTextChanged {
            testInfo.deviceId = it.toString()
        })

        groupEnviroment.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.chkDev -> viewModel.getDefaults(SonectSDK.Config.Enviroment.DEV)
                R.id.chkTest -> viewModel.getDefaults(SonectSDK.Config.Enviroment.STAGING)
                R.id.chkProd -> viewModel.getDefaults(SonectSDK.Config.Enviroment.PRODUCTION)
            }
        }

        copyAction.setOnClickListener {
            val clipboard = getSystemService<ClipboardManager>()
            val clip = ClipData.newPlainText("test info", viewModel.infoToClipBoard())
            clipboard?.primaryClip = clip
            Toast.makeText(applicationContext, "Info copied to clipboard!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun initObservers() {
        viewModel.currentInfo.observe(this, Observer {
            settingFields(it)
        })

        viewModel.defaultEnv.observe(this, Observer {
            settingFields(it)
        })
    }

    private fun settingFields(it: TestInfo) {
        testInfo = it
        etMerchantId.setText(it.merchantId)
        etClientId.setText(it.clientId)
        etClientSecret.setText(it.clientSecret)
        etHmacKey.setText(it.hmacKey)
        etDeviceId.setText(it.deviceId)

        when (it.envKey) {
            SonectSDK.Config.Enviroment.DEV -> chkDev.isChecked = true
            SonectSDK.Config.Enviroment.STAGING -> chkTest.isChecked = true
            SonectSDK.Config.Enviroment.PRODUCTION -> chkProd.isChecked = true
        }
    }

    private fun getTokenSDK(): String {
        return Base64.encodeToString(
            "${testInfo.clientId}:${testInfo.clientSecret}".toByteArray(), Base64.DEFAULT
        ).replace("\n", "")
    }

    private fun calculateSignature(uid: String): String {
        val hmacString = "${testInfo.clientId}:$packageName:$uid"
        return Base64.encodeToString(createHmac(hmacString.toByteArray()), Base64.DEFAULT).trim()
    }

    fun createHmac(data: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(testInfo.hmacKey?.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(keySpec)

        return mac.doFinal(data)
    }
}