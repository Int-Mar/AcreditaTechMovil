package com.hygeonix.AcreditaTech.modelo;

/** Datos del perfil, tal como los manda GET /api/perfil dentro de "perfil". */
public class Usuario {
    public int id_usuario;
    public String nombre;
    public String correo;
    public String telefono;
    public String curp;
    public String foto_url;
    public String estatus;
    public String fecha_registro;
    public int id_alcaldia;
    public String nombre_alcaldia;
    public int id_oficio;
    public String nombre_oficio;
}
