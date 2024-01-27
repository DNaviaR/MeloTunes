package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Actividad principal que maneja el inicio de sesión en la aplicación.
 * Esta actividad permite a los usuarios ingresar con su correo electrónico y contraseña.
 * Además, proporciona la funcionalidad para redirigir a los usuarios a la actividad de registro
 * en caso de que no tengan una cuenta.
 *
 * La clase utiliza elementos de la interfaz de usuario como EditText y TextView, y
 * también interactúa con Firebase Authentication para manejar el inicio de sesión.
 *
 */
public class Login extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private FirebaseAuth mAuth;
    private TextView registro;
    private Button buttonLogin;

    /**
     * Método que se llama cuando se crea la actividad.
     * Aquí se inicializan los elementos de la interfaz, se configuran los botones y se
     * asignan escuchas (listeners) a los eventos de clic.
     *
     * @param savedInstanceState La instancia previamente guardada del estado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Llama al método onCreate de la clase padre (AppCompatActivity).
        super.onCreate(savedInstanceState);

        // Establece el diseño de la actividad a partir del archivo XML activity_main.
        setContentView(R.layout.activity_main);

        // Inicializar elementos de la interfaz
        editTextUsername = findViewById(R.id.introducircorreoelectronico);
        editTextPassword = findViewById(R.id.introducircontraseña);
        mAuth = FirebaseAuth.getInstance();
        registro = findViewById(R.id.botonRegistro);

        // Configurar el botón de inicio de sesión
        buttonLogin = findViewById(R.id.botoniniciarsesion);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre de usuario y la contraseña ingresados
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Iniciar sesión utilizando Firebase Authentication
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Inicio de sesión exitoso, redirigir a la siguiente actividad
                                    Intent intent = new Intent(Login.this, Reproductor.class);
                                    startActivity(intent);
                                } else {
                                    // Si falla el inicio de sesión, muestra un mensaje de error
                                    String miString = getResources().getString(R.string.loginIncorrecto);
                                    Toast.makeText(Login.this, miString, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Configurar el TextView para el botón de registro
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una nueva intención para iniciar la actividad de registro
                Intent intent = new Intent(Login.this, Registro.class);
                // Iniciar la actividad de registro
                startActivity(intent);
            }
        });
    }
}
