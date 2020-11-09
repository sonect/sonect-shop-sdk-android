package ch.sonect.sdk.shop.integrationapp.di.module

import android.content.Context
import android.content.SharedPreferences
import ch.sonect.sdk.shop.integrationapp.SampleApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    internal fun provideContext(application: SampleApp): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    internal fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("shopsample", Context.MODE_PRIVATE)
    }
}