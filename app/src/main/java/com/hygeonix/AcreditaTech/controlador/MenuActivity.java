package com.hygeonix.AcreditaTech.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.modelo.AvanceResponse;
import com.hygeonix.AcreditaTech.modelo.PerfilResponse;
import com.hygeonix.AcreditaTech.network.ApiClient;
import com.hygeonix.AcreditaTech.util.Red;
import com.hygeonix.AcreditaTech.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private TextView txtTitulo;
    private TextView txtPorcentaje;
    private TextView txtEstatus;
    private TextView txtTecnica;
    private TextView txtSalud;
    private ProgressBar progressAvance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        NavActivity.setupNav(this);

        txtTitulo = findViewById(R.id.txtPanelTitulo);
        txtPorcentaje = findViewById(R.id.txtAvancePorcentaje);
        txtEstatus = findViewById(R.id.txtAvanceEstatus);
        txtTecnica = findViewById(R.id.txtEstadoTecnica);
        txtSalud = findViewById(R.id.txtEstadoSalud);
        progressAvance = findViewById(R.id.progressAvance);

        Button btnTecnico = findViewById(R.id.btnVisualizarTecnico);
        Button btnOcupacional = findViewById(R.id.btnVisualizarOcupacional);

        // Botón Test Técnico
        btnTecnico.setOnClickListener(v -> abrirResultado("TECNICO"));
        // Botón Test Ocupacional
        btnOcupacional.setOnClickListener(v -> abrirResultado("OCUPACIONAL"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mostrarOficio();
        // Se vuelve a pedir al regresar, por si hizo un test en la web mientras tanto
        cargarAvance();
    }

    /**
     * El título muestra el oficio con el que se registró (Plomería, Carpintería...).
     * Se guarda al iniciar sesión; si no está (sesión de una versión anterior),
     * se pide a GET /api/perfil. Mientras tanto queda el texto por defecto.
     */
    private void mostrarOficio() {
        String oficio = new SessionManager(this).getOficio();
        if (!oficio.trim().isEmpty()) {
            txtTitulo.setText(oficio);
            return;
        }
        txtTitulo.setText(R.string.user_panel_title);
        ApiClient.api(this).perfil().enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(@NonNull Call<PerfilResponse> call,
                                   @NonNull Response<PerfilResponse> respuesta) {
                if (isFinishing() || isDestroyed()) return;
                if (!respuesta.isSuccessful() || respuesta.body() == null
                        || respuesta.body().perfil == null) return;
                String nuevo = respuesta.body().perfil.nombre_oficio;
                if (nuevo == null || nuevo.trim().isEmpty()) return;
                new SessionManager(MenuActivity.this).guardarOficio(nuevo);
                txtTitulo.setText(nuevo);
            }

            @Override
            public void onFailure(@NonNull Call<PerfilResponse> call, @NonNull Throwable t) {
                // Sin conexión: se queda el título por defecto
            }
        });
    }

    private void abrirResultado(String tipo) {
        Intent intent = new Intent(MenuActivity.this, TestResultActivity.class);
        intent.putExtra("TIPO_TEST", tipo);
        startActivity(intent);
    }

    private void cargarAvance() {
        ApiClient.api(this).avance().enqueue(new Callback<AvanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<AvanceResponse> call,
                                   @NonNull Response<AvanceResponse> respuesta) {
                if (Red.sesionExpirada(MenuActivity.this, respuesta)) return;
                AvanceResponse avance = respuesta.body();
                if (!respuesta.isSuccessful() || avance == null) {
                    txtPorcentaje.setText(Red.mensajeDeError(respuesta,
                            getString(R.string.error_sin_conexion)));
                    return;
                }
                mostrarAvance(avance);
            }

            @Override
            public void onFailure(@NonNull Call<AvanceResponse> call, @NonNull Throwable t) {
                txtPorcentaje.setText(R.string.error_sin_conexion);
            }
        });
    }

    private void mostrarAvance(AvanceResponse avance) {
        txtPorcentaje.setText(getString(R.string.avance_porcentaje, avance.porcentaje));
        progressAvance.setProgress(avance.porcentaje);
        txtEstatus.setText(avance.estatus != null ? avance.estatus : "");

        boolean tecnicaAprobada = avance.tecnica != null && avance.tecnica.aprobada;
        boolean saludAprobada = avance.salud != null && avance.salud.aprobada;
        txtTecnica.setText(getString(R.string.avance_tecnica,
                tecnicaAprobada ? getString(R.string.estado_aprobada) : estado(avance.tecnica)));
        txtSalud.setText(getString(R.string.avance_salud,
                saludAprobada ? getString(R.string.estado_apta) : estado(avance.salud)));
    }

    /** El estado lo manda el servidor; si no viene, "Pendiente". */
    private String estado(AvanceResponse.Prueba prueba) {
        return prueba != null && prueba.estado != null
                ? prueba.estado : getString(R.string.estado_pendiente);
    }
}
