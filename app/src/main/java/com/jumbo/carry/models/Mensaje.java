package com.jumbo.carry.models;

public class Mensaje {
    private String idMensaje;
    private String idEmisor;
    private String idReceptor;
    private String idChat;
    private String contenido;
    private long fechaEnviador;
    private boolean visto;

    public Mensaje() {
    }

    public Mensaje(String idMensaje, String idEmisor, String idReceptor, String idChat, String contenido, long fechaEnviador, boolean visto) {
        this.idMensaje = idMensaje;
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
        this.idChat = idChat;
        this.contenido = contenido;
        this.fechaEnviador = fechaEnviador;
        this.visto = visto;
    }

    public String getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(String idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getFechaEnviador() {
        return fechaEnviador;
    }

    public void setFechaEnviador(long fechaEnviador) {
        this.fechaEnviador = fechaEnviador;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
}
