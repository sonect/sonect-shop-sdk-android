package ch.sonect.sdk.shop.integrationapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ch.sonect.sdk.shop.SonectSDK
import ch.sonect.sdk.shop.integrationapp.R
import ch.sonect.sdk.shop.integrationapp.data.Config
import ch.sonect.sdk.shop.integrationapp.ui.common.viewModelProvider
import ch.sonect.sdk.shop.integrationapp.ui.sdkwrapper.SdkWrapperActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModelProvider<MainActivityViewModel> {
        MainActivityViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        groupEnviroment.setOnCheckedChangeListener { _, _ ->
            viewModel.changeEnvironment(getSelectedEnvironment())
        }

        viewModel.state.observe(this, Observer {
            when (it) {
                is MainActivityViewModel.DataState.SdkInitiation -> {
                    SdkWrapperActivity.start(this)
                }
                is MainActivityViewModel.DataState.LoadedConfig -> applyConfig(
                    it.data,
                    selectEnv = false
                )
                is MainActivityViewModel.DataState.InitialLoadedConfig -> applyConfig(
                    it.data,
                    selectEnv = true
                )
            }
        })

        btnStartSdkFragment.setOnClickListener {
            val config = prepareConfig(
                merchantId = etMerchantId.text.toString(),
                clientId = etClientId.text.toString(),
                clientSecret = etClientSecret.text.toString(),
                hmacKey = etHmacKey.text.toString(),
                deviceId = etDeviceId.text.toString(),
                isLightTheme = chkLight.isChecked,
                isScandit = chkScandit.isChecked,
                environment = getSelectedEnvironment(),
                isRandomShopApplied = randomShopYesCheckbox.isChecked,
                isRandomBeneficiaryApplied = randomBenYesCheckbox.isChecked,
                additionalData = etAdditionalData.text.toString()
            )
            viewModel.save(config)
        }

    }

    private fun prepareConfig(
        merchantId: String,
        clientId: String,
        clientSecret: String,
        hmacKey: String,
        deviceId: String,
        isLightTheme: Boolean,
        isScandit: Boolean,
        environment: SonectSDK.Config.Enviroment,
        isRandomShopApplied: Boolean,
        isRandomBeneficiaryApplied: Boolean,
        additionalData: String
    ): Set<Config> = setOf(
        Config.MerchantId(merchantId),
        Config.ClientId(clientId),
        Config.ClientSecret(clientSecret),
        Config.DeviceId(deviceId),
        Config.HmacKey(hmacKey),
        Config.Theme(isLightTheme),
        Config.Scanner(isScandit),
        Config.Environment(environment),
        Config.RandomShop(isRandomShopApplied),
        Config.RandomBeneficiary(isRandomBeneficiaryApplied),
        Config.AdditionalData(additionalData)
    )

    private fun getSelectedEnvironment(): SonectSDK.Config.Enviroment {
        if (chkDev.isChecked) return SonectSDK.Config.Enviroment.DEV
        if (chkTest.isChecked) return SonectSDK.Config.Enviroment.STAGING
        if (chkProd.isChecked) return SonectSDK.Config.Enviroment.PRODUCTION
        return SonectSDK.Config.Enviroment.DEV
    }

    private fun applyConfig(config: Set<Config>, selectEnv: Boolean) {
        config.forEach {
            when (it) {
                is Config.MerchantId -> etMerchantId.setText(it.value)
                is Config.ClientId -> etClientId.setText(it.value)
                is Config.ClientSecret -> etClientSecret.setText(it.value)
                is Config.DeviceId -> etDeviceId.setText(it.value)
                is Config.HmacKey -> etHmacKey.setText(it.value)
                is Config.Environment -> {
                    if (!selectEnv) return@forEach
                    when (it.env) {
                        SonectSDK.Config.Enviroment.DEV -> chkDev.isChecked = true
                        SonectSDK.Config.Enviroment.STAGING -> chkTest.isChecked = true
                        SonectSDK.Config.Enviroment.PRODUCTION -> chkProd.isChecked = true
                    }
                }
                is Config.Theme -> {
                    chkLight.isChecked = it.isLight
                    chkDark.isChecked = !it.isLight
                }
                is Config.Scanner -> {
                    chkScandit.isChecked = it.isScandit
                    chkOwnScanner.isChecked = !it.isScandit
                }
                is Config.RandomShop -> {
                    randomShopYesCheckbox.isChecked = it.isRandomShopOnStart
                    randomShopNoCheckbox.isChecked = !it.isRandomShopOnStart
                }
                is Config.RandomBeneficiary -> {
                    randomBenYesCheckbox.isChecked = it.isRandomBeneficiary
                    randomBenNoCheckbox.isChecked = !it.isRandomBeneficiary
                }
                is Config.AdditionalData -> etAdditionalData.setText(it.value)
            }
        }
    }

}