package com.segundito.app.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileUploadUtil {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String saveFile(String subdirectory, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subdirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Sanitizar nombre de archivo
        String extension = FilenameUtils.getExtension(fileName);
        String cleanFileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(cleanFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // Devolver la ruta relativa para almacenar en la base de datos
            return subdirectory + "/" + cleanFileName;
        } catch (IOException e) {
            throw new IOException("No se pudo guardar el archivo: " + fileName, e);
        }
    }

    public void deleteFile(String relativePath) {
        try {
            Path filePath = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(filePath);

            // Intentar eliminar el directorio si está vacío
            Path directory = filePath.getParent();
            if (Files.exists(directory) && Files.isDirectory(directory)) {
                if (Files.list(directory).count() == 0) {
                    Files.delete(directory);
                }
            }
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Error al eliminar archivo: " + e.getMessage());
        }
    }
}