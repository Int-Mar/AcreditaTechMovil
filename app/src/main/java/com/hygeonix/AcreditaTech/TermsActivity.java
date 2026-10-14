package com.hygeonix.AcreditaTech;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Button btnAcceptTerms = findViewById(R.id.btnAcceptTerms);
        btnAcceptTerms.setOnClickListener(v -> {
            Intent intent = new Intent(TermsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}