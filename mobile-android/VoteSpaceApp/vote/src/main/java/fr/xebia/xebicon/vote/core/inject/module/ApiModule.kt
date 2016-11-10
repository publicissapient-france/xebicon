package fr.xebia.votespaceapp.core.inject.module

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import fr.xebia.xebicon.vote.BuildConfig
import fr.xebia.xebicon.vote.core.api.VoteAPI
import fr.xebia.xebicon.vote.core.preferences.UserPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApiModule(val baseUrl: String, val application: Application) {

    @Provides @Singleton
    fun providesInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
                .setLevel(
                        if (BuildConfig.DEBUG)
                            HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE)
    }

    @Provides @Singleton
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    @Provides @Singleton
    fun providesGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides @Singleton
    fun providesRxJavaCallAdapterFactory(): RxJavaCallAdapterFactory {
        return RxJavaCallAdapterFactory.create()
    }

    @Provides @Singleton fun providesRetrofit(okHttpClient: OkHttpClient, gson: Gson, rxJavaCallAdapterFactory: RxJavaCallAdapterFactory): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build()
    }

    @Provides @Singleton fun providesVoteApi(retrofit: Retrofit): VoteAPI {
        return retrofit.create(VoteAPI::class.java)
    }

    @Provides fun provideApplication(): Application {
        return application
    }

    @Provides @Singleton fun providesUserPreferences(context: Application) : UserPreferences {
        return UserPreferences(context)
    }
}

