package com.hygeonix.AcreditaTech.modelo;

/** Cuerpo de POST /api/acceso/iniciar-sesion. */
public class LoginRequest {
    public String correo;
    public String contrasena;

    public LoginRequest(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }
}
