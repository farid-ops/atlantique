package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class MarchandiseItem {
    private String poids;
    private String nombreColis;
    private String numeroBl;
}