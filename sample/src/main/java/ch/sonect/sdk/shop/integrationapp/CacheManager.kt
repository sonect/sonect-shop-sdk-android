package ch.sonect.sdk.shop.integrationapp

import android.content.Context

class CacheManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("shopsample", Context.MODE_PRIVATE)

    var merchantId: String?
        get() = sharedPreferences.getString(MERCHANT_ID, "800801")
        set(value) = sharedPreferences.edit().putString(MERCHANT_ID, value).apply()

    var clientId: String?
        get() = sharedPreferences.getString(CLIENT_ID, "4b0b1580-799f-11ea-b9fa-8798a68c4d2d")
        set(value) = sharedPreferences.edit().putString(CLIENT_ID, value).apply()

    var clientSecret: String?
        get() = sharedPreferences.getString(CLIENT_SECRET, "baeb244bd98c8eebf19ab26740f00ac4169dc762710951a196ccc020e1c0e39a")
        set(value) = sharedPreferences.edit().putString(CLIENT_SECRET, value).apply()

    var deviceId: String?
        get() = sharedPreferences.getString(DEVICE_ID, "")
        set(value) = sharedPreferences.edit().putString(DEVICE_ID, value).apply()

    var hmacKey: String?
        get() = sharedPreferences.getString(HMAC_KEY, "c313287948eb5a6134e31493d1620855ad21ac65337aca2aa640eb71ddb925e7")
        set(value) = sharedPreferences.edit().putString(HMAC_KEY, value).apply()

    var envKey: String?
        get() = sharedPreferences.getString(ENV_KEY, "DEV")
        set(value) = sharedPreferences.edit().putString(ENV_KEY, value).apply()

    fun copiedInfo(): String? {
        return "MerchantId: $merchantId\nClientId: $clientId\nClient Secret: $clientSecret\n" +
                "Hmac: $hmacKey\nDeviceId: $deviceId\nEnvironment: $envKey"
    }

    companion object{
        private const val MERCHANT_ID = "merchantid"
        private const val CLIENT_ID = "clientid"
        private const val CLIENT_SECRET = "clientsecret"
        private const val DEVICE_ID = "deviceid"
        private const val HMAC_KEY = "hmac"
        private const val ENV_KEY = "envKey"
    }

}