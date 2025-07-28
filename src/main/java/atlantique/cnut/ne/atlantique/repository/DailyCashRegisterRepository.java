package atlantique.cnut.ne.atlantique.repository;

import atlantique.cnut.ne.atlantique.entity.DailyCashRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyCashRegisterRepository extends JpaRepository<DailyCashRegister, String> {

    Optional<DailyCashRegister> findByCaissierIdAndOperationDate(String caissierId, LocalDate operationDate);

    Optional<DailyCashRegister> findByCaissierIdAndOperationDateAndIsClosed(String caissierId, LocalDate operationDate, boolean isClosed);
}