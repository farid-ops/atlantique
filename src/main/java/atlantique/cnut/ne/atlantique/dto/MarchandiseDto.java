package atlantique.cnut.ne.atlantique.dto;

import atlantique.cnut.ne.atlantique.enums.MarchandiseStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarchandiseDto {
    private String id;

    private String typeMarchandiseSelect;
    private String caf;
    private String poids;
    private String type;
    private String nombreColis;
    private String numeroChassis;
    private String numeroDouane;
    private String nombreConteneur;

    private boolean regularisation;
    private boolean exoneration;

    private String conteneur;
    private String typeConteneur;
    private String volume;
    private String observation;
    private String numVoyage;

    private String totalQuittance;
    private String be;
    private String visa;
    private String coutBsc;
    private String totalBePrice;

    private MarchandiseStatus status;
    private String submittedByUserId; // Géré par le backend
    private String validatedByUserId; // Géré par le backend
    private Date submissionDate; // Géré par le backend
    private Date validationDate; // Géré par le backend

    private String idNatureMarchandise;
    private String idArmateur;
    private String idTransitaire;
    private String idImportateur;
    private String idUtilisateur;

    private String idBl;
    private String manifesteCargaison;
    private String idConsignataireCargaison;
    private String transporteurCargaison;
    private String idNavireCargaison;

    @Column
    private String blFile;
    @Column
    private String declarationDouaneFile;
    @Column
    private String factureCommercialeFile;

    private Date dateDepartureNavireCargaison;
    private Date dateArriveNavireCargaison;

    private String idPortEmbarquementCargaison;

    private List<MarchandiseItemDto> marchandisesGroupage;

    private Date creationDate;
    private Date modificationDate;
}