package com.ml.shubham0204.docqa.di

import android.app.Application
import android.content.Context
import com.ml.shubham0204.docqa.data.ChunksDB
import com.ml.shubham0204.docqa.data.DocumentsDB
import com.ml.shubham0204.docqa.domain.embeddings.SentenceEmbeddingProvider
import com.ml.shubham0204.docqa.domain.llm.GeminiRemoteAPI
import com.ml.shubham0204.docqa.domain.llm.GemmaLocalLLM
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
    fun provideDocumentsDB(): DocumentsDB {
        return DocumentsDB()
    }

    @Provides
    @Singleton
    fun provideChunksDB(): ChunksDB {
        return ChunksDB()
    }

    @Provides
    @Singleton
    fun provideGeminiRemoteAPI(): GeminiRemoteAPI {
        return GeminiRemoteAPI()
    }

    @Provides
    @Singleton
    fun provideGemmaLocalLLM(context: Application): GemmaLocalLLM {
        return GemmaLocalLLM(context)
    }

    @Provides
    @Singleton
    fun provideSentenceEncoder(context: Application): SentenceEmbeddingProvider {
        return SentenceEmbeddingProvider(context)
    }
}
