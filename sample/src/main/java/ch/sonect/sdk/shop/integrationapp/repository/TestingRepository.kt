package ch.sonect.sdk.shop.integrationapp.repository

import ch.sonect.sdk.shop.SonectSDK
import ch.sonect.sdk.shop.integrationapp.data.CacheManager
import ch.sonect.sdk.shop.integrationapp.data.TestInfo

class TestingRepository(private val cacheManager: CacheManager) {

    fun getTestingInfo(): String? {
        return cacheManager.copiedInfo()
    }

    fun getDevDefaults(): TestInfo {
        return TestInfo(
            "800801",
            "4b0b1580-799f-11ea-b9fa-8798a68c4d2d",
            "baeb244bd98c8eebf19ab26740f00ac4169dc762710951a196ccc020e1c0e39a",
            "c313287948eb5a6134e31493d1620855ad21ac65337aca2aa640eb71ddb925e7",
            "", SonectSDK.Config.Enviroment.DEV
        )
    }

    fun getTestDefaults(): TestInfo {
        return TestInfo(
            "800801",
            "90061030-a637-11ea-b19e-2b0ec3d9e2c7",
            "10918d789ea3d3e233e3b2227a10ef91406ee0bad6104114ffba4e885189b4ef",
            "7e1829239a9bd90bb4e3453b0a271629046f2448e36e6c7121c97f0acea9874f",
            "", SonectSDK.Config.Enviroment.STAGING
        )
    }

    fun getProdDefaults(): TestInfo {
        return TestInfo("", "", "", "", "", SonectSDK.Config.Enviroment.PRODUCTION)
    }

    fun getLatestInfoUsed(): TestInfo? {
        return if (cacheManager.clientId.isNullOrEmpty()) {
            null
        } else {
            TestInfo(
                cacheManager.merchantId,
                cacheManager.clientId,
                cacheManager.clientSecret,
                cacheManager.hmacKey,
                cacheManager.deviceId,
                SonectSDK.Config.Enviroment.valueOf(cacheManager.envKey.orEmpty())
            )
        }
    }

    fun saveCurrentInfo(testInfo: TestInfo) {
        cacheManager.clientId = testInfo.clientId
        cacheManager.clientSecret = testInfo.clientSecret
        cacheManager.merchantId = testInfo.merchantId
        cacheManager.deviceId = testInfo.deviceId
        cacheManager.hmacKey = testInfo.hmacKey
        cacheManager.envKey = testInfo.envKey.name
    }
}