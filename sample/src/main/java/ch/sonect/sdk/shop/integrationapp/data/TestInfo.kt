package ch.sonect.sdk.shop.integrationapp.data

import ch.sonect.sdk.shop.SonectSDK

data class TestInfo(
    var merchantId: String? = "",
    var clientId: String? = "",
    var clientSecret: String? = "",
    var hmacKey: String? = "",
    var deviceId: String? = "",
    var envKey: SonectSDK.Config.Enviroment
)