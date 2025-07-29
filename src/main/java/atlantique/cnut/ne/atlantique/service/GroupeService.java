package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.GroupeDto;
import atlantique.cnut.ne.atlantique.entity.Groupe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface GroupeService {

    Groupe createGroupe(GroupeDto groupeDto, MultipartFile logoFile, MultipartFile signatureFile);

    Optional<Groupe> findGroupeById(String id);

    List<Groupe> findAllGroupes();

    Page<Groupe> findAllGroupesPaginated(Pageable pageable);

    Groupe updateGroupe(String id, GroupeDto groupeDto, MultipartFile logoFile, MultipartFile signatureFile);

    void deleteGroupe(String id);

    Optional<Groupe> findByDenomination(String denomination);
}