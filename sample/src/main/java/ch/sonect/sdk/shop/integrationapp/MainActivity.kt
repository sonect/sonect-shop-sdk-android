package ch.sonect.sdk.shop.integrationapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ch.sonect.sdk.shop.SonectSDK
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel : MainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        groupEnviroment.setOnCheckedChangeListener { _, _ ->
            viewModel.changeEnvironment(getSelectedEnvironment())
        }

        viewModel.state.observe(this, Observer {
            when (it) {
                is MainActivityViewModel.DataState.SdkInitiation -> {
                    SdkWrapperActivity.start(this)
                }
                is MainActivityViewModel.DataState.LoadedConfig -> applyConfig(it.data, selectEnv = false)
                is MainActivityViewModel.DataState.InitialLoadedConfig -> applyConfig(it.data, selectEnv = true)
            }
        })

        btnStartSdkFragment.setOnClickListener {
            val configBuilder = ConfigSetBuilder()
            configBuilder.merchantId(etMerchantId.text.toString())
                .clientId(etClientId.text.toString())
                .clientSecret(etClientSecret.text.toString())
                .hmacKey(etHmacKey.text.toString())
                .deviceId(etDeviceId.text.toString())
                .isLightTheme(chkLight.isChecked)
                .isScanditScanner(chkScandit.isChecked)
                .environment(getSelectedEnvironment())
            viewModel.save(configBuilder.build())
        }

    }

    private fun getSelectedEnvironment(): SonectSDK.Config.Enviroment {
        if (chkDev.isChecked) return SonectSDK.Config.Enviroment.DEV
        if (chkTest.isChecked) return SonectSDK.Config.Enviroment.STAGING
        if (chkProd.isChecked) return SonectSDK.Config.Enviroment.PRODUCTION
        return SonectSDK.Config.Enviroment.DEV
    }


    private fun applyConfig(
        config: Set<Config>,
        selectEnv: Boolean
    ) {
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
            }
        }
    }

}