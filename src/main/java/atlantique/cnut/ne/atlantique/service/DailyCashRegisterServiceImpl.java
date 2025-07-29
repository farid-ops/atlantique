package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.DailyCashRegisterDto;
import atlantique.cnut.ne.atlantique.entity.DailyCashRegister;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import atlantique.cnut.ne.atlantique.exceptions.ResourceNotFoundException;
import atlantique.cnut.ne.atlantique.repository.DailyCashRegisterRepository;
import atlantique.cnut.ne.atlantique.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyCashRegisterServiceImpl implements DailyCashRegisterService {

    private final DailyCashRegisterRepository dailyCashRegisterRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public DailyCashRegisterDto getOrCreateDailyCashRegister(String caissierId) {
        LocalDate today = LocalDate.now();
        Optional<DailyCashRegister> existingRegister = dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, today);

        if (existingRegister.isPresent()) {
            return convertToDto(existingRegister.get());
        } else {
            DailyCashRegister newRegister = new DailyCashRegister();
            newRegister.setCaissierId(caissierId);
            newRegister.setOperationDate(today);
            newRegister.setTotalDeposits(0.0);
            newRegister.setTotalWithdrawals(0.0);
            newRegister.setNumberOfTransactions(0);
            newRegister.setClosed(false);

            LocalDate yesterday = today.minusDays(1);
            Optional<DailyCashRegister> yesterdayRegister = dailyCashRegisterRepository.findByCaissierIdAndOperationDateAndIsClosed(caissierId, yesterday, true);

            if (yesterdayRegister.isPresent()) {
                newRegister.setStartingBalance(yesterdayRegister.get().getEndingBalance());
            } else {
                newRegister.setStartingBalance(0.0);
            }
            newRegister.setEndingBalance(newRegister.getStartingBalance());

            log.info("Creating new daily cash register for cashier {} for date {}. Starting balance: {}", caissierId, today, newRegister.getStartingBalance());
            return convertToDto(dailyCashRegisterRepository.save(newRegister));
        }
    }

    @Override
    @Transactional
    public DailyCashRegisterDto recordDeposit(String caissierId, double amount) {
        LocalDate today = LocalDate.now();
        DailyCashRegister register = dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, today)
                .orElseThrow(() -> new ResourceNotFoundException("Caisse journalière non trouvée pour le caissier " + caissierId + " à la date " + today));

        if (register.isClosed()) {
            throw new IllegalArgumentException("La caisse est clôturée pour aujourd'hui. Impossible d'enregistrer de nouveaux dépôts.");
        }

        register.setTotalDeposits(register.getTotalDeposits() + amount);
        register.setEndingBalance(register.getEndingBalance() + amount);
        register.setNumberOfTransactions(register.getNumberOfTransactions() + 1);

        log.info("Recording deposit of {} for cashier {}. New total deposits: {}. New ending balance: {}.", amount, caissierId, register.getTotalDeposits(), register.getEndingBalance());
        return convertToDto(dailyCashRegisterRepository.save(register));
    }

    @Override
    public DailyCashRegisterDto recordWithdrawal(String caissierId, double amount) {
        LocalDate today = LocalDate.now();
        DailyCashRegister register = dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, today)
                .orElseThrow(() -> new ResourceNotFoundException("Caisse journalière non trouvée pour le caissier " + caissierId + " à la date " + today));

        if (register.isClosed()) {
            throw new IllegalArgumentException("La caisse est clôturée pour aujourd'hui. Impossible d'enregistrer de nouveaux retraits.");
        }

        if (register.getEndingBalance() < amount) {
            throw new IllegalArgumentException("Solde insuffisant pour effectuer ce retrait. Solde actuel: " + register.getEndingBalance());
        }

        register.setTotalWithdrawals(register.getTotalWithdrawals() + amount);
        register.setEndingBalance(register.getEndingBalance() - amount);
        register.setNumberOfTransactions(register.getNumberOfTransactions() + 1);

        log.info("Recording withdrawal of {} for cashier {}. New total withdrawals: {}. New ending balance: {}.", amount, caissierId, register.getTotalWithdrawals(), register.getEndingBalance());
        return convertToDto(dailyCashRegisterRepository.save(register));    }

    @Override
    @Transactional
    public DailyCashRegisterDto getDailySummary(String caissierId, LocalDate date) {
        DailyCashRegister register = dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, date)
                .orElseThrow(() -> new ResourceNotFoundException("Résumé de caisse non trouvé pour le caissier " + caissierId + " à la date " + date));
        return convertToDto(register);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyCashRegisterDto> getDailySummariesForCaissier(String caissierId, LocalDate startDate, LocalDate endDate) {
        List<DailyCashRegister> summaries = dailyCashRegisterRepository.findByCaissierIdAndOperationDateBetween(caissierId, startDate, endDate);
        return summaries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DailyCashRegisterDto closeCashRegister(String caissierId) {
        LocalDate today = LocalDate.now();
        DailyCashRegister register = dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, today)
                .orElseThrow(() -> new ResourceNotFoundException("Caisse journalière non trouvée pour le caissier " + caissierId + " à la date " + today));

        if (register.isClosed()) {
            throw new IllegalArgumentException("La caisse est déjà clôturée pour aujourd'hui.");
        }

        register.setClosed(true);
        log.info("Closing daily cash register for cashier {} for date {}. Final balance: {}. Total transactions: {}.", caissierId, today, register.getEndingBalance(), register.getNumberOfTransactions());
        return convertToDto(dailyCashRegisterRepository.save(register));
    }

    @Override
    @Transactional
    public DailyCashRegisterDto openCashRegister(String caissierId) {
        LocalDate today = LocalDate.now();
        Optional<DailyCashRegister> existingRegister = dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, today);

        if (existingRegister.isPresent() && !existingRegister.get().isClosed()) {
            throw new IllegalArgumentException("La caisse est déjà ouverte pour aujourd'hui.");
        }
        return getOrCreateDailyCashRegister(caissierId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyCashRegisterDto> getDailySummariesForSite(String siteId, LocalDate startDate, LocalDate endDate) {
        List<Utilisateur> cashiers = utilisateurRepository.findAll().stream()
                .filter(user -> user.getIdSite() != null && user.getIdSite().equals(siteId) &&
                        user.getAuthorites().stream().anyMatch(auth -> "SCOPE_CAISSIER".equals(auth.getNom())))
                .toList();

        if (cashiers.isEmpty()) {
            throw new ResourceNotFoundException("Aucun caissier trouvé pour le site avec l'ID: " + siteId);
        }

        List<DailyCashRegisterDto> allSummaries = new ArrayList<>();
        for (Utilisateur cashier : cashiers) {
            List<DailyCashRegister> cashierSummaries = dailyCashRegisterRepository.findByCaissierIdAndOperationDateBetween(cashier.getId(), startDate, endDate);
            allSummaries.addAll(cashierSummaries.stream()
                    .map(this::convertToDto)
                    .toList());
        }

        return allSummaries;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCashRegisterOpen(String caissierId, LocalDate date) {
        return dailyCashRegisterRepository.findByCaissierIdAndOperationDate(caissierId, date)
                .map(register -> !register.isClosed())
                .orElse(true);
    }


    private DailyCashRegisterDto convertToDto(DailyCashRegister entity) {
        return new DailyCashRegisterDto(
                entity.getId(),
                entity.getCaissierId(),
                entity.getOperationDate(),
                entity.getStartingBalance(),
                entity.getTotalDeposits(),
                entity.getTotalWithdrawals(),
                entity.getEndingBalance(),
                entity.getNumberOfTransactions(),
                entity.isClosed(),
                entity.getCreationTimestamp(),
                entity.getLastUpdateTimestamp()
        );
    }
}