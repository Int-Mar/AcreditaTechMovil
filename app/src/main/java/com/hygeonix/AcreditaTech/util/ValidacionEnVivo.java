package com.hygeonix.AcreditaTech.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.function.Function;

/**
 * Conecta un campo con su regla de Validador y con un TextView rojo debajo,
 * para mostrar el error al instante mientras la persona escribe.
 *
 * El error aparece cuando el campo ya se tocó (se escribió algo o se salió de él),
 * así no sale en rojo un campo que todavía nadie ha llenado.
 */
public final class ValidacionEnVivo {

    private final EditText campo;
    private final TextView error;
    /** Regresa null si es válido, o el id del mensaje de error (R.string). */
    private final Function<String, Integer> regla;
    private boolean tocado = false;
    private boolean activo = true;

    public ValidacionEnVivo(EditText campo, TextView error, Function<String, Integer> regla) {
        this.campo = campo;
        this.error = error;
        this.regla = regla;

        campo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!activo) return;
                tocado = true;
                revisar();
            }
        });
        campo.setOnFocusChangeListener((v, tieneFoco) -> {
            if (!tieneFoco && activo && campo.isEnabled()) {
                tocado = true;
                revisar();
            }
        });
    }

    /** Revisa el campo, muestra u oculta el error y regresa true si es válido. */
    public boolean validar() {
        tocado = true;
        return revisar();
    }

    /** Para poner texto desde el código (por ejemplo, datos del servidor) sin marcar error. */
    public void ponerTexto(String texto) {
        activo = false;
        campo.setText(texto);
        activo = true;
        tocado = false;
        ocultar();
    }

    public void ocultar() {
        error.setText("");
        error.setVisibility(View.GONE);
    }

    private boolean revisar() {
        Integer mensaje = regla.apply(campo.getText().toString());
        if (mensaje == null || !tocado) {
            ocultar();
            return mensaje == null;
        }
        error.setText(mensaje.intValue());
        error.setVisibility(View.VISIBLE);
        return false;
    }
}
