package ch.sonect.sdk.shop.integrationapp

import ch.sonect.sdk.shop.integrationapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class SampleApp: DaggerApplication() {
    val applicationInjector = DaggerAppComponent.builder().application(this).build()
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = applicationInjector
}