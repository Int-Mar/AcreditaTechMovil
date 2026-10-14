package com.hygeonix.AcreditaTech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GuidesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos con tu layout principal de guías (asegúrate de que se llame así en res/layout)
        setContentView(R.layout.activity_guias);

        // 1. Activamos la barra de navegación inferior y el mini menú automáticamente
        NavActivity.setupNav(this);

        // 2. Enlazamos los botones de las guías
        Button btnVerGuiaTecnico = findViewById(R.id.btnVerGuiaTecnico);
        Button btnVerGuiaOcupacional = findViewById(R.id.btnVerGuiaOcupacional);

        // Botón Guía Técnica -> Abre la vista de detalle
        if (btnVerGuiaTecnico != null) {
            btnVerGuiaTecnico.setOnClickListener(v -> {
                Intent intent = new Intent(GuidesActivity.this, com.hygeonix.AcreditaTech.DetalleGuiaActivity.class);
                intent.putExtra("TIPO_GUIA", "TECNICO");
                startActivity(intent);
            });
        }

        // Botón Guía Ocupacional -> Abre la vista de detalle
        if (btnVerGuiaOcupacional != null) {
            btnVerGuiaOcupacional.setOnClickListener(v -> {
                Intent intent = new Intent(GuidesActivity.this, com.hygeonix.AcreditaTech.DetalleGuiaActivity.class);
                intent.putExtra("TIPO_GUIA", "OCUPACIONAL");
                startActivity(intent);
            });
        }
    }
}
