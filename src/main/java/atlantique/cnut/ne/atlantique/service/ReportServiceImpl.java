package atlantique.cnut.ne.atlantique.service;

import atlantique.cnut.ne.atlantique.dto.DailyCashRegisterDto;
import atlantique.cnut.ne.atlantique.entity.Groupe;
import atlantique.cnut.ne.atlantique.entity.Utilisateur;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Paragraph;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZoneId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final UtilisateurService utilisateurService;

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private static final String IMAGE_STORAGE_DIRECTORY = "./uploads/groupe/";

    public ReportServiceImpl(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public byte[] generatePdfReport(List<DailyCashRegisterDto> summaries, Groupe groupe, LocalDate startDate, LocalDate endDate) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Ajout du logo et du nom du groupe
            if (groupe != null) {
                if (groupe.getLogo() != null) {
                    try {
                        Path logoPath = Paths.get(IMAGE_STORAGE_DIRECTORY + groupe.getLogo());
                        if (Files.exists(logoPath)) {
                            Image logo = Image.getInstance(logoPath.toString());
                            logo.scaleAbsolute(100f, 100f);
                            logo.setAlignment(Element.ALIGN_CENTER);
                            document.add(logo);
                        }
                    } catch (Exception e) {
                        logger.error("Impossible d'ajouter le logo au PDF.", e);
                    }
                }
                Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.NORMAL);
                Paragraph title = new Paragraph("Rapport de Recettes - " + groupe.getDenomination(), fontTitle);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
            }

            // Ajout des titres du rapport
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String reportDates = "Période du " + startDate.format(formatter) + " au " + endDate.format(formatter);
            Paragraph reportTitle = new Paragraph(reportDates, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            reportTitle.setAlignment(Element.ALIGN_CENTER);
            reportTitle.setSpacingBefore(10);
            reportTitle.setSpacingAfter(20);
            document.add(reportTitle);

            // Création du tableau de données
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 2f, 2f, 2f, 2f, 2f});
            table.setSpacingBefore(10);

            // En-têtes du tableau
            table.addCell("Date d'Opération");
            table.addCell("Caissier");
            table.addCell("Solde Initial");
            table.addCell("Total Dépôts");
            table.addCell("Total Retraits");
            table.addCell("Solde Final");


            for (DailyCashRegisterDto summary : summaries) {
                Utilisateur utilisateur = utilisateurService.findUtilisateurById(summary.getCaissierId()).get();
                LocalDate operationDate = LocalDate.ofInstant(summary.getOperationDate(), ZoneId.systemDefault());
                table.addCell(operationDate.format(formatter));
                table.addCell(utilisateur.getNom().concat("-").concat(utilisateur.getPrenom()));
                table.addCell(String.format("%,.2f", summary.getStartingBalance()));
                table.addCell(String.format("%,.2f", summary.getTotalDeposits()));
                table.addCell(String.format("%,.2f", summary.getTotalWithdrawals()));
                table.addCell(String.format("%,.2f", summary.getEndingBalance()));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generateCsvReport(List<DailyCashRegisterDto> summaries) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("Date d'Opération", "Caissier", "Solde Initial", "Total Dépôts", "Total Retraits", "Solde Final"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            for (DailyCashRegisterDto summary : summaries) {
                Utilisateur utilisateur = utilisateurService.findUtilisateurById(summary.getCaissierId()).get();
                LocalDate operationDate = LocalDate.ofInstant(summary.getOperationDate(), ZoneId.systemDefault());
                csvPrinter.printRecord(
                        operationDate.format(formatter),
                        utilisateur.getNom().concat("-").concat(utilisateur.getPrenom()),
                        summary.getStartingBalance(),
                        summary.getTotalDeposits(),
                        summary.getTotalWithdrawals(),
                        summary.getEndingBalance()
                );
            }

            csvPrinter.flush();
            return out.toByteArray();
        }
    }
}
