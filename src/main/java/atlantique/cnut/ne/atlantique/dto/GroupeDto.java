package atlantique.cnut.ne.atlantique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupeDto {
    private String id;

    @NotBlank(message = "La dénomination du groupe est obligatoire.")
    private String denomination;

    @NotBlank(message = "Le téléphone du groupe est obligatoire.")
    private String telephone;

    @NotBlank(message = "L'email du groupe est obligatoire.")
    @Email(message = "Le format de l'email est invalide.")
    private String email;

    private String siteWeb;

    private String nif;

    private String bp;

    @NotBlank(message = "L'adresse du groupe est obligatoire.")
    private String adresse;

    @NotNull(message = "Le prix BE standard est obligatoire.")
    private Double prixBeStandard;

    @NotNull(message = "Le visa pour véhicule de moins de 5000kg est obligatoire.")
    private Double visaVehiculeMoins5000kg;

    @NotNull(message = "Le visa pour véhicule de 5000kg et plus est obligatoire.")
    private Double visaVehiculePlus5000kg;

    private String signatureImage;
    private String logo;

    private Double coutBSC;
    private Double tonnage;
    private Double valeurConteneur10Pieds;
    private Double valeurConteneur20Pieds;
    private Double valeurConteneur30Pieds;
}