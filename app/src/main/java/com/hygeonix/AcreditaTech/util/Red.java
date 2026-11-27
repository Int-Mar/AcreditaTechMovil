package com.hygeonix.AcreditaTech.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.controlador.LoginActivity;
import com.hygeonix.AcreditaTech.modelo.MensajeResponse;

import okhttp3.ResponseBody;
import retrofit2.Response;

/** Ayudas comunes para las pantallas que hablan con el servidor. */
public final class Red {

    private Red() { }

    /** ¿El teléfono tiene internet ahora mismo? */
    public static boolean hayConexion(Context contexto) {
        ConnectivityManager cm =
                (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    /** Lee el { mensaje } o { error } que manda el servidor cuando algo sale mal. */
    public static String mensajeDeError(Response<?> respuesta, String porDefecto) {
        ResponseBody cuerpo = respuesta.errorBody();
        if (cuerpo == null) return porDefecto;
        try {
            MensajeResponse m = new Gson().fromJson(cuerpo.charStream(), MensajeResponse.class);
            if (m != null && m.mensaje != null) return m.mensaje;
            if (m != null && m.error != null) return m.error;
        } catch (Exception ignorado) {
            // El cuerpo no era JSON (por ejemplo, Render despertando)
        }
        return porDefecto;
    }

    /**
     * Si el servidor respondió 401 (token vencido o inválido), borra la sesión
     * y regresa al login. Regresa true si se manejó.
     */
    public static boolean sesionExpirada(Activity activity, Response<?> respuesta) {
        if (respuesta.code() != 401) return false;
        irAlLogin(activity);
        Toast.makeText(activity, R.string.error_sesion_expirada, Toast.LENGTH_LONG).show();
        return true;
    }

    /** Cierra la sesión y abre el login sin dejar pantallas atrás. */
    public static void irAlLogin(Activity activity) {
        new SessionManager(activity).cerrarSesion();
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /** Pone el nombre guardado del trabajador en el encabezado (txtUserName) si la pantalla lo tiene. */
    public static void mostrarNombre(Activity activity) {
        TextView txt = activity.findViewById(R.id.txtUserName);
        String nombre = new SessionManager(activity).getNombre();
        if (txt != null && !nombre.isEmpty()) txt.setText(nombre);
    }
}
