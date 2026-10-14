package com.hygeonix.AcreditaTech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos con tu layout de perfil
        setContentView(R.layout.activity_perfil);

        // Activamos la barra de navegación inferior y el mini menú flotante automáticamente
        NavActivity.setupNav(this);
    }
}