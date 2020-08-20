package ch.sonect.sdk.shop.integrationapp

import android.content.Context
import ch.sonect.sdk.shop.BuildConfig

private const val MERCHANT_ID = "merchantid"
private const val CLIENT_ID = "clientid"
private const val CLIENT_SECRET = "clientsecret"
private const val DEVICE_ID = "deviceid"
private const val HMAC_KEY = "hmac"
private const val ENV_KEY = "envKey"

class CacheManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("shopsample", Context.MODE_PRIVATE)

    var merchantId: String?
        get() = sharedPreferences.getString(MERCHANT_ID, null)
        set(value) = sharedPreferences.edit().putString(MERCHANT_ID, value).apply()

    var clientId: String?
        get() = sharedPreferences.getString(CLIENT_ID, null)
        set(value) = sharedPreferences.edit().putString(CLIENT_ID, value).apply()

    var clientSecret: String?
        get() = sharedPreferences.getString(CLIENT_SECRET, null)
        set(value) = sharedPreferences.edit().putString(CLIENT_SECRET, value).apply()

    var deviceId: String?
        get() = sharedPreferences.getString(DEVICE_ID, null)
        set(value) = sharedPreferences.edit().putString(DEVICE_ID, value).apply()

    var hmacKey: String?
        get() = sharedPreferences.getString(HMAC_KEY, null)
        set(value) = sharedPreferences.edit().putString(HMAC_KEY, value).apply()

    var envKey: String?
        get() = sharedPreferences.getString(ENV_KEY, "DEV")
        set(value) = sharedPreferences.edit().putString(ENV_KEY, value).apply()

    fun copiedInfo(): String? {
        return "MerchantId: $merchantId\nClientId: $clientId\nClient Secret: $clientSecret\n" +
                "Hmac: $hmacKey\nDeviceId: $deviceId\nEnvironment: $envKey\nApp version:${BuildConfig.VERSION_NAME}"
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}