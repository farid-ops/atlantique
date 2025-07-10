package atlantique.cnut.ne.atlantique.exceptions;

import lombok.Getter;

@Getter
public enum StatusCode {
//    Entité : Armateur
    HTTP_ARMATEUR_CREATED("201", "Armateur créé avec succès."),
    HTTP_ARMATEUR_RETRIEVED("200", "Armateur récupéré avec succès."),
    HTTP_ARMATEUR_UPDATED("200", "Armateur mis à jour avec succès."),
    HTTP_ARMATEUR_DELETED("204", "Armateur supprimé avec succès."),
    HTTP_ARMATEUR_NOT_FOUND("404", "Armateur non trouvé."),
    HTTP_ARMATEUR_BAD_REQUEST("400", "Requête Armateur invalide."),

//    Entité : Autorite
    HTTP_AUTORITE_CREATED("201", "Autorité créée avec succès."),
    HTTP_AUTORITE_RETRIEVED("200", "Autorité récupérée avec succès."),
    HTTP_AUTORITE_UPDATED("200", "Autorité mise à jour avec succès."),
    HTTP_AUTORITE_DELETED("204", "Autorité supprimée avec succès."),
    HTTP_AUTORITE_NOT_FOUND("404", "Autorité non trouvée."),
    HTTP_AUTORITE_BAD_REQUEST("400", "Requête Autorité invalide."),

//    Entité : BL
    HTTP_BL_CREATED("201", "BL créé avec succès."),
    HTTP_BL_RETRIEVED("200", "BL récupéré avec succès."),
    HTTP_BL_UPDATED("200", "BL mis à jour avec succès."),
    HTTP_BL_DELETED("204", "BL supprimé avec succès."),
    HTTP_BL_NOT_FOUND("404", "BL non trouvé."),
    HTTP_BL_BAD_REQUEST("400", "Requête BL invalide."),

//    Entité : BonExpedition
    HTTP_BONEXPEDITION_CREATED("201", "Bon d'Expédition créé avec succès."),
    HTTP_BONEXPEDITION_RETRIEVED("200", "Bon d'Expédition récupéré avec succès."),
    HTTP_BONEXPEDITION_UPDATED("200", "Bon d'Expédition mis à jour avec succès."),
    HTTP_BONEXPEDITION_DELETED("204", "Bon d'Expédition supprimé avec succès."),
    HTTP_BONEXPEDITION_NOT_FOUND("404", "Bon d'Expédition non trouvé."),
    HTTP_BONEXPEDITION_BAD_REQUEST("400", "Requête Bon d'Expédition invalide."),

//    Entité : Cargaison
    HTTP_CARGAISON_CREATED("201", "Cargaison créée avec succès."),
    HTTP_CARGAISON_RETRIEVED("200", "Cargaison récupérée avec succès."),
    HTTP_CARGAISON_UPDATED("200", "Cargaison mise à jour avec succès."),
    HTTP_CARGAISON_DELETED("204", "Cargaison supprimée avec succès."),
    HTTP_CARGAISON_NOT_FOUND("404", "Cargaison non trouvée."),
    HTTP_CARGAISON_BAD_REQUEST("400", "Requête Cargaison invalide."),

//    Entité : Consignataire
    HTTP_CONSIGNATAIRE_CREATED("201", "Consignataire créé avec succès."),
    HTTP_CONSIGNATAIRE_RETRIEVED("200", "Consignataire récupéré avec succès."),
    HTTP_CONSIGNATAIRE_UPDATED("200", "Consignataire mis à jour avec succès."),
    HTTP_CONSIGNATAIRE_DELETED("204", "Consignataire supprimé avec succès."),
    HTTP_CONSIGNATAIRE_NOT_FOUND("404", "Consignataire non trouvé."),
    HTTP_CONSIGNATAIRE_BAD_REQUEST("400", "Requête Consignataire invalide."),

//    Entité : ExtraBE
    HTTP_EXTRABE_CREATED("201", "ExtraBE créé avec succès."),
    HTTP_EXTRABE_RETRIEVED("200", "ExtraBE récupéré avec succès."),
    HTTP_EXTRABE_UPDATED("200", "ExtraBE mis à jour avec succès."),
    HTTP_EXTRABE_DELETED("204", "ExtraBE supprimé avec succès."),
    HTTP_EXTRABE_NOT_FOUND("404", "ExtraBE non trouvé."),
    HTTP_EXTRABE_BAD_REQUEST("400", "Requête ExtraBE invalide."),

//    Entité : Gamme
    HTTP_GAMME_CREATED("201", "Gamme créée avec succès."),
    HTTP_GAMME_RETRIEVED("200", "Gamme récupérée avec succès."),
    HTTP_GAMME_UPDATED("200", "Gamme mise à jour avec succès."),
    HTTP_GAMME_DELETED("204", "Gamme supprimée avec succès."),
    HTTP_GAMME_NOT_FOUND("404", "Gamme non trouvée."),
    HTTP_GAMME_BAD_REQUEST("400", "Requête Gamme invalide."),

//    Entité : Importateur
    HTTP_IMPORTATEUR_CREATED("201", "Importateur créé avec succès."),
    HTTP_IMPORTATEUR_RETRIEVED("200", "Importateur récupéré avec succès."),
    HTTP_IMPORTATEUR_UPDATED("200", "Importateur mis à jour avec succès."),
    HTTP_IMPORTATEUR_DELETED("204", "Importateur supprimé avec succès."),
    HTTP_IMPORTATEUR_NOT_FOUND("404", "Importateur non trouvé."),
    HTTP_IMPORTATEUR_BAD_REQUEST("400", "Requête Importateur invalide."),

//    Entité : Marchandise
    HTTP_MARCHANDISE_CREATED("201", "Marchandise créée avec succès."),
    HTTP_MARCHANDISE_RETRIEVED("200", "Marchandise récupérée avec succès."),
    HTTP_MARCHANDISE_UPDATED("200", "Marchandise mise à jour avec succès."),
    HTTP_MARCHANDISE_DELETED("204", "Marchandise supprimée avec succès."),
    HTTP_MARCHANDISE_NOT_FOUND("404", "Marchandise non trouvée."),
    HTTP_MARCHANDISE_BAD_REQUEST("400", "Requête Marchandise invalide."),

//    Entité : NatureMarchandise
    HTTP_NATUREMARCHANDISE_CREATED("201", "Nature de Marchandise créée avec succès."),
    HTTP_NATUREMARCHANDISE_RETRIEVED("200", "Nature de Marchandise récupérée avec succès."),
    HTTP_NATUREMARCHANDISE_UPDATED("200", "Nature de Marchandise mise à jour avec succès."),
    HTTP_NATUREMARCHANDISE_DELETED("204", "Nature de Marchandise supprimée avec succès."),
    HTTP_NATUREMARCHANDISE_NOT_FOUND("404", "Nature de Marchandise non trouvée."),
    HTTP_NATUREMARCHANDISE_BAD_REQUEST("400", "Requête Nature de Marchandise invalide."),

//    Entité : Navire
    HTTP_NAVIRE_CREATED("201", "Navire créé avec succès."),
    HTTP_NAVIRE_RETRIEVED("200", "Navire récupéré avec succès."),
    HTTP_NAVIRE_UPDATED("200", "Navire mis à jour avec succès."),
    HTTP_NAVIRE_DELETED("204", "Navire supprimé avec succès."),
    HTTP_NAVIRE_NOT_FOUND("404", "Navire non trouvé."),
    HTTP_NAVIRE_BAD_REQUEST("400", "Requête Navire invalide."),

//    Entité : Pays
    HTTP_PAYS_CREATED("201", "Pays créé avec succès."),
    HTTP_PAYS_RETRIEVED("200", "Pays récupéré avec succès."),
    HTTP_PAYS_UPDATED("200", "Pays mis à jour avec succès."),
    HTTP_PAYS_DELETED("204", "Pays supprimé avec succès."),
    HTTP_PAYS_NOT_FOUND("404", "Pays non trouvé."),
    HTTP_PAYS_BAD_REQUEST("400", "Requête Pays invalide."),

//    Entité : Port
    HTTP_PORT_CREATED("201", "Port créé avec succès."),
    HTTP_PORT_RETRIEVED("200", "Port récupéré avec succès."),
    HTTP_PORT_UPDATED("200", "Port mis à jour avec succès."),
    HTTP_PORT_DELETED("204", "Port supprimé avec succès."),
    HTTP_PORT_NOT_FOUND("404", "Port non trouvé."),
    HTTP_PORT_BAD_REQUEST("400", "Requête Port invalide."),

//    Entité : Site
    HTTP_SITE_CREATED("201", "Site créé avec succès."),
    HTTP_SITE_RETRIEVED("200", "Site récupéré avec succès."),
    HTTP_SITE_UPDATED("200", "Site mis à jour avec succès."),
    HTTP_SITE_DELETED("204", "Site supprimé avec succès."),
    HTTP_SITE_NOT_FOUND("404", "Site non trouvé."),
    HTTP_SITE_BAD_REQUEST("400", "Requête Site invalide."),

//    Entité : Transitaire
    HTTP_TRANSITAIRE_CREATED("201", "Transitaire créé avec succès."),
    HTTP_TRANSITAIRE_RETRIEVED("200", "Transitaire récupéré avec succès."),
    HTTP_TRANSITAIRE_UPDATED("200", "Transitaire mis à jour avec succès."),
    HTTP_TRANSITAIRE_DELETED("204", "Transitaire supprimé avec succès."),
    HTTP_TRANSITAIRE_NOT_FOUND("404", "Transitaire non trouvé."),
    HTTP_TRANSITAIRE_BAD_REQUEST("400", "Requête Transitaire invalide."),

//    Entité : Utilisateur
    HTTP_UTILISATEUR_CREATED("201", "Utilisateur créé avec succès."),
    HTTP_UTILISATEUR_RETRIEVED("200", "Utilisateur récupéré avec succès."),
    HTTP_UTILISATEUR_UPDATED("200", "Utilisateur mis à jour avec succès."),
    HTTP_UTILISATEUR_DELETED("204", "Utilisateur supprimé avec succès."),
    HTTP_UTILISATEUR_NOT_FOUND("404", "Utilisateur non trouvé."),
    HTTP_UTILISATEUR_BAD_REQUEST("400", "Requête Utilisateur invalide.");

    private final String status_code;
    private final String status_message;

    StatusCode(final String status_code, final String status_message){
        this.status_code = status_code;
        this.status_message = status_message;
    }
}
