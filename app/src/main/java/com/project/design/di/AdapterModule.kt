package com.project.design.di


import com.project.design.adapters.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent


@InstallIn(ActivityComponent::class)
@Module
class AdapterModule {



    @Provides
    fun providesWithdrawalRecordAdapter(): OrderRecordAdapter {
        return OrderRecordAdapter()
    }

    @Provides
    fun providesProductAdapter(): ProductAdapter {
        return ProductAdapter()
    }


}