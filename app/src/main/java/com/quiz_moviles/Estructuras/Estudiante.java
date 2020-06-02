package com.quiz_moviles.Estructuras;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class Estudiante implements Serializable {
    String cedula, nombre, apellidos;
    Integer edad;

    public Estudiante(String cedula, String nombre, String apellidos, Integer edad) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public Integer getEdad() {
        return edad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudiante)) return false;
        Estudiante that = (Estudiante) o;
        return getCedula().equals(that.getCedula());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCedula());
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cedula", cedula);
            jsonObject.put("nombre", nombre);
            jsonObject.put("apellidos", apellidos);
            jsonObject.put("edad", edad);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
