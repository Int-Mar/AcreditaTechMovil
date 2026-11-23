package com.hygeonix.AcreditaTech.controlador;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.modelo.AvanceResponse;
import com.hygeonix.AcreditaTech.network.ApiClient;
import com.hygeonix.AcreditaTech.util.Red;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Resultados de los tests (solo lectura). Los tests se hacen en la web;
 * aquí solo se muestra lo que responde GET /api/avance.
 */
public class TestResultActivity extends AppCompatActivity {

    private String tipoTest;
    private TextView txtTitulo;
    private TextView txtPorcentaje;
    private TextView txtDetalle;
    private TextView txtFeedbackTitulo;
    private TextView txtFeedbackMensaje;
    private View cajaPuntaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        NavActivity.setupNav(this);

        tipoTest = getIntent().getStringExtra("TIPO_TEST");
        if (tipoTest == null) tipoTest = "FINAL";

        txtTitulo = findViewById(R.id.txtTestTitle);
        txtPorcentaje = findViewById(R.id.txtPercentage);
        txtDetalle = findViewById(R.id.txtAcerts);
        txtFeedbackTitulo = findViewById(R.id.txtFeedbackTitle);
        txtFeedbackMensaje = findViewById(R.id.txtFeedbackMessage);
        cajaPuntaje = findViewById(R.id.layoutScoreBox);

        switch (tipoTest) {
            case "TECNICO":
                txtTitulo.setText(R.string.result_titulo_tecnico);
                break;
            case "OCUPACIONAL":
                txtTitulo.setText(R.string.result_titulo_ocupacional);
                break;
            default:
                txtTitulo.setText(R.string.result_titulo_general);
                break;
        }
        txtPorcentaje.setText(R.string.result_cargando);
        txtDetalle.setText(R.string.cargando);
        txtFeedbackTitulo.setText("");
        txtFeedbackMensaje.setText("");

        cargar();
    }

    private void cargar() {
        ApiClient.api(this).avance().enqueue(new Callback<AvanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<AvanceResponse> call,
                                   @NonNull Response<AvanceResponse> respuesta) {
                if (Red.sesionExpirada(TestResultActivity.this, respuesta)) return;
                AvanceResponse avance = respuesta.body();
                if (!respuesta.isSuccessful() || avance == null) {
                    txtDetalle.setText(Red.mensajeDeError(respuesta,
                            getString(R.string.error_sin_conexion)));
                    return;
                }
                mostrar(avance);
            }

            @Override
            public void onFailure(@NonNull Call<AvanceResponse> call, @NonNull Throwable t) {
                txtDetalle.setText(R.string.error_sin_conexion);
            }
        });
    }

    private void mostrar(AvanceResponse avance) {
        AvanceResponse.Prueba tecnica = avance.tecnica != null ? avance.tecnica : new AvanceResponse.Prueba();
        AvanceResponse.Prueba salud = avance.salud != null ? avance.salud : new AvanceResponse.Prueba();

        switch (tipoTest) {
            case "TECNICO": {
                double puntos = tecnica.calificacion != null ? tecnica.calificacion : 0;
                txtPorcentaje.setText(getString(R.string.result_puntos, puntos));
                txtDetalle.setText(getString(R.string.result_estado_intentos,
                        tecnica.aprobada ? getString(R.string.estado_aprobada) : textoEstado(tecnica),
                        tecnica.intentos));
                retroalimentacion(tecnica.aprobada, pendiente(tecnica),
                        getString(R.string.result_tecnica_bien),
                        getString(R.string.result_tecnica_mal));
                break;
            }
            case "OCUPACIONAL":
                // En salud el trabajador no ve el número, solo el estado
                txtPorcentaje.setText(salud.aprobada ? getString(R.string.estado_apta) : textoEstado(salud));
                txtDetalle.setText(getString(R.string.result_intentos, salud.intentos));
                retroalimentacion(salud.aprobada, pendiente(salud),
                        getString(R.string.result_salud_bien),
                        getString(R.string.result_salud_mal));
                break;
            default:
                txtPorcentaje.setText(getString(R.string.result_porcentaje, avance.porcentaje));
                txtDetalle.setText(avance.estatus != null ? avance.estatus : "");
                retroalimentacion(avance.porcentaje == 100, avance.porcentaje == 0
                                && pendiente(tecnica) && pendiente(salud),
                        getString(R.string.result_general_bien),
                        getString(R.string.result_general_falta, getString(
                                tecnica.aprobada ? R.string.result_falta_salud
                                        : salud.aprobada ? R.string.result_falta_tecnica
                                        : R.string.result_falta_ambas)));
                break;
        }
    }

    private void retroalimentacion(boolean aprobada, boolean sinHacer,
                                   String mensajeBien, String mensajeMal) {
        if (sinHacer) {
            cajaPuntaje.setBackgroundResource(R.drawable.bg_score_yellow);
            txtFeedbackTitulo.setText(R.string.feedback_sin_hacer_titulo);
            txtFeedbackMensaje.setText(R.string.feedback_sin_hacer_mensaje);
        } else if (aprobada) {
            cajaPuntaje.setBackgroundResource(R.drawable.bg_score_green);
            txtFeedbackTitulo.setText(R.string.feedback_aprobado_titulo);
            txtFeedbackMensaje.setText(mensajeBien);
        } else {
            cajaPuntaje.setBackgroundResource(R.drawable.bg_score_red);
            txtFeedbackTitulo.setText(R.string.feedback_reprobado_titulo);
            txtFeedbackMensaje.setText(mensajeMal);
        }
    }

    private static boolean pendiente(AvanceResponse.Prueba p) {
        return p.estado == null || "Pendiente".equals(p.estado);
    }

    /** El estado lo manda el servidor; si no viene, "Pendiente" en el idioma del teléfono. */
    private String textoEstado(AvanceResponse.Prueba p) {
        return p.estado != null ? p.estado : getString(R.string.estado_pendiente);
    }
}
