package com.hygeonix.AcreditaTech.util;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda el token de sesión, el nombre y el oficio del trabajador en el teléfono. */
public class SessionManager {

    private static final String PREFS = "acredita_sesion";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NOMBRE = "nombre";
    private static final String KEY_OFICIO = "oficio";

    private final SharedPreferences prefs;

    public SessionManager(Context contexto) {
        prefs = contexto.getApplicationContext()
                .getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void guardarToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public boolean haySesion() {
        return prefs.getString(KEY_TOKEN, null) != null;
    }

    /** Formato que va en el encabezado: "Bearer eyJ...". */
    public String getBearer() {
        String t = prefs.getString(KEY_TOKEN, null);
        return t == null ? null : "Bearer " + t;
    }

    public void guardarNombre(String nombre) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply();
    }

    public String getNombre() {
        return prefs.getString(KEY_NOMBRE, "");
    }

    /** Oficio con el que se registró (Plomería, Carpintería...), para el título del menú. */
    public void guardarOficio(String oficio) {
        prefs.edit().putString(KEY_OFICIO, oficio).apply();
    }

    public String getOficio() {
        return prefs.getString(KEY_OFICIO, "");
    }

    /** Borra el token, el nombre y el oficio (cierra la sesión). */
    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }
}
