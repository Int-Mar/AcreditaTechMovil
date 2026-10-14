package com.hygeonix.AcreditaTech;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TestResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        NavActivity.setupNav(this);


        String tipoTest = getIntent().getStringExtra("TIPO_TEST");

        if (tipoTest != null) {
            switch (tipoTest) {
                case "TECNICO":

                    break;
                case "OCUPACIONAL":

                    break;
                case "FINAL":

                    break;
            }
        }
    }
}
