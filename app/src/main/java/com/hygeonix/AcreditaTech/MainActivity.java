package com.hygeonix.AcreditaTech;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ¡Importante! El Splash (MainActivity) no lleva barra inferior,
        // por lo que quitamos NavActivity.setupNav(this) de aquí.

        // Cargamos tu diseño de la pantalla de inicio
        setContentView(R.layout.activity_inicio);

        // Temporizador de 2 segundos para pasar a la pantalla de Términos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                startActivity(intent);
                finish(); // Cierra esta activity para que al presionar "atrás" no regrese al splash
            }
        }, 2000); // 2000 milisegundos = 2 segundos
    }
}