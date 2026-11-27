package com.hygeonix.AcreditaTech.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.hygeonix.AcreditaTech.R;

/**
 * Evita que las pantallas se metan debajo de la barra de estado (arriba) y de la
 * barra de navegación/gestos (abajo).
 *
 * En Android 15 o más nuevo (por ejemplo el Honor Magic 7 Lite) el sistema obliga a
 * las apps a dibujarse de borde a borde. Aquí se hace lo mismo en todas las versiones
 * y se deja un margen igual al tamaño de las barras del sistema, pintado con el color
 * del encabezado, para que todas las pantallas se vean igual en cualquier teléfono.
 *
 * Se llama una vez por pantalla desde AcreditaTechApp, así que ninguna Activity
 * tiene que hacer nada.
 */
public final class BarrasSistema {

    private BarrasSistema() { }

    public static void aplicar(Activity activity) {
        View contenido = activity.findViewById(android.R.id.content);
        // Solo una vez por pantalla (onStart se repite al volver a ella)
        if (contenido == null || contenido.getTag(R.id.barras_sistema_aplicadas) != null) {
            return;
        }
        contenido.setTag(R.id.barras_sistema_aplicadas, Boolean.TRUE);

        Window window = activity.getWindow();
        int colorBarras = colorEncabezado(activity);

        // La app se dibuja detrás de las barras en todos los Android, no solo en 15+
        WindowCompat.setDecorFitsSystemWindows(window, false);
        if (Build.VERSION.SDK_INT < 35) {
            // En Android 15+ ya son transparentes y estos métodos no hacen nada
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Sin velo gris encima de la barra de navegación de 3 botones
            window.setNavigationBarContrastEnforced(false);
        }

        // El encabezado es azul en tema claro y oscuro: íconos del sistema en blanco
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        // El margen que deja el padding se ve del color del encabezado / barra inferior
        contenido.setBackgroundColor(colorBarras);

        ViewCompat.setOnApplyWindowInsetsListener(contenido, (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            // Si el teclado está abierto, la pantalla se encoge para que no tape los campos
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right,
                    Math.max(barras.bottom, teclado.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(contenido);
    }

    /** Lee ?attr/appHeaderColor del tema actual (cambia entre claro y oscuro). */
    private static int colorEncabezado(Activity activity) {
        TypedValue valor = new TypedValue();
        if (activity.getTheme().resolveAttribute(R.attr.appHeaderColor, valor, true)
                && valor.type >= TypedValue.TYPE_FIRST_COLOR_INT
                && valor.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return valor.data;
        }
        return activity.getResources().getColor(R.color.header_bg, activity.getTheme());
    }
}
