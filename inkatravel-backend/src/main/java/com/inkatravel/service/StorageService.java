// En: src/main/java/com/inkatravel/service/StorageService.java

package com.inkatravel.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

@Service
public class StorageService {

    // Leeremos la ruta de guardado desde application.properties
    @Value("${upload.path}")
    private String uploadPath;

    private Path rootLocation;

    /**
     * Inicializa la carpeta de uploads al iniciar la aplicación.
     */
    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(uploadPath);
            // Crea el directorio si no existe
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            // Si hay un error, lo lanzamos para detener la app, ya que el almacenamiento es crítico.
            throw new RuntimeException("No se pudo inicializar la ubicación de almacenamiento: " + uploadPath, e);
        }
    }

    /**
     * Guarda el archivo MultipartFile en el sistema de archivos.
     * @param file El archivo recibido desde el frontend.
     * @return El nombre único del archivo guardado.
     */
    public String guardarArchivo(MultipartFile file) {
        if (file.isEmpty()) {
            // Manejo de error si el archivo está vacío
            throw new RuntimeException("Falló al guardar un archivo vacío.");
        }

        try {
            // 1. Generar un nombre de archivo único
            String originalFilename = file.getOriginalFilename();
            // Esto asegura unicidad y evita que dos usuarios con el mismo nombre de archivo colisionen.
            String uniqueFilename = System.currentTimeMillis() + "-" + originalFilename;

            Path destinationFile = this.rootLocation.resolve(uniqueFilename)
                    .normalize().toAbsolutePath();

            // 2. Copiar el stream de entrada al archivo de destino
            try (InputStream inputStream = file.getInputStream()) {
                // Copia el archivo, reemplazando si ya existe un archivo con ese nombre (aunque no debería con el timestamp)
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueFilename; // Devolvemos el nombre para guardarlo en la base de datos

        } catch (IOException e) {
            // Manejo de error durante la operación de guardado
            throw new RuntimeException("Falló al guardar el archivo: " + e.getMessage(), e);
        }
    }
}