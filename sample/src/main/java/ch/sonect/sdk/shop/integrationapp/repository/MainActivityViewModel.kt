package ch.sonect.sdk.shop.integrationapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ch.sonect.sdk.shop.SonectSDK
import ch.sonect.sdk.shop.integrationapp.data.CacheManager
import ch.sonect.sdk.shop.integrationapp.data.TestInfo
import com.hadilq.liveevent.LiveEvent

class MainActivityViewModel : ViewModel() {

    val currentInfo: LiveData<TestInfo>
        get() = _currentInfo

    val defaultEnv: LiveData<TestInfo>
        get() = _defaultEnv

    private val _currentInfo = LiveEvent<TestInfo>()
    private val _defaultEnv = LiveEvent<TestInfo>()
    private lateinit var testingRepository: TestingRepository

    fun infoToClipBoard(): String? {
        return testingRepository.getTestingInfo()
    }

    private fun getLastUsedInfo() {
        val testInfo = testingRepository.getLatestInfoUsed() ?: testingRepository.getDevDefaults()
        _currentInfo.value = testInfo
    }

    fun saveCurrentInfo(testInfo: TestInfo) {
        testingRepository.saveCurrentInfo(testInfo)
    }

    fun getDefaults(env: SonectSDK.Config.Enviroment) {
        _defaultEnv.value = when (env) {
            SonectSDK.Config.Enviroment.PRODUCTION -> testingRepository.getProdDefaults()
            SonectSDK.Config.Enviroment.STAGING -> testingRepository.getTestDefaults()
            SonectSDK.Config.Enviroment.DEV -> testingRepository.getDevDefaults()
        }
    }

    fun setContext(applicationContext: Context) {
        testingRepository = TestingRepository(CacheManager(applicationContext))
        getLastUsedInfo()
    }

}