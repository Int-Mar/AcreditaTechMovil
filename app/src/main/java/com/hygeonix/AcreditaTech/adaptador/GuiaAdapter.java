package com.hygeonix.AcreditaTech.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hygeonix.AcreditaTech.R;
import com.hygeonix.AcreditaTech.modelo.Guia;

import java.util.ArrayList;
import java.util.List;

/** Una tarjeta por cada guía del oficio (normalmente 5, según las que traiga el servidor). */
public class GuiaAdapter extends RecyclerView.Adapter<GuiaAdapter.TarjetaGuia> {

    private final List<Guia> guias = new ArrayList<>();

    public void setGuias(List<Guia> nuevas) {
        guias.clear();
        if (nuevas != null) guias.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TarjetaGuia onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guia, parent, false);
        return new TarjetaGuia(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TarjetaGuia tarjeta, int posicion) {
        Guia guia = guias.get(posicion);
        Context contexto = tarjeta.itemView.getContext();

        String numero = contexto.getString(R.string.guia_numero, posicion + 1, guias.size());
        if (tieneTexto(guia.codigo_tema)) numero += " · " + guia.codigo_tema.trim();
        tarjeta.txtNumero.setText(numero);

        tarjeta.txtTema.setText(tieneTexto(guia.tema) ? guia.tema.trim()
                : contexto.getString(R.string.guia_generica, posicion + 1));

        mostrarSiHay(tarjeta.txtTitulo, guia.titulo);
        mostrarSiHay(tarjeta.txtContenido, guia.contenido_multimedia);
        mostrarSiHay(tarjeta.txtInfografia, tieneTexto(guia.infografia_sugerida)
                ? contexto.getString(R.string.guia_infografia, guia.infografia_sugerida.trim()) : null);
    }

    @Override
    public int getItemCount() {
        return guias.size();
    }

    private static boolean tieneTexto(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static void mostrarSiHay(TextView vista, String texto) {
        if (tieneTexto(texto)) {
            vista.setText(texto.trim());
            vista.setVisibility(View.VISIBLE);
        } else {
            vista.setVisibility(View.GONE);
        }
    }

    static class TarjetaGuia extends RecyclerView.ViewHolder {
        final TextView txtNumero;
        final TextView txtTema;
        final TextView txtTitulo;
        final TextView txtContenido;
        final TextView txtInfografia;

        TarjetaGuia(@NonNull View vista) {
            super(vista);
            txtNumero = vista.findViewById(R.id.txtGuiaNumero);
            txtTema = vista.findViewById(R.id.txtGuiaTema);
            txtTitulo = vista.findViewById(R.id.txtGuiaTitulo);
            txtContenido = vista.findViewById(R.id.txtGuiaContenido);
            txtInfografia = vista.findViewById(R.id.txtGuiaInfografia);
        }
    }
}
