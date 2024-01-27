package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * Actividad que muestra una lista de canciones utilizando un RecyclerView.
 */
public class ListaCanciones extends AppCompatActivity {

    // Identificadores de recursos como constantes finales
    private static final int REPRODUCTOR_ID = R.id.reproductor;
    private static final int LISTA_CANCIONES_ID = R.id.listaCanciones;
    private static final int BUSQUEDA_ID = R.id.busqueda;
    private static final int PERFIL_ID = R.id.perfil;

    /**
     * Método llamado cuando se crea la actividad.
     *
     * @param savedInstanceState Objeto que contiene el estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_canciones);

        // Configuración del RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RawFilesAdapter adapter = new RawFilesAdapter(this, RawResourceReader.getRawResourceList(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Configuración del BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Configuración del listener para manejar los clics en el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Se puede seleccionar un elemento específico al iniciar la actividad
        bottomNavigationView.setSelectedItemId(LISTA_CANCIONES_ID);
    }

    // Listener para manejar los clics en el BottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Determina qué actividad se debe lanzar según el elemento seleccionado
                    int itemId = item.getItemId();
                    if (itemId == REPRODUCTOR_ID) {
                        startActivity(new Intent(ListaCanciones.this, Reproductor.class));
                        return true;
                    } else if (itemId == LISTA_CANCIONES_ID) {
                        // Esta actividad ya está en primer plano, no es necesario hacer nada aquí
                        return true;
                    } else if (itemId == BUSQUEDA_ID) {
                        startActivity(new Intent(ListaCanciones.this, ListaCanciones.class));
                        return true;
                    } else if (itemId == PERFIL_ID) {
                        startActivity(new Intent(ListaCanciones.this, ConfUsuario.class));
                        return true;
                    }
                    return false;
                }
            };
}