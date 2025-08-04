package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.DailyCashRegisterDto;
import atlantique.cnut.ne.atlantique.entity.Groupe;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    byte[] generatePdfReport(List<DailyCashRegisterDto> summaries, Groupe groupe, LocalDate startDate, LocalDate endDate) throws IOException;

    byte[] generateCsvReport(List<DailyCashRegisterDto> summaries) throws IOException;
}
