package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la actividad principal del reproductor de música en la aplicación.
 * Permite reproducir, pausar y cambiar entre canciones, así como visualizar la información
 * de la canción actual. También utiliza un BottomNavigationView para navegar entre diferentes
 * actividades de la aplicación.
 *
 * Esta clase interactúa con una lista de canciones y utiliza MediaPlayer para reproducir audio.
 *
 */
public class Reproductor extends AppCompatActivity {

    // Declaración de variables miembro
    static MediaPlayer music;
    private ImageView fondoCancion;
    private ProgressBar barraProgreso;
    private TextView titulo;
    private TextView artista;
    private ImageButton play;
    private ImageButton btnNext;
    private ImageButton btnBefore;

    // Identificadores de recursos como constantes finales
    private static final int REPRODUCTOR_ID = R.id.reproductor;
    private static final int LISTA_CANCIONES_ID = R.id.listaCanciones;
    private static final int BUSQUEDA_ID = R.id.busqueda;
    private static final int PERFIL_ID = R.id.perfil;

    private List<Song> canciones = new ArrayList<>();
    private int currentSongIndex = 0;

    /**
     * Método que se llama cuando se crea la actividad.
     * Aquí se realiza la inicialización de la interfaz de usuario, se configuran los elementos
     * y se asignan escuchas (listeners) a los eventos de clic.
     *
     * @param savedInstanceState La instancia previamente guardada del estado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Llama al método onCreate de la clase padre (AppCompatActivity).
        super.onCreate(savedInstanceState);

        // Establece el diseño de la actividad a partir del archivo XML activity_reproductor.
        setContentView(R.layout.activity_reproductor);

        // Crear un adaptador para obtener la lista de canciones desde los recursos raw
        RawFilesAdapter adapter = new RawFilesAdapter(this, RawResourceReader.getRawResourceList(this));
        // Obtener la lista de canciones del adaptador
        canciones = adapter.getCanciones();

        // Inicializar los elementos de la interfaz mediante sus identificadores de vista
        fondoCancion = findViewById(R.id.imageSong);
        barraProgreso = findViewById(R.id.seekBar);
        barraProgreso.setEnabled(false);
        titulo = findViewById(R.id.titulo);
        artista = findViewById(R.id.artista);
        play = findViewById(R.id.play);
        btnNext = findViewById(R.id.forward);
        btnBefore = findViewById(R.id.backward);

        // Obtener la referencia del BottomNavigationView mediante su identificador de vista
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Configurar el listener para manejar los clics en el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Seleccionar un elemento específico al iniciar la actividad
        bottomNavigationView.setSelectedItemId(REPRODUCTOR_ID);

        // Inicializar el reproductor de música
        music = new MediaPlayer();

        // Intent para obtener la posición de la canción desde la actividad anterior
        Intent intent = getIntent();
        if (intent != null) {
            // Obtener la posición de la canción del intent
            int posicionCancion = intent.getIntExtra("PosicionCancion", -1);
            if (posicionCancion != -1 && posicionCancion < canciones.size()) {
                // Si la posición es válida, obtener la canción correspondiente
                Song cancion = canciones.get(posicionCancion);
                try {
                    // Establecer la fuente de datos del reproductor con la canción seleccionada
                    music.setDataSource(getResources().openRawResourceFd(getResources().getIdentifier(cancion.getRawResourceName(), "raw", getPackageName())));
                    // Preparar el reproductor
                    music.prepare();
                    // Actualizar la interfaz con la información de la canción
                    updateAlbumCover(cancion);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Si no se proporciona una posición válida, reproducir la primera canción
                try {
                    if (canciones.size() > 0) {
                        // Obtener la primera canción de la lista
                        music.setDataSource(getResources().openRawResourceFd(getResources().getIdentifier(canciones.get(0).getRawResourceName(), "raw", getPackageName())));
                        // Preparar el reproductor
                        music.prepare();
                        // Actualizar la interfaz con la información de la primera canción
                        updateAlbumCover(canciones.get(0));
                    } else {
                        // Manejar el caso en el que no haya canciones disponibles
                        Log.d("Reproductor", "No hay canciones disponibles.");
                        Toast.makeText(Reproductor.this, "No hay canciones", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Manejar el caso en el que no se proporcionó un intent válido
            Log.d("Reproductor", "No se proporcionó un intent válido.");
            Toast.makeText(Reproductor.this, "Canción no válida", Toast.LENGTH_SHORT).show();
        }

        // Configuración de un botón "Siguiente canción"
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicNext(); // Llama al método para pasar a la siguiente canción
            }
        });

        // Configuración de un botón "Canción anterior"
        btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicBefore(); // Llama al método para pasar a la canción anterior
            }
        });
    }

    /**
     * Listener que maneja los clics en los elementos del BottomNavigationView.
     * Este listener determina qué actividad se debe lanzar según el elemento seleccionado.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                /**
                 * Método invocado cuando se selecciona un elemento en el BottomNavigationView.
                 *
                 * @param item El elemento de menú que se seleccionó.
                 * @return Devuelve `true` si se maneja la selección, `false` en caso contrario.
                 */
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Determinar qué actividad se debe lanzar según el elemento seleccionado
                    int itemId = item.getItemId();

