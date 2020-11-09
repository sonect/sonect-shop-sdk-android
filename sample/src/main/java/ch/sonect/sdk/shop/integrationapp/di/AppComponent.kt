package ch.sonect.sdk.shop.integrationapp.di

import ch.sonect.sdk.shop.integrationapp.MainActivityViewModel
import ch.sonect.sdk.shop.integrationapp.di.module.AppModule
import ch.sonect.sdk.shop.integrationapp.SampleApp
import ch.sonect.sdk.shop.integrationapp.di.module.feature.TestingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = arrayOf(
        AndroidInjectionModule::class,
        AppModule::class,
        TestingModule::class
    )
)

interface AppComponent : AndroidInjector<SampleApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: SampleApp): Builder

        fun build(): AppComponent
    }

    override fun inject(app: SampleApp)

    fun inject(mainActivityViewModel: MainActivityViewModel)
}