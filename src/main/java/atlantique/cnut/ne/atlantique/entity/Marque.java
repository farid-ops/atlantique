package atlantique.cnut.ne.atlantique.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "marque")
public class Marque {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column
    private String designation;
    @CreationTimestamp
    private Date dateCreation;
    @UpdateTimestamp
    private Date dateModification;
    @ManyToOne
    @JoinColumn(name = "gamme_id", nullable = false)
    @JsonBackReference
    private Gamme gamme;
}
