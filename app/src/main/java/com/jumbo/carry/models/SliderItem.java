package com.jumbo.carry.models;

public class SliderItem {
    String imagenUrl;
    Long fechaCreacion;

    public SliderItem() {
    }

    public SliderItem(String imagenUrl, Long fechaCreacion) {
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Long getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Long fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
