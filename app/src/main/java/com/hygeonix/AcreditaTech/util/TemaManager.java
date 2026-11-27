package com.hygeonix.AcreditaTech.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Recuerda el tema que eligió el usuario (claro u oscuro) con el botón del mini menú.
 * Si nunca eligió, la app sigue el tema del teléfono.
 * Usa un archivo de preferencias propio para que cerrar sesión no borre la elección.
 */
public class TemaManager {

    private static final String PREFS = "acredita_tema";
    private static final String KEY_MODO = "modo_noche";

    private TemaManager() { }

    /** Aplica el tema guardado. Se llama al arrancar la app (AcreditaTechApp). */
    public static void aplicarTemaGuardado(Context contexto) {
        int modo = prefs(contexto).getInt(KEY_MODO, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(modo);
    }

    /** Guarda y aplica la elección del usuario: true = oscuro, false = claro. */
    public static void guardarTema(Context contexto, boolean oscuro) {
        int modo = oscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        prefs(contexto).edit().putInt(KEY_MODO, modo).apply();
        AppCompatDelegate.setDefaultNightMode(modo);
    }

    /** true si la pantalla se ve en oscuro ahora mismo (por elección o por el teléfono). */
    public static boolean estaEnOscuro(Context contexto) {
        int uiMode = contexto.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return uiMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private static SharedPreferences prefs(Context contexto) {
        return contexto.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
