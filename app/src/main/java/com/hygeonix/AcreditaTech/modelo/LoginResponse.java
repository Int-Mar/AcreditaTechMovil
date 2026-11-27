package com.hygeonix.AcreditaTech.modelo;

/** Respuesta de POST /api/acceso/iniciar-sesion. */
public class LoginResponse {
    public String mensaje;
    public String token;
    /** 1 = trabajador, 2 = administrador (ver reglas.js del servidor). */
    public int id_tipo_usuario;
}
