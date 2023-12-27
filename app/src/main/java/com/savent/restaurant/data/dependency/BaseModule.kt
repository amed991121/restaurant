package com.savent.restaurant.data.dependency

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import coil.Coil
import coil.ImageLoader
import com.google.gson.Gson
import com.savent.restaurant.AppConstants
import com.savent.restaurant.NetworkConnectivityObserver
import com.savent.restaurant.data.local.database.AppDatabase
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

val baseModule = module {

    single { Gson() }

    single {
        NetworkConnectivityObserver(androidContext())
    }

    /*single<OkHttpClient> {

        OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", AppConstants.AUTHORIZATION)
                .build()
            chain.proceed(request)
        }.build()
    }*/

    fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                }
            )

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    single<OkHttpClient> {
        getUnsafeOkHttpClient()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", AppConstants.AUTHORIZATION)
                    .build()
                chain.proceed(request)
            }.build()
    }

    single<AppDatabase> {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            AppConstants.APP_DATABASE_NAME
        ).build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(AppConstants.SAVENT_RESTAURANT_API_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<ImageLoader> {
        val imageLoader = ImageLoader.Builder(androidApplication())
            .okHttpClient(getUnsafeOkHttpClient().build())
            .build()
        imageLoader.memoryCache.clear()
        imageLoader
    }
    single { SavedStateHandle() }
}