package com.jumbo.carry.models;

public class Guardado {
    private String idPublicacion;
    private String idUsuario;
    private String id;
    private Long fechaGuardado;

    public Guardado() {
    }

    public Guardado(String idPublicacion, String idUsuario, String id, Long fechaGuardado) {
        this.idPublicacion = idPublicacion;
        this.idUsuario = idUsuario;
        this.id = id;
        this.fechaGuardado = fechaGuardado;
    }

    public String getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(String idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFechaGuardado() {
        return fechaGuardado;
    }

    public void setFechaGuardado(Long fechaGuardado) {
        this.fechaGuardado = fechaGuardado;
    }
}
