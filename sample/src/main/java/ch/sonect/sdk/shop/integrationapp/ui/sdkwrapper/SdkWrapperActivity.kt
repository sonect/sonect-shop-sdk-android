package ch.sonect.sdk.shop.integrationapp.ui.sdkwrapper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ch.sonect.common.navigation.ActivityResult
import ch.sonect.common.navigation.ActivityResultStorage
import ch.sonect.sdk.shop.EntryPointFragment
import ch.sonect.sdk.shop.SdkActionsCallback
import ch.sonect.sdk.shop.SonectSDK
import ch.sonect.sdk.shop.integrationapp.R
import ch.sonect.sdk.shop.integrationapp.data.Config
import ch.sonect.sdk.shop.integrationapp.data.getSpecificConfig
import ch.sonect.sdk.shop.integrationapp.ui.CustomScannerFragment
import ch.sonect.sdk.shop.integrationapp.ui.common.viewModelProvider
import kotlin.random.Random

private const val SCANDIT_CUSTOM_LICENSE_KEY = "AbUeehXGNMOiM+J7yjRp+AYiJm9gMkFCsSa1J/54o4T5YZKNmmGVEWYM8ZPbTjuCOnyM6IxzWsJ0Q0nefXNu83ha/5tjUWNKl1lGfmtlqhRed7QWXzpwLeFSIqudfCBbzVaL4nd+UFcDWfoQ5g7WAp8Ef2vHAHdShh2/7t7dkGZ2pg0/SzjyvgMV8AQpFFuFmm9BfUoqQG+T8Sjd534yfAP8r3S5q95m5WHxfo37vnV+kziFBtSti+bOH1xJraCR/lCPgbIuAGBd7IBXmla3u/AKZSrCCBzhOEtms7Ws/14PZ6bp7tC8RunvYRUXRNE0njYB//uo7zikp0qzfAMxR6cygW5KT98F5Vr+g7HDNaEV9y9cqp4OU8oaFDAEhBqFZmpsGddGr6NsPUi6aWAlTxzgtzAcy8tEv2+VIk0x6S5C0YaALnHbmgnjidswnarA4eIq3s+1t/M6RkRJ0oAOAm5+EAL5Xn+tmvig7r8AJ/tZgmkAuinJtLAmNOXffqFoQ+ImyFS5GD8s2FfPYjx9K+9VrFYiQ/zkSSzR5DhbCmcliy+beQcMFACTbrkfFL5q4u6S6qf1CCiVNyNMprzB/vUgW3uPtVotqQ7V8cVmil0bLOseV1fAm+igjTb9v7B0il2syJfMO50PDXM2GSy0qXnihntpdEm1MdscjwclUgiUSN17loFqkik78P/64dF5QLeR2i3/FX9EULkWT66MJUWwtv2yuuxJarRrYK9iy+4hHTfyON3BJ88g8JDL0j5DkQF5K01wtlDURazyjgypQSNDWwrtG8sxgqumWMd3nrswVlxefYCKwbDV0SPnAbLunjvCqPU8nKP9+A=="

private val CHARS = ('A'..'Z') + ('a'..'z')
private val PICS = setOf(
    "https://sonect.net/wp-content/uploads/2020/02/anytime.png",
    "https://sonect.net/wp-content/uploads/2020/10/Win_Win_Icon-1.png",
    "https://sonect.net/wp-content/uploads/2020/02/Send.png",
    "https://sonect.net/wp-content/uploads/2020/02/No-risk.png"
)

class SdkWrapperActivity : AppCompatActivity(), ActivityResultStorage {

    private val pendingResults = mutableMapOf<Int, ActivityResult>()

    private val viewModel by viewModelProvider<SdkWrapperActivityViewModel> {
        SdkWrapperActivityViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wrapper)

        viewModel.state.observe(this, Observer {
            when (it) {
                is SdkWrapperActivityViewModel.DataState.LoadedConfig -> {
                    startSdk(it)
                }
            }
        })
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

