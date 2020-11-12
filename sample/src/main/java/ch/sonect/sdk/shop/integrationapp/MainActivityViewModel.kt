package ch.sonect.sdk.shop.integrationapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ch.sonect.sdk.shop.SonectSDK

class MainActivityViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    val state: LiveData<DataState>
        get() = _state

    private val _state: MutableLiveData<DataState> = MutableLiveData()

    init {
        _state.value = DataState.InitialLoadedConfig(configRepository.get())
    }

    fun save(config: Set<Config>) {
        configRepository.save(config)
        _state.value = DataState.SdkInitiation
    }

    fun changeEnvironment(env: SonectSDK.Config.Enviroment) {
        configRepository.setupEnvironment(env)
        _state.value = DataState.LoadedConfig(configRepository.get())
    }

    sealed class DataState {
        class LoadedConfig(val data: Set<Config>) : DataState()
        class InitialLoadedConfig(val data: Set<Config>) : DataState()
        object SdkInitiation: DataState()
    }
}