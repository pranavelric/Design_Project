package com.project.design.di


import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class RetrofitModule {

//    @Singleton
//    @Provides
//    fun providesRetrofit(): Retrofit.Builder {
//        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
//            .addConverterFactory(MoshiConverterFactory.create())
//
//    }

//    @Singleton
//    @Provides
//    fun providesPlayingCardService(retrofit: Retrofit.Builder): PlayingCardApi {
//        return retrofit.build().create(PlayingCardApi::class.java)
//    }
}