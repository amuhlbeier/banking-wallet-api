package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.TransactionResponse;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

@Service
public class PdfStatementService {

    private final TransactionService transactionService;

    @Autowired
    public PdfStatementService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public byte[] generateStatementPdf(Long accountId, LocalDateTime from, LocalDateTime to) {
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Bank Statement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph metadata = new Paragraph(
                    String.format("Account ID: %d\nDate Range: %s - %s\n\n", accountId,
                            from.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            to.format(DateTimeFormatter.ISO_LOCAL_DATE)),
                    metaFont
            );
            document.add(metadata);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            addTableHeader(table);
            addTransactionRows(table, transactions);

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF statement", e);
        }
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Transaction ID", "Sender ID", "Receiver ID", "Amount", "Date")
                .forEach(column -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(column));
                    table.addCell(header);
                });
    }

    private void addTransactionRows(PdfPTable table, List<TransactionResponse> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TransactionResponse tx : transactions) {
            table.addCell(tx.getTransactionId().toString());
            table.addCell(tx.getSenderId().toString());
            table.addCell(tx.getReceiverId().toString());
            table.addCell(tx.getAmount().toString());
            table.addCell(tx.getCreatedAt().format(formatter));
        }
    }

    public byte[] generateMonthlyStatementPdf(Long accountId, YearMonth month) {
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59);

        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId)
                .stream()
                .filter(tx -> !tx.getCreatedAt().isBefore(startDate) && !tx.getCreatedAt().isAfter(endDate))
                .toList();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("Monthly Bank Statement", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Paragraph info = new Paragraph(String.format(
                    "Account ID: %d\nMonth: %s\n\n", accountId, month.toString()
            ), infoFont);
            document.add(info);

            BigDecimal openingBalance = transactions.isEmpty() ? BigDecimal.ZERO : transactions.get(0).getAmount();
            BigDecimal closingBalance = transactions.isEmpty() ? BigDecimal.ZERO : transactions.get(transactions.size() - 1).getAmount();

            Paragraph summary = new Paragraph(
                    "Opening Balance; " + openingBalance.toString() + "\n" +
                            "Closing Balance; + closingBalance.toString()" + "\n\n",
                    infoFont
            );
            document.add(summary);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            addTableHeader(table);
            addTransactionRows(table, transactions);
            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF statement", e);
        }

    }
}
