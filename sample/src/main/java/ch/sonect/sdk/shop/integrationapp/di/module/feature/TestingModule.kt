package ch.sonect.sdk.shop.integrationapp.di.module.feature

import android.content.SharedPreferences
import ch.sonect.sdk.data.testing.shop.SampleCacheManager
import ch.sonect.sdk.data.testing.shop.ShopTestingGateway
import ch.sonect.sdk.domain.testing.shop.TestingRepository
import dagger.Module
import dagger.Provides

@Module
class TestingModule {

    @Provides
    fun provideTestingRepository(
        sampleCacheManager: SampleCacheManager
    ): TestingRepository {
        return ShopTestingGateway(
            sampleCacheManager
        )
    }

    @Provides
    fun provideCacheContext(
        sharedPreferences: SharedPreferences
    ): SampleCacheManager {
        return SampleCacheManager(sharedPreferences)
    }
}