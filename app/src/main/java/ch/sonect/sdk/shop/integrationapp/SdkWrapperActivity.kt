package ch.sonect.sdk.shop.integrationapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ch.sonect.sdk.shop.ActivityResultHandlingFragment
import ch.sonect.sdk.shop.EntryPointFragment
import ch.sonect.sdk.shop.SdkActionsCallback
import ch.sonect.sdk.shop.SonectSDK

class SdkWrapperActivity : AppCompatActivity() {

    companion object {
        const val LM = "lm"
        const val UID = "uid"
        const val TSDK = "toksdk"
        const val SIGN = "signature"
        const val SCANDIT = "scandit"
        const val DEVICE_ID = "devId"
        const val COUNTRY_CODE_ID = "countCod"

        const val SCANDIT_CUSTOM_LICENSE_KEY = "Af7O+WkxSKInN6dNTCx/1VQVFERGB4fVhCqgBSlYhAG4aaMH20MkhwEmJoLVTtUJB2BRh4JB4wpZMEZ7umLb+6giYwWARXDpVnKXRthSSZNueDQXtxaYmUtSyNMYLMWdwjeUFicrsE9ea6d03ww/TQMgs303FSckQSHRH8dBRccWfy3cuN49UaJpXGS49SBQrStQIGj1p2mw2Y9FmMx7EdY3AkPxa7/9aPQcqDW7yhORsAP0zXzYE21oyfD+5g+mQzYVxn5/70qYBZNS/970MWpRJdbCVPuBrz/aDPxj5tV71OyVWrO5pVXtIJd73lkfoIvMltGi+0JD7NiiAgTO1TYaaAErHJRr3PEDe5pYzwIksGHgshtXZonUNF6DFYUscBEwWRvB1ODcog5Lt8MUamLOIHQ2Cru/4gBNZ1bq6BfJt6duBDi4YZnxdXW5bSuXxX+Kz0oQDQ5TCRs096COutR24PzpZL3InwL7iwvsKQ/jvjFH5SRGq0ojbuCJY3lXTL3P89S5AsvwOWSuUvC3bhqSLwPPuKkK3UoRAB1JdT/8DHeedGWerdd2YSwjj8Oe0mmNlVnG8s9Vb1ihGxYMDID9IM1eTG6nbWQlrwz6cSWUVHO4GkyRGAWKGcsR+1tE3cC3880+s2R0YBislBAk/nuADk/MozJqNT/88b8yojs/MO7/fMWeFkK+Pn5qxWpfYu2K+9RZNE+YSE1XNGlPS+hjSvBpbjoEU/beXrxExwFNP8+bZDhP6Ks1BbAZeVwgrK8y3gYCG4+DzKQu48ckDgZ/xcMGOE0XW7ZUkbbitlMmuEmkGCyHEPTQ+c0zXhkbceLILQLXxQ=="

        internal const val ENV = "enviroment"

        fun start(
            activity: Activity, lightMode: Boolean, userId: String,
            isScandit: Boolean,
            tokenSDK: String,
            environment: SonectSDK.Config.Enviroment,
            signature: String,
            countryCode: String,
            deviceId: String?
        ) {
            val newActivity = Intent(activity, SdkWrapperActivity::class.java)
            newActivity.putExtra(LM, lightMode)
            newActivity.putExtra(UID, userId)
            newActivity.putExtra(TSDK, tokenSDK)
            newActivity.putExtra(ENV, environment)
            newActivity.putExtra(SCANDIT, isScandit)
            newActivity.putExtra(SIGN, signature)
            newActivity.putExtra(DEVICE_ID, deviceId)
            newActivity.putExtra(COUNTRY_CODE_ID, countryCode)
            activity.startActivity(newActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrapper)

        val builder: SonectSDK.Config.Builder = SonectSDK.Config.Builder()
        val configBuilder = builder
            .enviroment(intent.getSerializableExtra(ENV) as SonectSDK.Config.Enviroment)
            .userCredentials(
                SonectSDK.Config.UserCredentials(
                    intent.getStringExtra(UID),
                    intent.getStringExtra(TSDK),
                    signature = intent.getStringExtra(SIGN),
                    device_id = intent.getStringExtra(DEVICE_ID),
                    countryCode = intent.getStringExtra(COUNTRY_CODE_ID)
                )
            )
            .sdkCallbacks(object : SdkActionsCallback {
                override fun onSdkLastFragmentClosed() {
                    finish()
                }

                override fun onTermsAccepted() {
                    Log.e("!@#","T&C Accepted")
                }
            })

        if (intent.getBooleanExtra(SCANDIT, true)) {
            configBuilder.customScanditKey(SCANDIT_CUSTOM_LICENSE_KEY)
        } else {
            configBuilder.customScannerFragment(CustomScannerFragment())
        }

        if (intent.getBooleanExtra(LM, false)) {
            // Light theme is not supported yet
//            configBuilder.setLightTheme()
        }
        val config = configBuilder.build()
        val sonectSDK = SonectSDK(
            this,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, sonectSDK.getStartFragment())
            .addToBackStack(null).commit()
    }

    override fun onBackPressed() {
        var backIsHandled = false
        for (fragment in supportFragmentManager.fragments) {
            if ((fragment as? EntryPointFragment)?.handleBack() == true) {
                // Handle back by ourselfs, SDK won't handle it anymore
                backIsHandled = true
            }
        }
        if (!backIsHandled) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is ActivityResultHandlingFragment) {
                fragment.onHostedActivityResult(requestCode, resultCode, data)
            } else {
                fragment?.onActivityResult(requestCode, resultCode, data)
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}