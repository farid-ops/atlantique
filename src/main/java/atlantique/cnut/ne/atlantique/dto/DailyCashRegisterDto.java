package atlantique.cnut.ne.atlantique.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DailyCashRegisterDto {
    private String id;
    private String caissierId;
    private Instant operationDate;
    private double startingBalance;
    private double totalDeposits;
    private double totalWithdrawals;
    private double endingBalance;
    private int numberOfTransactions;
    private boolean isClosed;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private String caissierName;
}