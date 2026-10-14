package com.hygeonix.AcreditaTech;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ConstanciaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos con tu layout de pre-constancia
        setContentView(R.layout.activity_pre_constancia);

        // Activamos la barra de navegación inferior y el mini menú automáticamente
        NavActivity.setupNav(this);
    }
}