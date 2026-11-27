package com.hygeonix.AcreditaTech.network;

import androidx.annotation.NonNull;

import com.hygeonix.AcreditaTech.util.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Pone el token guardado en el encabezado Authorization de cada petición. */
public class AuthInterceptor implements Interceptor {

    private final SessionManager sesion;

    public AuthInterceptor(SessionManager sesion) {
        this.sesion = sesion;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String bearer = sesion.getBearer();
        if (bearer == null || original.header("Authorization") != null) {
            return chain.proceed(original);
        }
        return chain.proceed(original.newBuilder()
                .header("Authorization", bearer)
                .build());
    }
}
