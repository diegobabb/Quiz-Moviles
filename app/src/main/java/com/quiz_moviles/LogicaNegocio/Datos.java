package com.quiz_moviles.LogicaNegocio;

import java.util.ArrayList;

public class Datos {

    private static Datos instance = null;
    ArrayList<Estudiante> estudiantes;
    ArrayList<Curso> cursos;

    static public Datos getInstance() {
        if (instance == null) {
            instance = new Datos();
        }
        return instance;
    }

    private Datos() {
        this.estudiantes = new ArrayList<>();
        this.cursos = new ArrayList<>();
    }

    public ArrayList<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(ArrayList<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }
}
