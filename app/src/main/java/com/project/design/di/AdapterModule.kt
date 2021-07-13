package com.project.design.di


import com.project.design.adapters.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@InstallIn(ActivityComponent::class)
@Module
class AdapterModule {

//    @Provides
//    fun providesCompleteProfileAdapter(): CompleteProfileAdapter {
//        return CompleteProfileAdapter()
//    }


    @Provides
    fun providesPurchaseAdapter(): PurchaseAdapter {
        return PurchaseAdapter()
    }


    @Provides
    fun providesWithdrawalRecordAdapter(): WithdrawalRecordAdapter {
        return WithdrawalRecordAdapter()
    }

    @Provides
    fun providesProductAdapter(): ProductAdapter {
        return ProductAdapter()
    }


}