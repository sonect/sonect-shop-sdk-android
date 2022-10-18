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

private const val SCANDIT_CUSTOM_LICENSE_KEY = "AdUiiRgQPeHNJN3llj2kzyI/AtdVI/TPOwOvQOpT96LqeeF90RaObYI9hbAPcNZLUx6KkNIoVIhlIWmWU22StPUArYqFWWWzMVVABs0iLPazb3kDlQCqzfdsNGjkQ3n7MWEHcQBEJ94LcWK+iTT5LUQ0FKigD+AKKi4qp1Zv6+M9VIZDYp4i4BRbxn1+IZ0ZNi9xCoEKS+OzIuVGJNHeQYVZAG0PmDQ6txFniz34nhkllyBNv9Cm+BIaWiqOObbJf9pRl6nVU/YVFqmk9IYidH3Q/CK2uZaSHRe0VZy1ca9TDpQTYV9/DJcGX0jJ85ASkOhjr4/0kh1qeNfKOtrzTsJR2tdH/confDzipKh+EO3iLUedBUMHjflIyH9xQvf/3Y/jV1sFvs2a5HP9qam2s7UQnHGA7/kH15F32Ws0DNB6TG3axFMlIn0RJcsNZJkSAFfZdf/C1r2ksx9ShNhB8dkcQvC/L4oIpMGYgoBQCY5WTq/dow4RbBqhvwocADeYrtKjEzznw2j7T9iHe50ImMEjG6akcOl2EQ+rQQGGhBZ9WIR3IChLTuh3piOBSLoGaR/OxDPzuZg5vq6d32rWrazUVVTNEb9P6DZOgxdv54wKoyVosNsXP1xUba4Paj9EAt7kcVNLr9VkoiDt9jP673CHrM+FC8CKFJWzC4UIQLstPLO9jK/f3lAp2IpUWsD8BcjaNPiXld/A0ptKl3R/K540wOARCX69ts7eZMc3nJ9sOlY1IIlUkO/13Sdpj9lgUkUmOvt3O3wNdWuwYaWnHA+flV2Q9wNd/If9Eh9tmb/+gvbAE1467/xPPtyQRHvK7JTM5EItxLnQtsI="

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
            country = randomString(),
            countryCode = "CH",
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
        country = randomString(),
        countryCode = "CH",
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