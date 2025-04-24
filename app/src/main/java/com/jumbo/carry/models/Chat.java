package com.jumbo.carry.models;

import java.util.ArrayList;

public class Chat {
    private String idChat;
    private boolean escribiendo;
    private long fechaCreacion;
    private String idUsuarioDestino;
    private String idUsuarioEmisor;
    private int idNotificacion;
    private ArrayList<String>ids;
    public Chat() {
    }

    public Chat(String idChat, boolean escribiendo, long fechaCreacion, String idUsuarioDestino, String idUsuarioEmisor, int idNotificacion, ArrayList<String> ids) {
        this.idChat = idChat;
        this.escribiendo = escribiendo;
        this.fechaCreacion = fechaCreacion;
        this.idUsuarioDestino = idUsuarioDestino;
        this.idUsuarioEmisor = idUsuarioEmisor;
        this.idNotificacion = idNotificacion;
        this.ids = ids;
    }

    public boolean isEscribiendo() {
        return escribiendo;
    }

    public void setEscribiendo(boolean escribiendo) {
        this.escribiendo = escribiendo;
    }

    public long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getIdUsuarioDestino() {
        return idUsuarioDestino;
    }

    public void setIdUsuarioDestino(String idUsuarioDestino) {
        this.idUsuarioDestino = idUsuarioDestino;
    }

    public String getIdUsuarioEmisor() {
        return idUsuarioEmisor;
    }

    public void setIdUsuarioEmisor(String idUsuarioEmisor) {
        this.idUsuarioEmisor = idUsuarioEmisor;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }
}
