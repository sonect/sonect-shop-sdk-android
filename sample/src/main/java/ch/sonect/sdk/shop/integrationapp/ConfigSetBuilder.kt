package ch.sonect.sdk.shop.integrationapp

import ch.sonect.sdk.shop.SonectSDK

class ConfigSetBuilder {

    private var merchantId: String? = null
    private var clientId: String? = null
    private var clientSecret: String? = null
    private var hmacKey: String? = null
    private var deviceId: String? = null
    private var isLightTheme: Boolean = false
    private var isScanditScanner: Boolean = false
    private var environment: SonectSDK.Config.Enviroment = SonectSDK.Config.Enviroment.DEV

    fun merchantId(value: String): ConfigSetBuilder {
        this.merchantId = value
        return this
    }

    fun clientId(value: String): ConfigSetBuilder {
        this.clientId = value
        return this
    }

    fun clientSecret(value: String): ConfigSetBuilder {
        this.clientSecret = value
        return this
    }

    fun hmacKey(value: String): ConfigSetBuilder {
        this.hmacKey = value
        return this
    }

    fun deviceId(value: String): ConfigSetBuilder {
        this.deviceId = value
        return this
    }

    fun isLightTheme(value: Boolean): ConfigSetBuilder {
        this.isLightTheme = value
        return this
    }

    fun isScanditScanner(value: Boolean): ConfigSetBuilder {
        this.isScanditScanner = value
        return this
    }

    fun environment(value: SonectSDK.Config.Enviroment): ConfigSetBuilder {
        this.environment = value
        return this
    }

    fun build(): Set<Config> = setOf(
        Config.MerchantId(merchantId ?: ""),
        Config.ClientId(clientId ?: ""),
        Config.ClientSecret(clientSecret ?: ""),
        Config.DeviceId(deviceId ?: ""),
        Config.HmacKey(hmacKey ?: ""),
        Config.Theme(isLightTheme),
        Config.Scanner(isScanditScanner),
        Config.Environment(environment)
    )

}