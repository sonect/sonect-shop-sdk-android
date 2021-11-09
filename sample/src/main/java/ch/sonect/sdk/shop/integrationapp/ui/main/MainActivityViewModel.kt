package ch.sonect.sdk.shop.integrationapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.sonect.sdk.shop.SonectSDK
import ch.sonect.sdk.shop.integrationapp.data.Config
import ch.sonect.sdk.shop.integrationapp.data.ConfigRepository
import ch.sonect.sdk.shop.integrationapp.data.PredefineConfig

class MainActivityViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    val state: LiveData<DataState>
        get() = _state

    private val _state: MutableLiveData<DataState> = MutableLiveData()

    init {
        _state.value = DataState.InitialLoadedConfig(configRepository.getConfig())
    }

    fun save(config: Set<Config>) {
        configRepository.save(config)
        _state.value = DataState.SdkInitiation
    }

    fun changeEnvironment(env: SonectSDK.Config.Enviroment) {
        configRepository.setupEnvironment(env)
        _state.value = DataState.LoadedConfig(configRepository.getConfig())
    }

    fun loadPredefinedConfig(predefineConfig: PredefineConfig) {
        _state.value = DataState.InitialLoadedConfig(predefineConfig.config)
    }

    sealed class DataState {
        class LoadedConfig(val data: Set<Config>) : DataState()
        class InitialLoadedConfig(val data: Set<Config>) : DataState()
        object SdkInitiation : DataState()
    }
}