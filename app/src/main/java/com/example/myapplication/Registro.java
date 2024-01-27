package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase que representa la actividad de registro de usuarios.
 */
public class Registro extends AppCompatActivity {

    // Elementos de la interfaz
    private TextView correo;
    private TextView name;
    private TextView contrasena;
    private Spinner genero;
    private Button botonRegistrar;

    // Firebase Authentication
    private FirebaseAuth mAuth;

    // Firebase Firestore
    private FirebaseFirestore db;

    /**
     * Método llamado cuando se crea la actividad.
     * Inicializa los elementos de la interfaz y configura el botón de registro.
     *
     * @param savedInstanceState Objeto que proporciona datos sobre el estado anterior de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de elementos de la interfaz
        correo = findViewById(R.id.introducircorreoelectronico);
        name = findViewById(R.id.introducirnombre);
        contrasena = findViewById(R.id.introducircontraseña);
        botonRegistrar = findViewById(R.id.confirmarRegistro);
        genero = findViewById(R.id.seleccionargenero);

        // Inicialización de Firebase Authentication y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configuración del Spinner (Selector de género)
        List<String> elementos = Arrays.asList("Hombre", "Mujer");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, elementos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genero.setAdapter(adapter);

        // Configuración del botón de registro
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    /**
     * Método privado para realizar el registro de un nuevo usuario.
     * Obtiene la información de los elementos de la interfaz, crea un nuevo usuario en Firebase Authentication
     * y almacena información adicional en Firebase Firestore.
     */
    private void registrarUsuario() {
        // Obtener los valores de los campos de entrada
        String nombre = name.getText().toString();
        String email = correo.getText().toString();
        String contrasenha = contrasena.getText().toString();
        String gender = genero.getSelectedItem().toString();

        // Verificar si los campos obligatorios están llenos
        if (email.isEmpty() || contrasenha.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(Registro.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo usuario con correo y contraseña en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, contrasenha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso
                            Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            // Obtener el ID del usuario recién registrado
                            String userId = mAuth.getCurrentUser().getUid();

                            // Almacenar información adicional en Firestore
                            DocumentReference usuarioRef = db.collection("usuarios").document(userId);

                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("nombre", nombre);
                            usuario.put("email", email);
                            usuario.put("genero", gender);

                            usuarioRef.set(usuario)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Registro en Firestore exitoso
                                            Intent intent = new Intent(Registro.this, Reproductor.class);
                                            startActivity(intent);
                                            finish(); // Cerrar la actividad de registro
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error al almacenar información en Firestore
                                            Toast.makeText(Registro.this, "Error al almacenar información en Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Si falla el registro, muestra un mensaje de error
                            Toast.makeText(Registro.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

