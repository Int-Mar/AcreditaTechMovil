package com.hygeonix.AcreditaTech.modelo;

/** Respuesta de GET /api/avance (Backend/controladores/avance_controlador.js). */
public class AvanceResponse {
    /** 0, 50 o 100: cada prueba aprobada suma 50. */
    public int porcentaje;
    /** "En Diagnóstico", "Preparación Completada" o "Vencida". */
    public String estatus;
    public Prueba tecnica;
    /** En salud el servidor no manda la calificación, solo el estado. */
    public Prueba salud;

    public static class Prueba {
        /** Solo viene en la técnica. */
        public Double calificacion;
        /** "Pendiente", "Aprobado", "Apto", "No Aprobado"... */
        public String estado;
        public boolean aprobada;
        public int intentos;
    }
}
