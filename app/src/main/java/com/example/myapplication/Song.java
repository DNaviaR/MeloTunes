package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * Clase que representa una canción y contiene información relevante sobre la misma.
 * Implementa la interfaz Serializable para poder enviar objetos de esta clase a través de intents.
 */
public class Song implements Serializable { //para poder hacer el intent

    // Atributos de la clase
    private String title;            // Título de la canción
    private String artist;           // Artista de la canción
    private String duration;         // Duración de la canción en milisegundos (representada como String)
    private Bitmap albumArt;         // Imagen del álbum de la canción (representada como Bitmap)
    private String rawResourceName;  // Nombre del recurso crudo asociado a la canción

    /**
     * Constructor de la clase Song.
     *
     * @param title           Título de la canción.
     * @param artist          Artista de la canción.
     * @param duration        Duración de la canción en milisegundos (representada como String).
     * @param albumArt        Imagen del álbum de la canción (representada como Bitmap).
     * @param rawResourceName Nombre del recurso crudo asociado a la canción.
     */
    public Song(String title, String artist, String duration, Bitmap albumArt, String rawResourceName) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumArt = albumArt;
        this.rawResourceName = rawResourceName;
    }

    /**
     * Obtiene el título de la canción.
     *
     * @return Título de la canción.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Obtiene el artista de la canción.
     *
     * @return Artista de la canción.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Obtiene la duración de la canción en milisegundos (representada como String).
     *
     * @return Duración de la canción.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Obtiene la duración formateada en minutos y segundos (por ejemplo, "mm:ss").
     *
     * @return Duración formateada de la canción.
     */
    public String getDurationMMSS() {
        String duracionString = this.duration; // Tu duración en milisegundos representada como String

        // Convertir el String a valor numérico (long)
        long duracionEnMilisegundos = Long.parseLong(duracionString);

        // Calcular minutos y segundos
        long segundos = (duracionEnMilisegundos / 1000) % 60;
        long minutos = (duracionEnMilisegundos / (1000 * 60)) % 60;

        // Formatear los minutos y segundos en un formato legible (por ejemplo, "mm:ss")
        String duracionFormateada = String.format("%02d:%02d", minutos, segundos);
        return  duracionFormateada;
    }

    /**
     * Obtiene la imagen del álbum de la canción.
     *
     * @return Imagen del álbum de la canción (representada como Bitmap).
     */
    public Bitmap getAlbumArt() {
        return albumArt;
    }

    /**
     * Obtiene el nombre del recurso crudo asociado a la canción.
     *
     * @return Nombre del recurso crudo asociado a la canción.
     */
    public String getRawResourceName() {
        return rawResourceName;
    }
}
