package atlantique.cnut.ne.atlantique.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
            logger.info("Répertoire de stockage initialisé : {}", rootLocation.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Impossible d'initialiser le répertoire de stockage des fichiers!", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new IOException("Impossible de stocker le fichier en dehors du répertoire désigné.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.info("Fichier sauvegardé : {} sous le nom unique : {}", originalFilename, uniqueFilename);
            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Échec du stockage du fichier.", e);
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Impossible de lire le fichier: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }

    @Override
    public Path loadPath(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            FileSystemUtils.deleteRecursively(file);
            logger.info("Fichier supprimé : {}", filename);
        } catch (IOException e) {
            logger.error("Échec de la suppression du fichier {}: {}", filename, e.getMessage(), e);
        }
    }

    @Override
    public void deleteByPath(String filePath) {
        try {
            Path file = Paths.get(filePath);
            if (Files.exists(file) && Files.isRegularFile(file)) {
                Files.delete(file);
                logger.info("Fichier supprimé physiquement : {}", filePath);
            } else {
                logger.warn("Tentative de suppression d'un fichier inexistant ou non régulier : {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Échec de la suppression du fichier par chemin {}: {}", filePath, e.getMessage(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Échec du chargement de tous les fichiers!", e);
        }
    }
}