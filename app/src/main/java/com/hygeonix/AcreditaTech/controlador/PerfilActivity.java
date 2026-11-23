package com.hygeonix.AcreditaTech.controlador;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.modelo.EditarPerfilRequest;
import com.hygeonix.AcreditaTech.modelo.MensajeResponse;
import com.hygeonix.AcreditaTech.modelo.PerfilResponse;
import com.hygeonix.AcreditaTech.modelo.Usuario;
import com.hygeonix.AcreditaTech.network.ApiClient;
import com.hygeonix.AcreditaTech.util.FotoPerfil;
import com.hygeonix.AcreditaTech.util.Red;
import com.hygeonix.AcreditaTech.util.SessionManager;
import com.hygeonix.AcreditaTech.util.ValidacionEnVivo;
import com.hygeonix.AcreditaTech.util.Validador;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Perfil: muestra los datos de GET /api/perfil y deja cambiar nombre y teléfono
 * con PUT /api/perfil. El correo es solo lectura. La foto viene de foto_url.
 */
public class PerfilActivity extends AppCompatActivity {

    private static final int FONDO_BLOQUEADO = Color.parseColor("#D9D9D9");
    private static final int FONDO_EDITABLE = Color.WHITE;

    private ImageView imgAvatar;
    private TextView tvOficio;
    private EditText etNombre;
    private EditText etCorreo;
    private EditText etTelefono;
    private EditText etAlcaldia;
    private TextView tvError;
    private Button btnEditar;
    private Button btnCancelar;

    private ValidacionEnVivo validaNombre;
    private ValidacionEnVivo validaTelefono;

    private Usuario usuario;
    private boolean editando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Vinculamos con tu layout de perfil
        setContentView(R.layout.activity_perfil);

        // Activamos la barra de navegación inferior y el mini menú flotante automáticamente
        NavActivity.setupNav(this);

        imgAvatar = findViewById(R.id.imgAvatar);
        tvOficio = findViewById(R.id.tvOficioTitle);
        etNombre = findViewById(R.id.etNameProfile);
        etCorreo = findViewById(R.id.etEmailProfile);
        etTelefono = findViewById(R.id.etPhoneProfile);
        etAlcaldia = findViewById(R.id.etAlcaldiaProfile);
        tvError = findViewById(R.id.tvProfileErrorBanner);
        btnEditar = findViewById(R.id.btnEditSaveProfile);
        btnCancelar = findViewById(R.id.btnCancelProfile);

        // Errores al instante mientras edita (mismas reglas que el servidor)
        validaNombre = new ValidacionEnVivo(etNombre, findViewById(R.id.tvNameError), Validador::nombre);
        validaTelefono = new ValidacionEnVivo(etTelefono, findViewById(R.id.tvPhoneError), Validador::telefono);

        btnEditar.setEnabled(false);
        btnEditar.setOnClickListener(v -> {
            if (editando) guardar();
            else modoEdicion(true);
        });
        btnCancelar.setOnClickListener(v -> {
            // Regresa a lo que estaba guardado en el servidor
            mostrarPerfil();
            modoEdicion(false);
        });

