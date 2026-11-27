package com.hygeonix.AcreditaTech.network;

import android.content.Context;

import com.hygeonix.AcreditaTech.BuildConfig;
import com.hygeonix.AcreditaTech.util.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente único de Retrofit. La app solo habla con el servidor de Render,
 * nunca directo con Supabase.
 *
 * La dirección viene de BuildConfig.API_BASE_URL (se cambia en gradle.properties).
 */
public final class ApiClient {

    private static ApiService api;

    private ApiClient() { }

    public static synchronized ApiService api(Context contexto) {
        if (api == null) {
            SessionManager sesion = new SessionManager(contexto.getApplicationContext());

            OkHttpClient.Builder cliente = new OkHttpClient.Builder()
                    // Render gratis se duerme y tarda cerca de 1 minuto en despertar
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    // Agrega "Authorization: Bearer ..." a cada petición si hay sesión
                    .addInterceptor(new AuthInterceptor(sesion));

            if (BuildConfig.DEBUG) {
                // Solo en desarrollo, y sin imprimir el token ni el cuerpo
                HttpLoggingInterceptor log = new HttpLoggingInterceptor();
                log.setLevel(HttpLoggingInterceptor.Level.BASIC);
                cliente.addInterceptor(log);
            }

            api = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(cliente.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return api;
    }
}
