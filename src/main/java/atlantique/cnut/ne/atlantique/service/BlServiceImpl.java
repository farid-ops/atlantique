package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.BlDto;
import atlantique.cnut.ne.atlantique.entity.BL;
import atlantique.cnut.ne.atlantique.entity.Cargaison;
import atlantique.cnut.ne.atlantique.entity.Port;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.BlRepository;
import atlantique.cnut.ne.atlantique.repository.CargaisonRepository;
import atlantique.cnut.ne.atlantique.repository.PortRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlServiceImpl implements BlService {

    private static final Logger logger = LoggerFactory.getLogger(BlServiceImpl.class);

    private final BlRepository blRepository;
    private final CargaisonRepository cargaisonRepository;
    private final PortRepository portRepository;
    private final PaysService paysService;

    private static final String BL_STORAGE_DIRECTORY = "./uploads/bl_documents/";
    private static final long MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10 Mo
    private static final String PDF_MIME_TYPE = "application/pdf";

    public BlServiceImpl(BlRepository blRepository, CargaisonRepository cargaisonRepository,
                         PortRepository portRepository, PaysService paysService) {
        this.blRepository = blRepository;
        this.cargaisonRepository = cargaisonRepository;
        this.portRepository = portRepository;
        this.paysService = paysService;

        try {
            Files.createDirectories(Paths.get(BL_STORAGE_DIRECTORY));
            logger.info("Répertoire de stockage des BLs vérifié/créé: {}", BL_STORAGE_DIRECTORY);
        } catch (IOException e) {
            logger.error("Impossible de créer le répertoire de stockage des BLs: {}", BL_STORAGE_DIRECTORY, e);
        }
    }

    @Override
    public BL createBl(BlDto blDto) {
        BL bl = new BL();
        bl.setDesignation(blDto.getDesignation());
        bl.setFilename(blDto.getFilename());
        bl.setMimeType(blDto.getMimetype());
        bl.setPath(blDto.getPath());
        return blRepository.save(bl);
    }

    @Override
    public List<BL> findAllBls() {
        return blRepository.findAll();
    }

    @Override
    public Page<BL> findAllBlsPaginated(Pageable pageable) {
        return blRepository.findAll(pageable);
    }

    @Override
    public Optional<BL> findBlById(String id) {
        return blRepository.findById(id);
    }

    @Override
    public BL updateBl(String id, BlDto blDto) {
        return blRepository.findById(id)
                .map(existingBl -> {
                    existingBl.setDesignation(blDto.getDesignation());
                    existingBl.setFilename(blDto.getFilename());
                    existingBl.setMimeType(blDto.getMimetype());
                    existingBl.setPath(blDto.getPath());
                    return blRepository.save(existingBl);
                }).orElseThrow(() -> new ResourceNotFoundException("BL non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteBl(String id) {
        BL blToDelete = blRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BL non trouvé avec l'ID: " + id));

        if (blToDelete.getPath() != null && !blToDelete.getPath().isEmpty()) {
            Path filePath = Paths.get(blToDelete.getPath());
            try {
                Files.deleteIfExists(filePath);
                logger.info("Fichier BL supprimé physiquement: {}", filePath);
            } catch (IOException e) {
                logger.error("Impossible de supprimer le fichier BL {}: {}", filePath, e.getMessage(), e);
            }
        }
        blRepository.deleteById(id);
    }

    @Override
    public List<BL> findBlsByPaysId(String idPays) {
        if (paysService.findPaysById(idPays).isEmpty()) {
            throw new ResourceNotFoundException("Pays non trouvé avec l'ID: " + idPays);
        }

        List<Port> ports = portRepository.findByIdPays(idPays);
        if (ports.isEmpty()) {
            return List.of();
        }

        Set<String> portIds = ports.stream()
                .map(Port::getId)
                .collect(Collectors.toSet());

        Set<String> blIdsFromCargaisons = new HashSet<>();
        for (String portId : portIds) {
            List<Cargaison> cargaisons = cargaisonRepository.findByIdPortEmbarquementOrIdPortDemarquement(portId, portId);
            cargaisons.forEach(cargaison -> {
                if (cargaison.getIdBl() != null && !cargaison.getIdBl().isEmpty()) {
                    blIdsFromCargaisons.add(cargaison.getIdBl());
                }
            });
        }

        if (blIdsFromCargaisons.isEmpty()) {
            return List.of();
        }

        return blRepository.findAllById(blIdsFromCargaisons);
    }

    @Override
    public BL uploadBlDocument(MultipartFile file, String designation) {
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            logger.warn("Taille du fichier BL dépasse la limite ({} bytes): {}", file.getSize(), file.getOriginalFilename());
            throw new IllegalArgumentException("La taille du fichier ne doit pas dépasser 10 Mo.");
        }

        if (!PDF_MIME_TYPE.equals(file.getContentType())) {
            logger.warn("Type de fichier BL invalide. Attendu: {}, Reçu: {}", PDF_MIME_TYPE, file.getContentType());
            throw new IllegalArgumentException("Seuls les fichiers PDF sont autorisés.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = Paths.get(BL_STORAGE_DIRECTORY + uniqueFilename);

            Files.copy(file.getInputStream(), filePath);
            logger.info("Fichier BL enregistré: {} à {}", originalFilename, filePath.toAbsolutePath());

            BL newBl = new BL();
            newBl.setDesignation(designation);
            newBl.setFilename(originalFilename);
            newBl.setMimeType(file.getContentType());
            newBl.setPath(filePath.toAbsolutePath().toString());

            return blRepository.save(newBl);

        } catch (IOException e) {
            logger.error("Échec du stockage du fichier BL {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Échec du stockage du fichier.", e);
        }
    }
}