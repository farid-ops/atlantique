package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.UtilisateurDto;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;

import java.util.List; // Importez List
import java.util.Optional;

public interface UtilisateurService {

    Optional<Utilisateur> findByPhone(String phone);

    /**
     * Crée un nouvel utilisateur à partir des données du DTO.
     * Le mot de passe sera encodé et des rôles par défaut seront attribués.
     * @param utilisateurDto Le DTO contenant les informations du nouvel utilisateur.
     * @return L'entité Utilisateur créée et sauvegardée.
     */
    Utilisateur createUtilisateur(UtilisateurDto utilisateurDto);

    /**
     * Récupère tous les utilisateurs.
     * @return Une liste de tous les utilisateurs.
     */
    List<Utilisateur> findAllUtilisateurs();

    /**
     * Récupère un utilisateur par son ID.
     * @param id L'ID de l'utilisateur.
     * @return Un Optional contenant l'utilisateur s'il est trouvé.
     */
    Optional<Utilisateur> findUtilisateurById(String id);

    /**
     * Met à jour un utilisateur existant.
     * @param id L'ID de l'utilisateur à mettre à jour.
     * @param utilisateurDto Le DTO contenant les données de mise à jour.
     * @return L'entité Utilisateur mise à jour.
     */
    Utilisateur updateUtilisateur(String id, UtilisateurDto utilisateurDto);

    /**
     * Supprime un utilisateur par son ID.
     * @param id L'ID de l'utilisateur à supprimer.
     */
    void deleteUtilisateur(String id);
}