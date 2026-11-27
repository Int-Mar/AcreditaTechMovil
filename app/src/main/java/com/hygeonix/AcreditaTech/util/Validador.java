package com.hygeonix.AcreditaTech.util;

import androidx.annotation.StringRes;

import com.hygeonix.AcreditaTech.R;

import java.util.regex.Pattern;

/**
 * Mismas reglas que el servidor (seguridad/validaciones.js del backend),
 * para avisar al instante antes de enviar. El servidor siempre hace la
 * validación final.
 *
 * Cada método regresa null si el valor es válido, o el id del mensaje de error
 * (R.string.val_*), para que salga en el idioma del teléfono.
 */
public final class Validador {

    public static final int CONTRASENA_MIN = 8;
    public static final int CONTRASENA_MAX = 20;
    public static final int CORREO_MAX = 50;
    // Si cambias estos límites, cambia también los textos val_* en strings.xml
    private static final String DOMINIO = "gmail.com";

    // Nombre: solo letras (con acentos, ñ y ü) y espacios, de 5 a 25 caracteres
    private static final Pattern REGLA_NOMBRE =
            Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü ]{5,25}$");
    // Parte antes del @: letras, números y . _ % + -
    private static final Pattern REGLA_USUARIO_CORREO =
            Pattern.compile("^[A-Za-z0-9._%+\\-]+$");
    // Dominio con al menos un punto (ejemplo.com)
    private static final Pattern REGLA_DOMINIO =
            Pattern.compile("^[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern REGLA_TELEFONO = Pattern.compile("^55\\d{8}$");

    private Validador() { }

    @StringRes
    public static Integer nombre(String v) {
        String n = v == null ? "" : v.trim();
        if (n.isEmpty()) return R.string.val_nombre_vacio;
        if (n.length() < 5 || n.length() > 25) return R.string.val_nombre_longitud;
        if (!REGLA_NOMBRE.matcher(n).matches()) return R.string.val_nombre_letras;
        return null;
    }

    @StringRes
    public static Integer telefono(String v) {
        String t = v == null ? "" : v.trim();
        if (t.isEmpty()) return R.string.val_telefono_vacio;
        if (!t.matches("\\d+")) return R.string.val_telefono_numeros;
        if (!t.startsWith("55")) return R.string.val_telefono_55;
        if (!REGLA_TELEFONO.matcher(t).matches()) return R.string.val_telefono_10;
        return null;
    }

    /**
     * Correo para iniciar sesión: formato válido, con @ y dominio.
     * Las cuentas de AcreditaTech solo se registran con @gmail.com.
     */
    @StringRes
    public static Integer correo(String v) {
        String c = v == null ? "" : v.trim();
        if (c.isEmpty()) return R.string.val_correo_vacio;
        if (c.contains(" ")) return R.string.val_correo_espacios;
        if (c.length() > CORREO_MAX) return R.string.val_correo_largo;

        int arroba = c.indexOf('@');
        if (arroba < 0) return R.string.val_correo_sin_arroba;
        if (arroba != c.lastIndexOf('@')) return R.string.val_correo_dos_arrobas;

        String usuario = c.substring(0, arroba);
        String dominio = c.substring(arroba + 1);
        if (usuario.isEmpty()) return R.string.val_correo_sin_usuario;
        if (!REGLA_USUARIO_CORREO.matcher(usuario).matches())
            return R.string.val_correo_usuario;
        if (dominio.isEmpty()) return R.string.val_correo_sin_dominio;
        if (!REGLA_DOMINIO.matcher(dominio).matches()) return R.string.val_correo_dominio;
        if (!dominio.equalsIgnoreCase(DOMINIO)) return R.string.val_correo_gmail;
        return null;
    }

    /** Contraseña para iniciar sesión: solo se revisa la longitud (8 a 20). */
    @StringRes
    public static Integer contrasena(String v) {
        if (v == null || v.isEmpty()) return R.string.val_contrasena_vacia;
        if (v.length() < CONTRASENA_MIN)
            return R.string.val_contrasena_corta;
        if (v.length() > CONTRASENA_MAX)
            return R.string.val_contrasena_larga;
        return null;
    }
}
