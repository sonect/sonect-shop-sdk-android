package ch.sonect.sdk.shop.integrationapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ch.sonect.common.navigation.ActivityResult
import ch.sonect.common.navigation.ActivityResultStorage
import ch.sonect.sdk.shop.EntryPointFragment
import ch.sonect.sdk.shop.SdkActionsCallback
import ch.sonect.sdk.shop.SonectSDK

class SdkWrapperActivity : AppCompatActivity(), ActivityResultStorage {

    private val pendingResults = mutableMapOf<Int, ActivityResult>()

    companion object {
        const val LM = "lm"
        const val UID = "uid"
        const val TSDK = "toksdk"
        const val SIGN = "signature"
        const val SCANDIT = "scandit"
        const val DEVICE_ID = "devId"

        const val SCANDIT_CUSTOM_LICENSE_KEY = "AbUeehXGNMOiM+J7yjRp+AYiJm9gMkFCsSa1J/54o4T5YZKNmmGVEWYM8ZPbTjuCOnyM6IxzWsJ0Q0nefXNu83ha/5tjUWNKl1lGfmtlqhRed7QWXzpwLeFSIqudfCBbzVaL4nd+UFcDWfoQ5g7WAp8Ef2vHAHdShh2/7t7dkGZ2pg0/SzjyvgMV8AQpFFuFmm9BfUoqQG+T8Sjd534yfAP8r3S5q95m5WHxfo37vnV+kziFBtSti+bOH1xJraCR/lCPgbIuAGBd7IBXmla3u/AKZSrCCBzhOEtms7Ws/14PZ6bp7tC8RunvYRUXRNE0njYB//uo7zikp0qzfAMxR6cygW5KT98F5Vr+g7HDNaEV9y9cqp4OU8oaFDAEhBqFZmpsGddGr6NsPUi6aWAlTxzgtzAcy8tEv2+VIk0x6S5C0YaALnHbmgnjidswnarA4eIq3s+1t/M6RkRJ0oAOAm5+EAL5Xn+tmvig7r8AJ/tZgmkAuinJtLAmNOXffqFoQ+ImyFS5GD8s2FfPYjx9K+9VrFYiQ/zkSSzR5DhbCmcliy+beQcMFACTbrkfFL5q4u6S6qf1CCiVNyNMprzB/vUgW3uPtVotqQ7V8cVmil0bLOseV1fAm+igjTb9v7B0il2syJfMO50PDXM2GSy0qXnihntpdEm1MdscjwclUgiUSN17loFqkik78P/64dF5QLeR2i3/FX9EULkWT66MJUWwtv2yuuxJarRrYK9iy+4hHTfyON3BJ88g8JDL0j5DkQF5K01wtlDURazyjgypQSNDWwrtG8sxgqumWMd3nrswVlxefYCKwbDV0SPnAbLunjvCqPU8nKP9+A=="

        internal const val ENV = "enviroment"

        fun start(
            activity: Activity, lightMode: Boolean, userId: String,
            isScandit: Boolean,
            tokenSDK: String,
            environment: SonectSDK.Config.Enviroment,
            signature: String,
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
                    device_id = intent.getStringExtra(DEVICE_ID)
                )
            )
            .sdkCallbacks(object : SdkActionsCallback {
                override fun onSdkLastFragmentClosed() {
                    finish()
                }

                override fun onTermsAccepted() {
                    Log.e("!@#","T&C Accepted")
                }

                override fun onShopOnboardingComplete() {
                    Log.e("!@#","Shop onboarding completed")
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

    override fun addNavigationResult(requestCode: Int, result: ActivityResult) {
        pendingResults[requestCode] = result
    }

    override fun getPendingResult(requestCode: Int) = pendingResults.remove(requestCode)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        addNavigationResult(requestCode, ActivityResult(resultCode, data))

        val topFragment = supportFragmentManager.let { it.fragments[it.fragments.size - 1] }
        topFragment?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    
}