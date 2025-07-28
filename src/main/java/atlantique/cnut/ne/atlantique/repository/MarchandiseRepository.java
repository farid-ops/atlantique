package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.Marchandise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MarchandiseRepository extends JpaRepository<Marchandise, String> {

    Page<Marchandise> findByIdUtilisateur(String idUtilisateur, Pageable pageable);

    List<Marchandise> findByIdUtilisateur(String idUtilisateur);

    @Query(value = """
        SELECT m.* FROM marchandises m
        WHERE m.status = 'SOUMIS_POUR_VALIDATION'
        UNION ALL
        SELECT m.* FROM marchandises m
        WHERE m.validated_by_user_id = :caissierId AND (m.status = 'VALIDE' OR m.status = 'REJETE')
        -- ORDER BY et LIMIT/OFFSET seront ajoutés automatiquement par Spring Data JPA via Pageable
        """,
            countQuery = """
        SELECT count(*) FROM (
            SELECT m.id FROM marchandises m WHERE m.status = 'SOUMIS_POUR_VALIDATION'
            UNION ALL
            SELECT m.id FROM marchandises m WHERE m.validated_by_user_id = :caissierId AND (m.status = 'VALIDE' OR m.status = 'REJETE')
        ) as combined_marchandises
        """,
            nativeQuery = true)
    Page<Marchandise> findForCaissierCombined(
            @org.springframework.data.repository.query.Param("caissierId") String caissierId,
            Pageable pageable // Ajout du paramètre Pageable
    );

    // Version non paginée de la requête combinée pour le caissier (inchangée)
    @Query(value = """
        SELECT m.* FROM marchandises m
        WHERE m.status = 'SOUMIS_POUR_VALIDATION'
        UNION ALL
        SELECT m.* FROM marchandises m
        WHERE m.validated_by_user_id = :caissierId AND (m.status = 'VALIDE' OR m.status = 'REJETE')
        ORDER BY m.creation_date DESC
        """, nativeQuery = true)
    List<Marchandise> findForCaissierCombinedList(
            @org.springframework.data.repository.query.Param("caissierId") String caissierId
    );


    Page<Marchandise> findByLieuEmissionCargaison(String lieuEmissionCargaison, Pageable pageable);
    List<Marchandise> findByLieuEmissionCargaison(String lieuEmissionCargaison);

    // Si un caissier doit voir les marchandises que des opérateurs lui ont soumises spécifiquement
    // et que cet ID de caissier est stocké dans la marchandise, il faudrait :
    // Page<Marchandise> findByAssignedToCaissierId(String assignedToCaissierId, Pageable pageable);
    // Comme ce champ n'existe pas, nous allons nous baser sur l'interprétation "toutes les marchandises à valider".
}
