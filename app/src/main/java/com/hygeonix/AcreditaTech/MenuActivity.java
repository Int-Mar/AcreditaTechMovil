package com.hygeonix.AcreditaTech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        NavActivity.setupNav(this);

        Button btnTecnico = findViewById(R.id.btnVisualizarTecnico);
        Button btnOcupacional = findViewById(R.id.btnVisualizarOcupacional);
        Button btnFinal = findViewById(R.id.btnVisualizarFinal);

        // Botón Test Técnico
        btnTecnico.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, TestResultActivity.class);
            intent.putExtra("TIPO_TEST", "TECNICO");
            startActivity(intent);
        });

        // Botón Test Ocupacional
        btnOcupacional.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, TestResultActivity.class);
            intent.putExtra("TIPO_TEST", "OCUPACIONAL");
            startActivity(intent);
        });

        // Botón Test Final
        btnFinal.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, TestResultActivity.class);
            intent.putExtra("TIPO_TEST", "FINAL");
            startActivity(intent);
        });
    }
}