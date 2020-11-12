package ch.sonect.sdk.shop.integrationapp

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> Activity.injectViewModel(configRepository: ConfigRepository): Lazy<T> = lazy {
    ConfigRelatedViewModelFactory(configRepository).create(T::class.java)
}

class ConfigRelatedViewModelFactory(private val configRepository: ConfigRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(configRepository) as T
        } else if (modelClass.isAssignableFrom(SdkWrapperActivityViewModel::class.java)) {
            return SdkWrapperActivityViewModel(configRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}