                    // Verificar el ID del elemento seleccionado y lanzar la actividad correspondiente
                    if (itemId == REPRODUCTOR_ID) {
                        // Esta actividad ya está en primer plano, no necesitas hacer nada aquí
                        return true;
                    } else if (itemId == LISTA_CANCIONES_ID || itemId == BUSQUEDA_ID) {
                        // Si se selecciona la lista de canciones o la opción de búsqueda,
                        // lanzar la actividad ListaCanciones
                        startActivity(new Intent(Reproductor.this, ListaCanciones.class));
                        return true;
                    } else if (itemId == PERFIL_ID) {
                        // Si se selecciona la opción de perfil, lanzar la actividad ConfUsuario
                        startActivity(new Intent(Reproductor.this, ConfUsuario.class));
                        return true;
                    }

                    // Si no se reconoce el elemento seleccionado, devolver false
                    return false;
                }
            };


    /**
     * Actualiza la interfaz de usuario con la información de la canción proporcionada.
     *
     * @param song La canción cuya información se utilizará para actualizar la interfaz.
     */
    private void updateAlbumCover(Song song) {
        // Establecer la imagen del álbum de la canción en el ImageView correspondiente
        fondoCancion.setImageBitmap(song.getAlbumArt());

        // Establecer el texto del artista de la canción en el TextView correspondiente
        artista.setText(song.getArtist());

        // Establecer el título de la canción en el TextView correspondiente
        titulo.setText(song.getTitle());
    }

    /**
     * Maneja el inicio y la pausa de la reproducción de la música cuando se hace clic en el botón de reproducción/pausa.
     *
     * @param v La vista que activa este método al ser clicada (puede ser el botón de reproducción/pausa).
     */
    public void musicPlay(View v) {
        // Verifica si la música actualmente no se está reproduciendo
        if (!music.isPlaying()) {
            // Inicia la reproducción de la música
            music.start();

            // Cambia la imagen del botón a la de pausa
            play.setImageResource(R.drawable.icon__pause);

            // Actualiza la barra de progreso de reproducción
            actualizarBarra();
        } else {
            // Pausa la reproducción de la música
            music.pause();

            // Cambia la imagen del botón a la de reproducción
            play.setImageResource(R.drawable.icon__play);
        }
    }


    /**
     * Actualiza la barra de progreso de la canción.
     * Se supone que 'songDuration' es la duración total de la canción en milisegundos.
     */
    public void actualizarBarra() {
        // Obtener la duración total de la canción en milisegundos
        int songDuration = music.getDuration();

        // Actualizar la posición de la ProgressBar cada segundo
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Verificar si el reproductor de música es válido
                if (music != null) {
                    // Obtener la posición actual de reproducción
                    int currentPosition = music.getCurrentPosition();

                    // Establecer el valor máximo de la ProgressBar como la duración total de la canción
                    barraProgreso.setMax(songDuration);

                    // Establecer el progreso de la ProgressBar según la posición actual de reproducción
                    barraProgreso.setProgress(currentPosition);
                }

                // Programar la ejecución de este Runnable después de 1000 milisegundos (1 segundo)
                handler.postDelayed(this, 1000);
            }
        };

        // Iniciar la actualización de la barra de progreso después de 1000 milisegundos (1 segundo)
        handler.postDelayed(runnable, 1000);
    }


    /**
     * Cambia a la siguiente canción en la lista.
     * Si la canción actual no es la última de la lista, pasa a la siguiente canción.
     * Si es la última, vuelve al inicio de la lista.
     */
    private void musicNext() {
        // Verificar si hay más canciones en la lista
        if (currentSongIndex < canciones.size() - 1) {
            // Incrementar el índice para pasar a la siguiente canción
            currentSongIndex++;
        } else {
            // Si es la última canción, volver al inicio de la lista
            currentSongIndex = 0;
        }
        try {
            // Resetear el reproductor para cargar la nueva canción
            music.reset();

            // Establecer la fuente de datos del reproductor con la nueva canción
            music.setDataSource(getResources().openRawResourceFd(getResources().getIdentifier(canciones.get(currentSongIndex).getRawResourceName(), "raw", getPackageName())));

            // Preparar el reproductor
            music.prepare();

            // Actualizar la interfaz con la información de la nueva canción
            updateAlbumCover(canciones.get(currentSongIndex));

            // Iniciar la reproducción de la nueva canción
            music.start();

            // Actualizar la barra de progreso de la canción
            actualizarBarra();

            // Cambiar la imagen del botón a pausa
            play.setImageResource(R.drawable.icon__pause);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cambia a la canción anterior en la lista.
     * Si la canción actual no es la primera de la lista, retrocede a la canción anterior.
     * Si es la primera, se desplaza al final de la lista.
     */
    public void musicBefore() {
        // Verificar si la canción actual no es la primera de la lista
        if (currentSongIndex > 0) {
            // Decrementar el índice para retroceder a la canción anterior
            currentSongIndex--;
        } else {
            // Si es la primera canción, ir al final de la lista
            currentSongIndex = canciones.size() - 1;
        }
        try {
            // Resetear el reproductor para cargar la nueva canción
            music.reset();

            // Establecer la fuente de datos del reproductor con la nueva canción
            music.setDataSource(getResources().openRawResourceFd(getResources().getIdentifier(canciones.get(currentSongIndex).getRawResourceName(), "raw", getPackageName())));

            // Preparar el reproductor
            music.prepare();

            // Actualizar la interfaz con la información de la nueva canción
            updateAlbumCover(canciones.get(currentSongIndex));

            // Iniciar la reproducción de la nueva canción
            music.start();

            // Cambiar la imagen del botón a pausa
            play.setImageResource(R.drawable.icon__pause);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
