package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "navire")
public class Navire {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String designation;
}
