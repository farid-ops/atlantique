package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.DailyCashRegisterDto;
import java.time.LocalDate;
import java.util.List;

public interface DailyCashRegisterService {

    DailyCashRegisterDto getOrCreateDailyCashRegister(String caissierId);

    DailyCashRegisterDto recordDeposit(String caissierId, double amount);

    DailyCashRegisterDto recordWithdrawal(String caissierId, double amount);

    DailyCashRegisterDto getDailySummary(String caissierId, LocalDate date);

    List<DailyCashRegisterDto> getDailySummariesForCaissier(String caissierId, LocalDate startDate, LocalDate endDate);

    DailyCashRegisterDto closeCashRegister(String caissierId);

    DailyCashRegisterDto openCashRegister(String caissierId);

    List<DailyCashRegisterDto> getDailySummariesForSite(String siteId, LocalDate startDate, LocalDate endDate);

    boolean isCashRegisterOpen(String caissierId, LocalDate date);

    void openCashRegister(String userId, LocalDate date);

    void closeCashRegister(String userId, LocalDate date);

    List<DailyCashRegisterDto> getDailySummariesForGroup(String groupId, LocalDate startDate, LocalDate endDate);
}