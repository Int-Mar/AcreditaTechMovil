package com.hygeonix.AcreditaTech.controlador;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.modelo.ConstanciaResponse;
import com.hygeonix.AcreditaTech.network.ApiClient;
import com.hygeonix.AcreditaTech.util.Red;
import com.hygeonix.AcreditaTech.util.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pre-Constancia: el servidor revisa las reglas y genera el PDF;
 * la app solo lo descarga y lo guarda en Descargas.
 */
public class ConstanciaActivity extends AppCompatActivity {

    private Button btnDescargar;
    private TextView[] lineas;
    private String codigo;

    private final ExecutorService hilo = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos con tu layout de pre-constancia
        setContentView(R.layout.activity_pre_constancia);

        // Activamos la barra de navegación inferior y el mini menú automáticamente
        NavActivity.setupNav(this);

        TextView txtNombre = findViewById(R.id.txtRecipientName);
        txtNombre.setText(new SessionManager(this).getNombre());

        lineas = new TextView[]{
                findViewById(R.id.txtConstanciaLine1),
                findViewById(R.id.txtConstanciaLine2),
                findViewById(R.id.txtConstanciaLine3),
                findViewById(R.id.txtConstanciaLine4),
                findViewById(R.id.txtConstanciaLine5),
                findViewById(R.id.txtConstanciaLine6),
        };
        mostrarLineas(getString(R.string.cargando));

        btnDescargar = findViewById(R.id.btnDownload);
        btnDescargar.setEnabled(false);
        btnDescargar.setOnClickListener(v -> descargar());

