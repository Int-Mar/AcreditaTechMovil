package com.hygeonix.AcreditaTech.modelo;

/** Respuesta de GET /api/constancia. */
public class ConstanciaResponse {
    public boolean tiene_constancia;
    /** true si aprobó las dos pruebas y aún no la ha generado. */
    public boolean puede_generar;
    public String codigo;
    public String emitida;
    public String vigente_hasta;
    /** "Vigente" o "Vencida". */
    public String estado;
}
