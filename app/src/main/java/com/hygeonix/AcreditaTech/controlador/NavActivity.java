package com.hygeonix.AcreditaTech.controlador;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import androidx.appcompat.widget.SwitchCompat;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.util.Red;
import com.hygeonix.AcreditaTech.util.TemaManager;

public class NavActivity {

    public static void setupNav(Activity activity) {

        // Nombre del trabajador en el encabezado de la pantalla (si lo tiene)
        Red.mostrarNombre(activity);

        // Botón Menú (navMenu) -> Abre el mini menú flotante de ancho completo
        View navMenu = activity.findViewById(R.id.navMenu);
        if (navMenu != null) {
            navMenu.setOnClickListener(v -> {

                View popupView = LayoutInflater.from(activity).inflate(R.layout.layout_miniopc, null);

                // PopupWindow que abarca todo el ancho horizontal de la pantalla
                PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        activity.getResources().getDisplayMetrics().widthPixels,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                );

                // Configurar el Switch de tema dentro del mini menú
                SwitchCompat switchTheme = popupView.findViewById(R.id.switchTheme);
                // Marca el switch según cómo se ve la app ahora (elección guardada o tema del teléfono)
                switchTheme.setChecked(TemaManager.estaEnOscuro(activity));

                View layoutToggle = popupView.findViewById(R.id.layoutToggleTheme);
                layoutToggle.setOnClickListener(view -> {
                    boolean newState = !switchTheme.isChecked();
                    switchTheme.setChecked(newState);
                    popupWindow.dismiss();
                    // Guarda la elección para que se recuerde al volver a abrir la app
                    TemaManager.guardarTema(activity, newState);
                });

                // Botón Cerrar Sesión dentro del mini menú
                View layoutLogout = popupView.findViewById(R.id.layoutLogout);
                layoutLogout.setOnClickListener(view -> {
                    popupWindow.dismiss();
                    // Borra el token y regresa al login sin dejar pantallas atrás
                    Red.irAlLogin(activity);
                });

                // Para mostrar el mini menú flotando arriba de la barra de navegación
                popupWindow.showAsDropDown(navMenu, 0, -500, Gravity.TOP);
            });
        }

        // Botón Inicio (navHome) -> Lleva a MenuActivity
        View navHome = activity.findViewById(R.id.navHome);
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                if (!(activity instanceof MenuActivity)) {
                    Intent intent = new Intent(activity, MenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
        }

        // Botón Guías (navGuides) -> Lleva a GuidesActivity (activity_guias)
        View navGuides = activity.findViewById(R.id.navGuides);
        if (navGuides != null) {
            navGuides.setOnClickListener(v -> {
                if (!(activity instanceof GuidesActivity)) {
                    Intent intent = new Intent(activity, GuidesActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        // Botón Constancia (navConstancia) -> Lleva a ConstanciaActivity (activity_pre_constancia)
        View navConstancia = activity.findViewById(R.id.navConstancia);
        if (navConstancia != null) {
            navConstancia.setOnClickListener(v -> {
                if (!(activity instanceof ConstanciaActivity)) {
                    Intent intent = new Intent(activity, ConstanciaActivity.class);
                    activity.startActivity(intent);
                }
            });
        }

        // Botón Perfil (navPerfil) -> Lleva a PerfilActivity (activity_perfil)
        View navPerfil = activity.findViewById(R.id.navPerfil);
        if (navPerfil != null) {
            navPerfil.setOnClickListener(v -> {
                if (!(activity instanceof PerfilActivity)) {
                    Intent intent = new Intent(activity, PerfilActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
    }
}
