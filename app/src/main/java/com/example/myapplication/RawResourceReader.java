package com.example.myapplication;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para leer recursos raw.
 */
public class RawResourceReader {

    /**
     * Obtiene una lista de nombres de recursos raw disponibles en la aplicación.
     *
     * @param context Contexto de la aplicación.
     * @return Lista de nombres de recursos raw.
     */
    public static List<String> getRawResourceList(Context context) {
        List<String> rawResourceList = new ArrayList<>();

        // Obtener todos los campos de la clase R.raw
        Field[] fields = R.raw.class.getFields();

        // Iterar a través de los campos y obtener información sobre los recursos raw
        for (Field field : fields) {
            try {
                // Obtener el nombre y el ID del recurso raw
                String resourceName = field.getName();
                int resourceId = field.getInt(null);

                // Agregar el nombre del recurso a la lista
                rawResourceList.add(resourceName);
            } catch (IllegalAccessException e) {
                // Manejar errores de acceso ilegal a campos
                Log.e("RawResourceReader", "Error al leer recursos raw", e);
            }
        }

        return rawResourceList;
    }
}