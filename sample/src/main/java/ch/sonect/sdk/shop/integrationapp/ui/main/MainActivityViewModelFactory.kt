package ch.sonect.sdk.shop.integrationapp.ui.main

import android.content.Context
import ch.sonect.sdk.shop.integrationapp.data.ConfigRepository
import ch.sonect.sdk.shop.integrationapp.ui.common.createViewModelFactory

@Suppress("FunctionName")
fun MainActivityViewModelFactory(ctx: Context) = createViewModelFactory {
    MainActivityViewModel(ConfigRepository(ctx))
}
