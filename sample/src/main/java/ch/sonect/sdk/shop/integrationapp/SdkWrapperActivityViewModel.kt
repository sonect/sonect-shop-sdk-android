package ch.sonect.sdk.shop.integrationapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SdkWrapperActivityViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    val state: LiveData<DataState>
        get() = _state

    private val _state: MutableLiveData<DataState> = MutableLiveData()

    init {
        _state.value = DataState.LoadedConfig(
            configRepository.getConfig(),
            configRepository.getSdkToken(),
            configRepository.getSignature()
        )
    }

    sealed class DataState {
        data class LoadedConfig(val data: Set<Config>, val token: String, val signature: String) : DataState()
    }
}