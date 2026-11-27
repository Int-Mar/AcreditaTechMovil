package com.hygeonix.AcreditaTech.modelo;

/**
 * Cuerpo de PUT /api/perfil. El backend nuevo solo acepta nombre y teléfono
 * (la alcaldía y el oficio no se editan).
 */
public class EditarPerfilRequest {
    public String nombre;
    public String telefono;

    public EditarPerfilRequest(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }
}
