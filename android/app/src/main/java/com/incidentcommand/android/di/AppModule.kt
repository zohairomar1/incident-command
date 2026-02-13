package com.incidentcommand.android.di

import android.content.Context
import androidx.room.Room
import com.incidentcommand.android.data.local.dao.IncidentDao
import com.incidentcommand.android.data.local.db.AppDatabase
import com.incidentcommand.android.data.manager.TokenManager
import com.incidentcommand.android.data.remote.api.AuthApi
import com.incidentcommand.android.data.remote.api.EscalationApi
import com.incidentcommand.android.data.remote.api.IncidentApi
import com.incidentcommand.android.data.remote.api.MetricsApi
import com.incidentcommand.android.data.remote.api.TeamApi
import com.incidentcommand.android.data.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIncidentApi(retrofit: Retrofit): IncidentApi {
        return retrofit.create(IncidentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMetricsApi(retrofit: Retrofit): MetricsApi {
        return retrofit.create(MetricsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTeamApi(retrofit: Retrofit): TeamApi {
        return retrofit.create(TeamApi::class.java)
    }

    @Provides
    @Singleton
    fun provideEscalationApi(retrofit: Retrofit): EscalationApi {
        return retrofit.create(EscalationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "incident_command.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideIncidentDao(database: AppDatabase): IncidentDao {
        return database.incidentDao()
    }
}
