package atlantique.cnut.ne.atlantique.enums;

import lombok.Getter;

@Getter
public enum MarchandiseStatus {
    BROUILLON("Brouillon"),
    SOUMIS_POUR_VALIDATION("Soumis pour validation"),
    VALIDE("Validé"),
    REJETE("Rejeté");

    private final String displayName;

    MarchandiseStatus(String displayName) {
        this.displayName = displayName;
    }
}
