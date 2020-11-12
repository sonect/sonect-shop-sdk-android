package ch.sonect.sdk.shop.integrationapp.ui.common

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified T : ViewModel> FragmentActivity.viewModelProvider(
    crossinline factoryProvider: () -> ViewModelProvider.Factory
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProvider(this, factoryProvider()).get(T::class.java)
}

inline fun <reified R : ViewModel> createViewModelFactory(
    crossinline viewModelProvider: () -> R
) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(R::class.java)) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return viewModelProvider() as T
    }
}