package com.hygeonix.AcreditaTech.controlador;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.adaptador.GuiaAdapter;
import com.hygeonix.AcreditaTech.modelo.Guia;
import com.hygeonix.AcreditaTech.repository.GuiaRepository;
import com.hygeonix.AcreditaTech.util.Red;

import java.util.List;

import retrofit2.Response;

/**
 * Muestra las guías de un tipo (técnica u ocupacional) del oficio del trabajador,
 * una tarjeta por guía en un RecyclerView. El servidor ya filtra por oficio;
 * la app solo muestra lo que llega.
 */
public class DetalleGuiaActivity extends AppCompatActivity {

    private static final int TIPO_TECNICA = 1;
    private static final int TIPO_SALUD = 2;

    private TextView txtEstado;
    private RecyclerView recyclerGuias;
    private final GuiaAdapter adaptador = new GuiaAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalleguia);

        NavActivity.setupNav(this);

        boolean ocupacional = "OCUPACIONAL".equals(getIntent().getStringExtra("TIPO_GUIA"));
        int tipo = ocupacional ? TIPO_SALUD : TIPO_TECNICA;

        TextView txtSubtipo = findViewById(R.id.txtGuideSubtype);
        txtSubtipo.setText(ocupacional ? R.string.guia_ocupacional : R.string.guia_tecnica);

        TextView txtTitulo = findViewById(R.id.txtGuideTitleHeader);
        txtTitulo.setText(ocupacional ? R.string.guias_salud_titulo : R.string.guias_tecnicas_titulo);

        txtEstado = findViewById(R.id.txtGuiasEstado);
        recyclerGuias = findViewById(R.id.recyclerGuias);
        recyclerGuias.setAdapter(adaptador);

        mostrarMensaje(getString(R.string.cargando));

        new GuiaRepository(this).obtenerGuias(tipo, new GuiaRepository.Resultado() {
            @Override
            public void alTerminar(@NonNull List<Guia> guias) {
                if (guias.isEmpty()) {
                    mostrarMensaje(getString(R.string.guias_vacias));
                    return;
                }
                adaptador.setGuias(guias);
                txtEstado.setVisibility(View.GONE);
                recyclerGuias.setVisibility(View.VISIBLE);
            }

            @Override
            public void alFallar(@NonNull Response<?> respuesta) {
                if (Red.sesionExpirada(DetalleGuiaActivity.this, respuesta)) return;
                mostrarMensaje(Red.mensajeDeError(respuesta, getString(R.string.error_sin_conexion)));
            }

            @Override
            public void sinConexion() {
                mostrarMensaje(getString(R.string.guias_sin_conexion));
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        recyclerGuias.setVisibility(View.GONE);
        txtEstado.setText(mensaje);
        txtEstado.setVisibility(View.VISIBLE);
    }
}
