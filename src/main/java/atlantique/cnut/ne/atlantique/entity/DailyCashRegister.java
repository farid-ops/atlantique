package atlantique.cnut.ne.atlantique.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "daily_cash_register")
public class DailyCashRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String caissierId;

    @Column(nullable = false)
    private LocalDate operationDate;

    @Column(nullable = false)
    private double startingBalance;

    @Column(nullable = false)
    private double totalDeposits;

    @Column(nullable = false)
    private double totalWithdrawals;

    @Column(nullable = false)
    private double endingBalance;

    @Column(nullable = false)
    private int numberOfTransactions;

    @Column(nullable = false)
    private boolean isClosed;

    @CreationTimestamp
    private Date creationTimestamp;
    @UpdateTimestamp
    private Date lastUpdateTimestamp;

}