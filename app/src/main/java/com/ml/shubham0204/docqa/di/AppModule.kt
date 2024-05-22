package com.ml.shubham0204.docqa.di

import com.ml.shubham0204.docqa.data.DocumentsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// AppModule provides dependencies that are to be injected by Hilt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // SingletonComponent ensures that instances survive
    // across the application's lifespan

    // @Singleton creates a single instance in the app's lifespan
    @Provides
    @Singleton
    fun provideUserRepository(): DocumentsRepository {
        return DocumentsRepository()
    }
}
