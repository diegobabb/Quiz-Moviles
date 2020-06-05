package com.quiz_moviles.LogicaNegocio;

import java.util.Objects;

public class Curso {

    String codigo, descripcion;
    Integer creditos;

    public Curso(String codigo, String descripcion, Integer creditos) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.creditos = creditos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCreditos() {
        return creditos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curso)) return false;
        Curso curso = (Curso) o;
        return getCodigo().equals(curso.getCodigo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodigo());
    }
}
