package com.hygeonix.AcreditaTech.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.modelo.LoginRequest;
import com.hygeonix.AcreditaTech.modelo.LoginResponse;
import com.hygeonix.AcreditaTech.modelo.PerfilResponse;
import com.hygeonix.AcreditaTech.network.ApiClient;
import com.hygeonix.AcreditaTech.util.Red;
import com.hygeonix.AcreditaTech.util.SessionManager;
import com.hygeonix.AcreditaTech.util.ValidacionEnVivo;
import com.hygeonix.AcreditaTech.util.Validador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    /** id_tipo_usuario del administrador (reglas.js del servidor). */
    private static final int ROL_ADMINISTRADOR = 2;

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private SessionManager sesion;
    private ValidacionEnVivo validaCorreo;
    private ValidacionEnVivo validaContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sesion = new SessionManager(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView tvErrorCorreo = findViewById(R.id.tvEmailError);
        TextView tvErrorContrasena = findViewById(R.id.tvPasswordError);

        // Errores al instante mientras escribe (mismas reglas que el servidor)
        validaCorreo = new ValidacionEnVivo(etEmail, tvErrorCorreo, Validador::correo);
        validaContrasena = new ValidacionEnVivo(etPassword, tvErrorContrasena, Validador::contrasena);

        btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = etEmail.getText().toString().trim();
        String contrasena = etPassword.getText().toString();

        // 1. Validación en la app: si algo no cumple, no se llama al servidor
        boolean correoOk = validaCorreo.validar();
        boolean contrasenaOk = validaContrasena.validar();
        if (!correoOk) {
            etEmail.requestFocus();
            return;
        }
        if (!contrasenaOk) {
            etPassword.requestFocus();
            return;
        }

        // 2. Solo si cumple se manda al servidor, que hace la validación final
        cargando(true);
        ApiClient.api(this).iniciarSesion(new LoginRequest(correo.toLowerCase(), contrasena))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResponse> call,
                                           @NonNull Response<LoginResponse> respuesta) {
                        LoginResponse cuerpo = respuesta.body();
                        if (respuesta.isSuccessful() && cuerpo != null && cuerpo.token != null) {
                            if (cuerpo.id_tipo_usuario == ROL_ADMINISTRADOR) {
                                cargando(false);
                                Toast.makeText(LoginActivity.this,
                                        R.string.error_solo_trabajador, Toast.LENGTH_LONG).show();
                                return;
                            }
                            sesion.guardarToken(cuerpo.token);
                            cargarNombreYEntrar();
                            return;
                        }

                        cargando(false);
                        // 401: nunca decimos si falló el correo o la contraseña
                        String mensaje = respuesta.code() == 401
                                ? getString(R.string.error_login)
                                : Red.mensajeDeError(respuesta, getString(R.string.error_sin_conexion));
                        Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                        cargando(false);
                        Toast.makeText(LoginActivity.this,
                                R.string.error_sin_conexion, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /** Trae el nombre y el oficio del perfil para los encabezados. Si falla, entra igual. */
    private void cargarNombreYEntrar() {
        ApiClient.api(this).perfil().enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(@NonNull Call<PerfilResponse> call,
                                   @NonNull Response<PerfilResponse> respuesta) {
                if (respuesta.isSuccessful() && respuesta.body() != null
                        && respuesta.body().perfil != null) {
                    sesion.guardarNombre(respuesta.body().perfil.nombre);
                    sesion.guardarOficio(respuesta.body().perfil.nombre_oficio);
                }
                entrar();
            }

            @Override
            public void onFailure(@NonNull Call<PerfilResponse> call, @NonNull Throwable t) {
                entrar();
            }
        });
    }

    private void entrar() {
        startActivity(new Intent(LoginActivity.this, MenuActivity.class));
        finish(); // para que Atrás no regrese al login
    }

    private void cargando(boolean activo) {
        btnLogin.setEnabled(!activo);
        btnLogin.setText(activo ? R.string.cargando : R.string.login_title);
    }
}
