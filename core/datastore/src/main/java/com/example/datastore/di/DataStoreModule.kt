package com.example.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.datastore.LanguageDataStore
import com.example.datastore.LanguageDataStoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "language_preferences"
    )

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.languageDataStore
    }

    @Provides
    @Singleton
    fun provideLanguageDataStore(
        dataStore: DataStore<Preferences>
    ): LanguageDataStore {
        return LanguageDataStoreImpl(dataStore)
    }
}