    private fun startSdk(config: SdkWrapperActivityViewModel.DataState.LoadedConfig) {
        val outerData = config.data
        val token = config.token
        val sig = config.signature

        val builder: SonectSDK.Config.Builder = SonectSDK.Config.Builder()

        val env = outerData.getSpecificConfig<Config.Environment>().env
        val merchantId = outerData.getSpecificConfig<Config.MerchantId>().value
        val deviceId = outerData.getSpecificConfig<Config.DeviceId>().value

        val configBuilder = builder
            .enviroment(env)
            .userCredentials(
                SonectSDK.Config.UserCredentials(
                    merchantId,
                    token,
                    signature = sig,
                    device_id = deviceId
                )
            )
            .sdkCallbacks(object : SdkActionsCallback {
                override fun comparePasscode(passcode: String): Boolean {
                    return true
                }

                override fun onSdkLastFragmentClosed() {
                    finish()
                }

                override fun onTermsAccepted() {
                    Log.e("!@#", "T&C Accepted")
                }

                override fun onShopOnboardingComplete() {
                    Log.e("!@#", "Shop onboarding completed")
                }
            })

        val isScandit = outerData.getSpecificConfig<Config.Scanner>().isScandit
        if (isScandit) {
            configBuilder.customScanditKey(SCANDIT_CUSTOM_LICENSE_KEY)
        } else {
            configBuilder.customScannerFragment(CustomScannerFragment())
        }

        val isLight = outerData.getSpecificConfig<Config.Theme>().isLight

        if (isLight) {
            configBuilder.setLightTheme()
        }

        val isRandomShopOnStart = outerData.getSpecificConfig<Config.RandomShop>().isRandomShopOnStart
        if (isRandomShopOnStart) {
            val shop = generateRandomShop()
            Log.i("Random shop", shop.toString())
            configBuilder.shop(shop)
        }

        val isRandomBeneficiary = outerData.getSpecificConfig<Config.RandomBeneficiary>().isRandomBeneficiary
        if (isRandomBeneficiary) {
            val beneficiary = generateRandomBeneficiary()
            Log.i("Random beneficiary", beneficiary.toString())
            configBuilder.beneficiary(beneficiary)
        }

        val additionalData = outerData.getSpecificConfig<Config.AdditionalData>().value
        // Data is in a format "key1=value1;key2=value2..."
        val params = additionalData.split(";").associate {
            if (it.isBlank()) return@associate "" to ""
            val (key, value) = it.split("=")
            key to value
        }.filterKeys { it.isNotBlank() }
        if (params.isNotEmpty()) configBuilder.additionalParams(params)

        val config = configBuilder.build()
        val sonectSDK = SonectSDK(
            this,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, sonectSDK.getStartFragment())
            .addToBackStack(null).commit()
    }

    private fun generateRandomShop(): SonectSDK.Config.Shop = SonectSDK.Config.Shop(
        name = randomString(),
        minHandout = Random.nextInt(10, 100),
        maxHandout = Random.nextInt(101, 500),
        pictureUrl = PICS.random(),
        address = SonectSDK.Config.Address(
            address1 = randomString(),
            address2 = randomString(),
            city = randomString(),
            zipCode = Random.nextInt(1000, 10000).toString(),
            country = randomString()
        ),
        openHours = SonectSDK.Config.OpeningHours(
            monday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours())),
            tuesday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours())),
            wednesday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours())),
            thursday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours())),
            friday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours())),
            saturday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours())),
            sunday = listOf(SonectSDK.Config.OpenCloseTime(getRandomCloseHours(), getRandomOpenHours()))
        )
    )

    private fun generateRandomBeneficiary(): SonectSDK.Config.Beneficiary = SonectSDK.Config.Beneficiary(
        fullName = randomString(),
        address = randomAddress(),
        payoutBankAccountNumber = "IT60X0542811101000000123456",
        email = "${randomString()}@{${randomString()}.com"
    )

    private fun randomAddress(): SonectSDK.Config.Address = SonectSDK.Config.Address(
        address1 = randomString(),
        city = randomString(),
        zipCode = Random.nextInt(1000, 10000).toString(),
        country = randomString()
    )

    private fun randomString() = (1..10).map { CHARS.random() }.joinToString(separator = "")

    private fun getRandomOpenHours(): String = "${Random.nextInt(0, 10)}:${Random.nextInt(0, 59)}"

    private fun getRandomCloseHours(): String = "${Random.nextInt(11, 23)}:${Random.nextInt(0, 59)}"

    companion object {
        fun start(activity: Activity) {
            val newActivity = Intent(activity, SdkWrapperActivity::class.java)
            activity.startActivity(newActivity)
        }
    }

}