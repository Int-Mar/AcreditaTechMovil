package com.hygeonix.AcreditaTech.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Descarga la foto de perfil (foto_url que manda GET /api/perfil; el backend la
 * guarda en el bucket "fotos-perfil" de Supabase Storage) y la pone recortada en
 * círculo dentro del avatar.
 *
 * Usa su propio cliente, sin AuthInterceptor, para no mandar el token de la
 * sesión a un servidor que no es el nuestro. No guarda la foto en el teléfono.
 */
public final class FotoPerfil {

    private static final long MAX_BYTES = 5L * 1024 * 1024; // el backend acepta hasta 2 MB
    private static final ExecutorService HILO = Executors.newSingleThreadExecutor();
    private static final Handler PRINCIPAL = new Handler(Looper.getMainLooper());
    private static OkHttpClient cliente;

    public interface AlTerminar {
        void listo(boolean exito);
    }

    private FotoPerfil() { }

    private static synchronized OkHttpClient cliente() {
        if (cliente == null) {
            cliente = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
        return cliente;
    }

    /** Si no hay URL o falla la descarga, el avatar se queda con la imagen por defecto. */
    public static void cargar(Activity activity, String url, ImageView avatar, AlTerminar alTerminar) {
        if (url == null || url.trim().isEmpty()) {
            alTerminar.listo(false);
            return;
        }
        int lado = Math.max(avatar.getLayoutParams().width, 1);

        HILO.execute(() -> {
            Bitmap foto = null;
            Request peticion = new Request.Builder().url(url.trim()).build();
            try (Response respuesta = cliente().newCall(peticion).execute()) {
                ResponseBody cuerpo = respuesta.body();
                if (respuesta.isSuccessful() && cuerpo != null
                        && (cuerpo.contentLength() < 0 || cuerpo.contentLength() <= MAX_BYTES)) {
                    foto = decodificar(cuerpo.bytes(), lado);
                }
            } catch (IOException | IllegalArgumentException ignorado) {
                // Sin conexión, URL inválida o la imagen ya no existe
            }

            Bitmap resultado = foto;
            PRINCIPAL.post(() -> {
                if (activity.isFinishing() || activity.isDestroyed()) return;
                if (resultado == null) {
                    alTerminar.listo(false);
                    return;
                }
                RoundedBitmapDrawable circulo =
                        RoundedBitmapDrawableFactory.create(activity.getResources(), resultado);
                circulo.setCircular(true);
                avatar.setImageDrawable(circulo);
                alTerminar.listo(true);
            });
        });
    }

    /** Reduce la imagen al tamaño del avatar y la recorta cuadrada desde el centro. */
    private static Bitmap decodificar(byte[] datos, int lado) {
        BitmapFactory.Options medidas = new BitmapFactory.Options();
        medidas.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(datos, 0, datos.length, medidas);
        if (medidas.outWidth <= 0 || medidas.outHeight <= 0) return null;

        int escala = 1;
        while (Math.min(medidas.outWidth, medidas.outHeight) / (escala * 2) >= lado) escala *= 2;

        BitmapFactory.Options opciones = new BitmapFactory.Options();
        opciones.inSampleSize = escala;
        Bitmap original = BitmapFactory.decodeByteArray(datos, 0, datos.length, opciones);
        if (original == null) return null;

        int min = Math.min(original.getWidth(), original.getHeight());
        int x = (original.getWidth() - min) / 2;
        int y = (original.getHeight() - min) / 2;
        Bitmap cuadrada = Bitmap.createBitmap(original, x, y, min, min);
        if (cuadrada != original) original.recycle();
        return cuadrada;
    }
}
