package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para manejar la visualización de archivos raw en un RecyclerView.
 */
public class RawFilesAdapter extends RecyclerView.Adapter<RawFilesAdapter.ViewHolder> {

    private Context context;
    private List<String> rawFilesList;
    private List<Song> canciones;

    /**
     * Constructor de la clase RawFilesAdapter.
     *
     * @param context      Contexto de la aplicación.
     * @param rawFilesList Lista de nombres de recursos raw.
     */
    public RawFilesAdapter(Context context, List<String> rawFilesList) {
        this.context = context;
        this.rawFilesList = rawFilesList;
        this.canciones = new ArrayList<>();

        // Iterar a través de los archivos raw y obtener información de cada canción
        for (int i = 0; i < rawFilesList.size(); i++) {
            Song cancion = getSongMetadata(rawFilesList.get(i));
            canciones.add(cancion);
        }
    }

    /**
     * Obtiene la lista de canciones.
     *
     * @return Lista de objetos Song.
     */
    public List<Song> getCanciones() {
        return canciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño del elemento de la lista
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.archivo_raw, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fileName = rawFilesList.get(position);

        // Obtener información de la canción actual
        Song song = getSongMetadata(fileName);

        // Establecer la información en los elementos de la vista
        holder.textViewFileName.setText(song.getTitle());
        holder.textViewArtist.setText(song.getArtist());
        holder.textViewDuration.setText(song.getDurationMMSS());

        // Establecer la carátula en el ImageView
        if (song.getAlbumArt() != null) {
            holder.imageViewAlbumArt.setImageBitmap(song.getAlbumArt());
        } else {
            // Si no hay carátula, establecer una imagen predeterminada
            holder.imageViewAlbumArt.setImageResource(R.drawable.imagenreproductor);
        }

        // Configurar el evento de clic en el elemento de la lista
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la posición del elemento clicado
                int clickedPosition = holder.getAdapterPosition();

                // Crear un Intent para enviar la posición al reproductor
                Intent intent = new Intent(context, Reproductor.class);
                intent.putExtra("PosicionCancion", clickedPosition);

                // Iniciar la actividad del reproductor con la posición seleccionada
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rawFilesList.size();
    }

    /**
     * Clase interna que representa una vista de elemento de la lista.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFileName;
        public TextView textViewArtist;
        public TextView textViewDuration;
        public ImageView imageViewAlbumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewFileName = itemView.findViewById(R.id.textViewFileName);
            textViewArtist = itemView.findViewById(R.id.textViewArtist);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            imageViewAlbumArt = itemView.findViewById(R.id.imageViewAlbumArt);
        }
    }

    /**
     * Obtiene metadatos de una canción a partir de un archivo raw.
     *
     * @param fileName Nombre del archivo raw.
     * @return Objeto Song con los metadatos de la canción.
     */
    private Song getSongMetadata(String fileName) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // Obtener el identificador del recurso raw
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier(fileName, "raw", context.getPackageName());
            Uri rawResourceUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resourceId);

            // Configurar el MediaMetadataRetriever con el recurso raw
            retriever.setDataSource(context, rawResourceUri);

            // Obtener la información de la canción
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            // Obtener la carátula de la canción (si está disponible)
            byte[] albumArtBytes = retriever.getEmbeddedPicture();
            Bitmap albumArtBitmap = (albumArtBytes != null) ?
                    BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length) : null;

            // Crear y devolver un objeto Song con la información obtenida
            return new Song(title, artist, duration, albumArtBitmap, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, devolver una canción con mensaje de error
            return new Song("Error al obtener la información", "", "", null, "");
        } finally {
            // Liberar los recursos del MediaMetadataRetriever
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


