package atlantique.cnut.ne.atlantique.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Transactional
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private final Path rootLocation;

    public FileStorageServiceImpl(Path rootLocation) {
        this.rootLocation = rootLocation;
        logger.info("FileStorageServiceImpl initialized. Root directory: {}", rootLocation.toAbsolutePath());
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

            Path destinationFile = this.rootLocation.resolve(uniqueFilename).normalize().toAbsolutePath();

            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
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
            Path file = this.rootLocation.resolve(filename).normalize();
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
        return this.rootLocation.resolve(filename).normalize();
    }

    @Override
    public void delete(String filename) {
        Path file = this.rootLocation.resolve(filename).normalize();
        FileSystemUtils.deleteRecursively(file.toFile());
        logger.info("Fichier supprimé : {}", filename);
    }

    @Override
    public void deleteByPath(String filePath) {
        try {
            Path file = this.rootLocation.resolve(filePath).normalize();

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