        modoEdicion(false);
        cargarPerfil();
    }

    private void cargarPerfil() {
        ApiClient.api(this).perfil().enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(@NonNull Call<PerfilResponse> call,
                                   @NonNull Response<PerfilResponse> respuesta) {
                if (Red.sesionExpirada(PerfilActivity.this, respuesta)) return;
                if (!respuesta.isSuccessful() || respuesta.body() == null
                        || respuesta.body().perfil == null) {
                    mostrarError(Red.mensajeDeError(respuesta, getString(R.string.error_sin_conexion)));
                    return;
                }
                usuario = respuesta.body().perfil;
                SessionManager sesion = new SessionManager(PerfilActivity.this);
                sesion.guardarNombre(usuario.nombre);
                sesion.guardarOficio(usuario.nombre_oficio);
                mostrarPerfil();
                cargarFoto();
            }

            @Override
            public void onFailure(@NonNull Call<PerfilResponse> call, @NonNull Throwable t) {
                mostrarError(getString(R.string.error_sin_conexion));
            }
        });
    }

    private void mostrarPerfil() {
        if (usuario == null) return;
        tvError.setVisibility(View.GONE);
        String oficio = usuario.nombre_oficio != null && !usuario.nombre_oficio.trim().isEmpty()
                ? usuario.nombre_oficio : getString(R.string.perfil_oficio_default);
        String estatus = usuario.estatus != null ? " · " + usuario.estatus : "";
        tvOficio.setText(oficio.toUpperCase() + estatus);
        validaNombre.ponerTexto(usuario.nombre);
        validaTelefono.ponerTexto(usuario.telefono);
        etCorreo.setText(usuario.correo);
        etAlcaldia.setText(usuario.nombre_alcaldia);
        btnEditar.setEnabled(true);
    }

    /** Foto de perfil: la URL la manda el backend (Supabase Storage, bucket "fotos-perfil"). */
    private void cargarFoto() {
        FotoPerfil.cargar(this, usuario.foto_url, imgAvatar, exito -> {
            // Con foto real quitamos el relleno para que llene todo el círculo
            if (exito) imgAvatar.setPadding(0, 0, 0, 0);
        });
    }

    /** Nombre y teléfono se editan desde la app; el correo y la alcaldía no. */
    private void modoEdicion(boolean activo) {
        editando = activo;
        editable(etNombre, activo);
        editable(etTelefono, activo);
        if (activo) {
            etNombre.requestFocus();
            etNombre.setSelection(etNombre.getText().length());
        } else {
            validaNombre.ocultar();
            validaTelefono.ocultar();
        }
        btnCancelar.setVisibility(activo ? View.VISIBLE : View.GONE);
        btnEditar.setText(activo ? R.string.perfil_guardar : R.string.perfil_hacer_cambios);
    }

    private static void editable(EditText campo, boolean activo) {
        campo.setEnabled(activo);
        campo.setFocusable(activo);
        campo.setFocusableInTouchMode(activo);
        campo.setBackgroundColor(activo ? FONDO_EDITABLE : FONDO_BLOQUEADO);
    }

    private void guardar() {
        if (usuario == null) return;

        // 1. Validación en la app: si algo no cumple, no se llama al servidor
        boolean nombreOk = validaNombre.validar();
        boolean telefonoOk = validaTelefono.validar();
        if (!nombreOk) {
            etNombre.requestFocus();
            return;
        }
        if (!telefonoOk) {
            etTelefono.requestFocus();
            return;
        }

        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        if (nombre.equals(usuario.nombre) && telefono.equals(usuario.telefono)) {
            modoEdicion(false); // no cambió nada, no hace falta llamar al servidor
            return;
        }

        // 2. El servidor hace la validación final (400 dato inválido, 409 teléfono repetido)
        btnEditar.setEnabled(false);
        btnCancelar.setEnabled(false);
        ApiClient.api(this).editarPerfil(new EditarPerfilRequest(nombre, telefono))
                .enqueue(new Callback<MensajeResponse>() {
            @Override
            public void onResponse(@NonNull Call<MensajeResponse> call,
                                   @NonNull Response<MensajeResponse> respuesta) {
                if (Red.sesionExpirada(PerfilActivity.this, respuesta)) return;
                btnEditar.setEnabled(true);
                btnCancelar.setEnabled(true);
                if (!respuesta.isSuccessful()) {
                    mostrarError(Red.mensajeDeError(respuesta, getString(R.string.error_sin_conexion)));
                    return;
                }
                usuario.nombre = nombre;
                usuario.telefono = telefono;
                new SessionManager(PerfilActivity.this).guardarNombre(nombre);
                Red.mostrarNombre(PerfilActivity.this);
                tvError.setVisibility(View.GONE);
                modoEdicion(false);
                Toast.makeText(PerfilActivity.this,
                        R.string.perfil_actualizado, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<MensajeResponse> call, @NonNull Throwable t) {
                btnEditar.setEnabled(true);
                btnCancelar.setEnabled(true);
                mostrarError(getString(R.string.error_sin_conexion));
            }
        });
    }

    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
    }
}
