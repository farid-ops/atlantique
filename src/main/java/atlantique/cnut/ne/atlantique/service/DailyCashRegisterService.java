package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.DailyCashRegisterDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DailyCashRegisterService {

    DailyCashRegisterDto getOrCreateDailyCashRegister(String caissierId);

    DailyCashRegisterDto recordDeposit(String caissierId, double amount);

    DailyCashRegisterDto recordWithdrawal(String caissierId, double amount);

    DailyCashRegisterDto getDailySummary(String caissierId, LocalDate date);

    DailyCashRegisterDto closeCashRegister(String caissierId);

    DailyCashRegisterDto openCashRegister(String caissierId);

    List<DailyCashRegisterDto> getDailySummariesForSite(String siteId, LocalDate date);

    boolean isCashRegisterOpen(String caissierId, LocalDate date);
}