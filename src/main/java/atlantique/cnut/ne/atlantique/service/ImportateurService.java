package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.ImportateurDto;
import atlantique.cnut.ne.atlantique.entity.Importateur;

import java.util.List;
import java.util.Optional;

public interface ImportateurService {


    Importateur createImportateur(ImportateurDto importateurDto);

    List<Importateur> findAllImportateurs();

    Optional<Importateur> findImportateurById(String id);

    Importateur updateImportateur(String id, ImportateurDto importateurDto);

    void deleteImportateur(String id);
}