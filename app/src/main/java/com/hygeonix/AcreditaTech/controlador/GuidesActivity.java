package com.hygeonix.AcreditaTech.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;

public class GuidesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guias);

        // Activamos la barra de navegación inferior y el mini menú automáticamente
        NavActivity.setupNav(this);

        // Enlazamos los botones de las guías
        Button btnVerGuiaTecnico = findViewById(R.id.btnVerGuiaTecnico);
        Button btnVerGuiaOcupacional = findViewById(R.id.btnVerGuiaOcupacional);

        // Botón Guía Técnica -> Abre la vista de detalle
        if (btnVerGuiaTecnico != null) {
            btnVerGuiaTecnico.setOnClickListener(v -> {
                Intent intent = new Intent(GuidesActivity.this, DetalleGuiaActivity.class);
                intent.putExtra("TIPO_GUIA", "TECNICO");
                startActivity(intent);
            });
        }

        // Botón Guía Ocupacional -> Abre la vista de detalle
        if (btnVerGuiaOcupacional != null) {
            btnVerGuiaOcupacional.setOnClickListener(v -> {
                Intent intent = new Intent(GuidesActivity.this, DetalleGuiaActivity.class);
                intent.putExtra("TIPO_GUIA", "OCUPACIONAL");
                startActivity(intent);
            });
        }
    }
}