        cargarEstado();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hilo.shutdown();
    }

    /** GET /api/constancia: ¿ya la tiene? ¿puede generarla? */
    private void cargarEstado() {
        ApiClient.api(this).constancia().enqueue(new Callback<ConstanciaResponse>() {
            @Override
            public void onResponse(@NonNull Call<ConstanciaResponse> call,
                                   @NonNull Response<ConstanciaResponse> respuesta) {
                if (Red.sesionExpirada(ConstanciaActivity.this, respuesta)) return;
                ConstanciaResponse c = respuesta.body();
                if (!respuesta.isSuccessful() || c == null) {
                    mostrarLineas(Red.mensajeDeError(respuesta, getString(R.string.error_sin_conexion)));
                    return;
                }
                mostrarEstado(c);
            }

            @Override
            public void onFailure(@NonNull Call<ConstanciaResponse> call, @NonNull Throwable t) {
                mostrarLineas(getString(R.string.constancia_sin_conexion));
            }
        });
    }

    private void mostrarEstado(ConstanciaResponse c) {
        if (c.tiene_constancia) {
            codigo = c.codigo;
            boolean vigente = "Vigente".equals(c.estado);
            mostrarLineas(
                    getString(R.string.constancia_motivo),
                    getString(R.string.constancia_codigo, c.codigo),
                    getString(R.string.constancia_estado, c.estado),
                    getString(R.string.constancia_emitida, fecha(c.emitida)),
                    getString(R.string.constancia_vigente_hasta, fecha(c.vigente_hasta)),
                    getString(R.string.constancia_aviso));
            btnDescargar.setEnabled(vigente);
            btnDescargar.setText(vigente ? R.string.btn_descargar_constancia : R.string.constancia_vencida);
        } else if (c.puede_generar) {
            mostrarLineas(
                    getString(R.string.constancia_aprobaste),
                    getString(R.string.constancia_puedes_generar));
            btnDescargar.setEnabled(true);
            btnDescargar.setText(R.string.btn_descargar_constancia);
        } else {
            mostrarLineas(getString(R.string.constancia_bloqueada) + ".");
            btnDescargar.setEnabled(false);
            btnDescargar.setText(R.string.constancia_bloqueada);
        }
    }

    /** GET /api/constancia/pdf → guarda el archivo en Descargas. */
    private void descargar() {
        if (!Red.hayConexion(this)) {
            Toast.makeText(this, R.string.constancia_sin_conexion, Toast.LENGTH_LONG).show();
            return;
        }
        btnDescargar.setEnabled(false);
        btnDescargar.setText(R.string.constancia_descargando);

        Call<ResponseBody> llamada = ApiClient.api(this).descargarConstancia();
        hilo.execute(() -> {
            // @Streaming: el cuerpo se lee aquí, fuera del hilo principal
            try {
                Response<ResponseBody> respuesta = llamada.execute();
                if (!respuesta.isSuccessful() || respuesta.body() == null) {
                    String mensaje = Red.mensajeDeError(respuesta,
                            getString(R.string.error_sin_conexion));
                    principal.post(() -> {
                        if (Red.sesionExpirada(this, respuesta)) return;
                        terminarDescarga(null, mensaje);
                    });
                    return;
                }
                String nombre = "pre-constancia-" + (codigo != null ? codigo : "acreditatech") + ".pdf";
                Uri archivo = guardarEnDescargas(respuesta.body(), nombre);
                principal.post(() -> terminarDescarga(archivo, getString(R.string.constancia_guardada)));
            } catch (IOException e) {
                principal.post(() -> terminarDescarga(null, getString(R.string.constancia_sin_conexion)));
            }
        });
    }

    private void terminarDescarga(Uri archivo, String mensaje) {
        if (isFinishing() || isDestroyed()) return;
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        cargarEstado(); // vuelve a pedir el estado (ahora ya tiene código y fechas)
        if (archivo != null) abrirPdf(archivo);
    }

    /**
     * Android 10+ : carpeta pública Descargas con MediaStore (no pide permisos).
     * Android 7 a 9: carpeta Descargas propia de la app (tampoco pide permisos).
     */
    private Uri guardarEnDescargas(ResponseBody cuerpo, String nombre) throws IOException {
        try (InputStream entrada = cuerpo.byteStream()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = getContentResolver();
                ContentValues datos = new ContentValues();
                datos.put(MediaStore.Downloads.DISPLAY_NAME, nombre);
                datos.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                datos.put(MediaStore.Downloads.IS_PENDING, 1);
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, datos);
                if (uri == null) throw new IOException("No se pudo crear el archivo");
                try (OutputStream salida = resolver.openOutputStream(uri)) {
                    if (salida == null) throw new IOException("No se pudo abrir el archivo");
                    copiar(entrada, salida);
                } catch (IOException e) {
                    resolver.delete(uri, null, null);
                    throw e;
                }
                datos.clear();
                datos.put(MediaStore.Downloads.IS_PENDING, 0);
                resolver.update(uri, datos, null, null);
                return uri;
            } else {
                File carpeta = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (carpeta == null) throw new IOException("Sin almacenamiento");
                File archivo = new File(carpeta, nombre);
                try (OutputStream salida = new FileOutputStream(archivo)) {
                    copiar(entrada, salida);
                }
                return null; // en estas versiones no lo abrimos (necesitaría FileProvider)
            }
        }
    }

    private static void copiar(InputStream entrada, OutputStream salida) throws IOException {
        byte[] buffer = new byte[8192];
        int leidos;
        while ((leidos = entrada.read(buffer)) != -1) {
            salida.write(buffer, 0, leidos);
        }
    }

    private void abrirPdf(Uri uri) {
        Intent ver = new Intent(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/pdf")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(ver);
        } catch (android.content.ActivityNotFoundException e) {
            // No hay visor de PDF instalado; el archivo ya quedó en Descargas
        }
    }

    private void mostrarLineas(String... textos) {
        for (int i = 0; i < lineas.length; i++) {
            lineas[i].setText(i < textos.length ? textos[i] : "");
        }
    }

    /** "2026-11-13T06:00:00.000Z" → "13/11/2026". */
    private static String fecha(String iso) {
        if (iso == null || iso.length() < 10) return iso == null ? "" : iso;
        return iso.substring(8, 10) + "/" + iso.substring(5, 7) + "/" + iso.substring(0, 4);
    }
}
