package com.bankingapi.walletapi.service;

import com.bankingapi.walletapi.dto.TransactionResponse;
import com.bankingapi.walletapi.enums.TransactionType;
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
import java.util.stream.Stream;

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

            generatePdfTable(document, transactions);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF statement", e);
        }
    }

    public byte[] generateMonthlyStatementPdf(Long accountId, YearMonth month) {
        LocalDateTime startDate = month.atDay(1).atStartOfDay();
        LocalDateTime endDate = month.atEndOfMonth().atTime(23, 59, 59);

        List<TransactionResponse> allTransactions = transactionService.getTransactionsByAccountId(accountId);

        BigDecimal openingBalance = BigDecimal.ZERO;
        BigDecimal closingBalance = BigDecimal.ZERO;

        boolean seenFirst = false;

        for (TransactionResponse tx : allTransactions.stream().sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt())).toList()) {
            if (tx.getCreatedAt().isBefore(startDate)) {
                openingBalance = applyTransaction(openingBalance, tx, accountId);
            } else if (!tx.getCreatedAt().isAfter(endDate)) {
                if (!seenFirst) {
                    closingBalance = openingBalance;
                    seenFirst = true;
                }
                closingBalance = applyTransaction(closingBalance, tx, accountId);
            }
        }

        List<TransactionResponse> monthlyTransactions = allTransactions.stream()
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


            Paragraph summary = new Paragraph(
                    String.format("Opening Balance: %s\nClosing Balance: %s\n\n",
                            openingBalance.toString(),
                            closingBalance.toString()),
                    infoFont
            );
            document.add(summary);

            generatePdfTable(document, monthlyTransactions);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF statement", e);
        }
    }

    private BigDecimal applyTransaction(BigDecimal currentBalance, TransactionResponse tx, Long accountId) {
        if (tx.getTransactionType() == null || tx.getAmount() == null) return currentBalance;

        boolean isThisAccountSender = tx.getSenderId() != null && tx.getSenderId().equals(accountId);
        boolean isThisAccountReceiver = tx.getReceiverId() != null && tx.getReceiverId().equals(accountId);

        if (tx.getTransactionType() == TransactionType.DEBIT && isThisAccountSender) {
            return currentBalance.subtract(tx.getAmount());
        } else if (tx.getTransactionType() == TransactionType.CREDIT && isThisAccountReceiver) {
            return currentBalance.add(tx.getAmount());
        }
        return currentBalance;
    }


    private void generatePdfTable(Document document, List<TransactionResponse> transactions) throws DocumentException {
               PdfPTable table = new PdfPTable(6);
               table.setWidthPercentage(100);
               table.setSpacingBefore(10);
               addTableHeader(table);
               addTransactionRows(table, transactions);
               document.add(table);
           }

    private void addTableHeader(PdfPTable table) {
        Stream.of("Transaction ID", "Sender ID", "Receiver ID", "Amount", "Date", "Transaction Type")
                .forEach(column -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(column));
                    table.addCell(header);
                });
    }

    private void addTransactionRows(PdfPTable table, List<TransactionResponse> transactions) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TransactionResponse tx : transactions) {
            table.addCell(tx.getTransactionId() != null ? tx.getTransactionId().toString() : "—");
            table.addCell(tx.getSenderId() != null ? tx.getSenderId().toString() : "—");
            table.addCell(tx.getReceiverId() != null ? tx.getReceiverId().toString() : "—");
            table.addCell(tx.getAmount() != null ? tx.getAmount().toString() : "—");
            table.addCell(tx.getCreatedAt() != null ? tx.getCreatedAt().format(formatter) : "—");
            table.addCell(tx.getTransactionType() != null ? tx.getTransactionType().name() : "—");
        }
    }
}
