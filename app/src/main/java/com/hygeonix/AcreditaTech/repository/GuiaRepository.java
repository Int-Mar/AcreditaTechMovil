package com.hygeonix.AcreditaTech.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hygeonix.AcreditaTech.modelo.Guia;
import com.hygeonix.AcreditaTech.modelo.GuiasResponse;
import com.hygeonix.AcreditaTech.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Trae las guías del oficio del trabajador desde el servidor (GET /api/guias).
 * El servidor ya filtra por el oficio del usuario de la sesión.
 *
 * No se guarda ninguna copia en el teléfono: sin conexión no hay guías.
 */
public class GuiaRepository {

    public interface Resultado {
        void alTerminar(@NonNull List<Guia> guias);

        /** El servidor respondió con un error (400, 401, 404, 500...). */
        void alFallar(@NonNull Response<?> respuesta);

        /** No hubo conexión o el servidor no respondió. */
        void sinConexion();
    }

    private final Context contexto;

    public GuiaRepository(Context contexto) {
        this.contexto = contexto.getApplicationContext();
    }

    /** @param tipo 1 = técnica, 2 = salud ocupacional, null = todas. */
    public void obtenerGuias(Integer tipo, Resultado resultado) {
        ApiClient.api(contexto).guias(tipo).enqueue(new Callback<GuiasResponse>() {
            @Override
            public void onResponse(@NonNull Call<GuiasResponse> call,
                                   @NonNull Response<GuiasResponse> respuesta) {
                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    List<Guia> guias = respuesta.body().guias != null
                            ? respuesta.body().guias : new ArrayList<>();
                    resultado.alTerminar(guias);
                } else {
                    resultado.alFallar(respuesta);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GuiasResponse> call, @NonNull Throwable t) {
                resultado.sinConexion();
            }
        });
    }
}
