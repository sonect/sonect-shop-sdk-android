package ch.sonect.sdk.shop.integrationapp.ui.sdkwrapper

import android.content.Context
import ch.sonect.sdk.shop.integrationapp.data.ConfigRepository
import ch.sonect.sdk.shop.integrationapp.ui.common.createViewModelFactory

@Suppress("FunctionName")
fun SdkWrapperActivityViewModelFactory(ctx: Context) = createViewModelFactory {
    SdkWrapperActivityViewModel(ConfigRepository(ctx))
}