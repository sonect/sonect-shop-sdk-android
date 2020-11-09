package ch.sonect.sdk.shop.integrationapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ch.sonect.sdk.domain.testing.shop.TestingRepository
import ch.sonect.sdk.domain.testing.shop.model.TestInfo
import ch.sonect.sdk.shop.SonectSDK
import com.hadilq.liveevent.LiveEvent
import javax.inject.Inject

class MainActivityViewModel : ViewModel() {

    val currentInfo: LiveData<TestInfo>
        get() = _currentInfo

    val defaultEnv: LiveData<TestInfo>
        get() = _defaultEnv

    @Inject
    lateinit var testingRepository: TestingRepository

    private val _currentInfo = LiveEvent<TestInfo>()
    private val _defaultEnv = LiveEvent<TestInfo>()

    fun infoToClipBoard(): String? {
        return testingRepository.getTestingInfo()
    }

    fun getLastUsedInfo() {
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
}