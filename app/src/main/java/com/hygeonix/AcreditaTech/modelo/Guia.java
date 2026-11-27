package com.hygeonix.AcreditaTech.modelo;

/** Una guía de estudio (tabla ctutorial). */
public class Guia {
    public int id_tutorial;
    /** 1 = técnica, 2 = salud ocupacional. */
    public int id_tipo_evaluacion;
    public String codigo;
    public String codigo_tema;
    public String tema;
    public String titulo;
    /** Texto de la guía. */
    public String contenido_multimedia;
    public String infografia_sugerida;
}
