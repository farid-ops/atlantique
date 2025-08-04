package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.DailyCashRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyCashRegisterRepository extends JpaRepository<DailyCashRegister, String> {

    Optional<DailyCashRegister> findByCaissierIdAndOperationDate(String caissierId, LocalDate operationDate);

    Optional<DailyCashRegister> findByCaissierIdAndOperationDateAndIsClosed(String caissierId, LocalDate operationDate, boolean isClosed);

    List<DailyCashRegister> findByCaissierIdAndOperationDateBetween(String caissierId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dcr FROM DailyCashRegister dcr WHERE dcr.caissierId IN :caissierIds AND dcr.operationDate BETWEEN :startDate AND :endDate")
    List<DailyCashRegister> findByCaissierIdInAndOperationDateBetween(@Param("caissierIds") List<String> caissierIds, @Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}