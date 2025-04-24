package com.jumbo.carry.models;

public class Usuario {
    private String id;
    private String email;
    private String usuario;
    private String contraseña;
    private String numero;
    private String foto_perfil;
    private String foto_portada;
    private Long fechaCreacion;
    private Long ultimaConexion;
    private boolean online;

    public Usuario(){}

    public Usuario(String id, String email, String usuario, String contraseña, String numero, String foto_perfil, String foto_portada, Long fechaCreacion, Long ultimaConexion, boolean online) {
        this.id = id;
        this.email = email;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.numero = numero;
        this.foto_perfil = foto_perfil;
        this.foto_portada = foto_portada;
        this.fechaCreacion = fechaCreacion;
        this.ultimaConexion = ultimaConexion;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFoto_perfil() {
        return foto_perfil;
    }

    public void setFoto_perfil(String foto_perfil) {
        this.foto_perfil = foto_perfil;
    }

    public String getFoto_portada() {
        return foto_portada;
    }

    public void setFoto_portada(String foto_portada) {
        this.foto_portada = foto_portada;
    }

    public Long getUltimaConexion() {
        return ultimaConexion;
    }

    public void setUltimaConexion(Long ultimaConexion) {
        this.ultimaConexion = ultimaConexion;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
