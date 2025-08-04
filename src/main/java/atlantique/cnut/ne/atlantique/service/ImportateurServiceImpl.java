package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.ImportateurDto;
import atlantique.cnut.ne.atlantique.entity.Importateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.ImportateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ImportateurServiceImpl implements ImportateurService {

    private final ImportateurRepository importateurRepository;
    private final AuthService authService;

    public ImportateurServiceImpl(ImportateurRepository importateurRepository, AuthService authService) {
        this.importateurRepository = importateurRepository;
        this.authService = authService;
    }

    @Override
    public Importateur createImportateur(ImportateurDto importateurDto) {
         if (importateurRepository.findByNif(importateurDto.getNif()).isPresent()) {
             throw new IllegalArgumentException("Un importateur avec ce NIF existe déjà.");
         }

        Importateur importateur = new Importateur();
        importateur.setNom(importateurDto.getNom());
        importateur.setPrenom(importateurDto.getPrenom());
        importateur.setPhone(importateurDto.getPhone());
        importateur.setNif(importateurDto.getNif());
        importateur.setIdGroupe(authService.getLoggedInUserGroupId());

        return importateurRepository.save(importateur);
    }

    @Override
    public List<Importateur> findAllImportateurs() {
        return importateurRepository.findAll();
    }

    @Override
    public List<Importateur> findByIdGroupe(String idGroupe) {
        return this.importateurRepository.findByIdGroupe(idGroupe);
    }

    @Override
    public Page<Importateur> findAllImportateursPaginated(Pageable pageable, String idGroupe) {
        if (idGroupe != null && !idGroupe.isEmpty()) {
            return importateurRepository.findByIdGroupe(idGroupe, pageable);
        }
        return importateurRepository.findAll(pageable);
    }

    @Override
    public Optional<Importateur> findImportateurById(String id) {
        return importateurRepository.findById(id);
    }

    @Override
    public Importateur updateImportateur(String id, ImportateurDto importateurDto) {
        return importateurRepository.findById(id)
                .map(existingImportateur -> {
                    existingImportateur.setNom(importateurDto.getNom());
                    existingImportateur.setPrenom(importateurDto.getPrenom());
                    existingImportateur.setPhone(importateurDto.getPhone());
                    existingImportateur.setNif(importateurDto.getNif());

                    return importateurRepository.save(existingImportateur);
                }).orElseThrow(() -> new ResourceNotFoundException("Importateur non trouvé avec l'ID: " + id));
    }

    @Override
    public void deleteImportateur(String id) {
        if (!importateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Importateur non trouvé avec l'ID: " + id);
        }
        importateurRepository.deleteById(id);
    }
}