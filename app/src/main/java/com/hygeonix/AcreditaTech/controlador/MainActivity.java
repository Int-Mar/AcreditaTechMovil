package com.hygeonix.AcreditaTech.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inicio);

        // Temporizador de 2 segundos. Si ya hay sesión guardada va directo al menú;
        // si no, pasa a Términos y luego al login.
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean haySesion = new SessionManager(this).haySesion();
            Class<?> siguiente = haySesion ? MenuActivity.class : TermsActivity.class;
            startActivity(new Intent(MainActivity.this, siguiente));
            finish();
        }, 2000); // 2000 milisegundos = 2 segundos
    }
}
