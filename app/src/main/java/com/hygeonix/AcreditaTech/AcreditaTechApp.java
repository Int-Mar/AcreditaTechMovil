package com.hygeonix.AcreditaTech;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hygeonix.AcreditaTech.util.BarrasSistema;
import com.hygeonix.AcreditaTech.util.TemaManager;

/** Se ejecuta una vez al abrir la app, antes de cualquier pantalla. */
public class AcreditaTechApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Aplica el tema claro/oscuro que el usuario eligió la última vez
        TemaManager.aplicarTemaGuardado(this);

        // En cada pantalla, deja libre el espacio de la barra de estado y la de navegación
        // (en Android 15+ el sistema dibuja la app de borde a borde)
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                // Aquí el layout de la pantalla ya está puesto (setContentView en onCreate)
                BarrasSistema.aplicar(activity);
            }

            @Override public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) { }
            @Override public void onActivityResumed(@NonNull Activity activity) { }
            @Override public void onActivityPaused(@NonNull Activity activity) { }
            @Override public void onActivityStopped(@NonNull Activity activity) { }
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }
            @Override public void onActivityDestroyed(@NonNull Activity activity) { }
        });
    }
}
