package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ConfUsuario extends AppCompatActivity {
    // Identificadores de recursos como constantes finales
    private static final int REPRODUCTOR_ID = R.id.reproductor;
    private static final int LISTA_CANCIONES_ID = R.id.listaCanciones;
    private static final int BUSQUEDA_ID = R.id.busqueda;
    private static final int PERFIL_ID = R.id.perfil;
    private String idiomaActual;
    private TextView correo;
    private TextView name;
    private TextView genero;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_usuario);
        correo=findViewById(R.id.introducircorreoelectronico);
        name=findViewById(R.id.introducirnombre);
        genero=findViewById(R.id.introducirgenero);
        Spinner spinner = findViewById(R.id.seleccionaridioma);
        List<String> elementos = Arrays.asList("Español", "Inglés", "Alemán", "Italiano", "Francés"); // Ejemplo de lista de elementos

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, elementos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // El usuario está autenticado, obtén su correo electrónico
            String userEmail = user.getEmail();
            String id = user.getUid();
            // Obtener el usuario actualmente autenticado
            db= FirebaseFirestore.getInstance();
            db.collection("usuarios").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String gender=documentSnapshot.getString("genero");
                        name.setText(nombre);
                        genero.setText(gender);

                        // Establecer el correo electrónico aquí también si es necesario
                        if (userEmail != null) {
                            correo.setText(userEmail);
                        }
                    }
                }
            });
            if (userEmail != null) {
                correo.setText(userEmail);
            }
        }

        spinner.setAdapter(adapter);
        idiomaActual=obtenerIdiomaActual();
        switch (idiomaActual) {
            case "en":
                spinner.setSelection(1); // Inglés
                break;
            case "de":
                spinner.setSelection(2); // Alemán
                break;
            case "it":
                spinner.setSelection(3); // Italiano
                break;
            case "fr":
                spinner.setSelection(4); // Francés
                break;
            default:
                spinner.setSelection(0);
                break;
        }

        // Configurar el BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Configura el listener para manejar los clics en el BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Puedes seleccionar un elemento específico al iniciar la actividad
        bottomNavigationView.setSelectedItemId(PERFIL_ID);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Obtener el código del idioma seleccionado
                String codigoIdioma = obtenerCodigoIdiomaDesdePosicion(position);

                // Verificar si el idioma actual es diferente del nuevo idioma seleccionado
                if (!idiomaActual.equals(codigoIdioma)) {
                    // Cambiar el idioma y reiniciar la actividad
                    cambiarIdioma(codigoIdioma);
                    reiniciarActividad();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Manejar el caso en el que no se ha seleccionado nada
            }
        });
    }
    private String obtenerCodigoIdiomaDesdePosicion(int posicion) {
        switch (posicion) {
            case 0:
                return "es"; // Español
            case 1:
                return "en"; // Inglés
            case 2:
                return "de"; // Alemán
            case 3:
                return "it";
            case 4:
                return "fr";
            default:
                return "es"; // Por defecto, español
        }
    }

    // Listener para manejar los clics en el BottomNavigationView
    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                // Determinar qué actividad se debe lanzar según el elemento seleccionado
                int itemId = item.getItemId();
                if (itemId == REPRODUCTOR_ID) {
                    startActivity(new Intent(ConfUsuario.this, Reproductor.class));
                    return true;
                } else if (itemId == LISTA_CANCIONES_ID) {
                    startActivity(new Intent(ConfUsuario.this, ListaCanciones.class));
                    return true;
                } else if (itemId == BUSQUEDA_ID) {
                    startActivity(new Intent(ConfUsuario.this, ListaCanciones.class));
                    return true;
                } else if (itemId == PERFIL_ID) {
                    // Esta actividad ya está en primer plano, no necesitas hacer nada aquí
                    return true;
                }
                return false;
            };
    // Método para cambiar el idioma de la aplicación
    private void cambiarIdioma(String codigoIdioma) {
        Locale nuevaLocale = new Locale(codigoIdioma);
        Locale.setDefault(nuevaLocale);

        Configuration config = new Configuration();
        config.locale = nuevaLocale;

        getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // Actualizar la variable de idioma actual
        idiomaActual = codigoIdioma;
    }
    private String obtenerIdiomaActual() {
        Locale locale = getResources().getConfiguration().locale;
        return locale.getLanguage(); // Esto devuelve el código de idioma (por ejemplo, "es", "en", "de")
    }

    // Método para reiniciar la actividad actual
    private void reiniciarActividad() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}