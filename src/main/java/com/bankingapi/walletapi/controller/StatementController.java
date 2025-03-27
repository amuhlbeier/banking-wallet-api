package com.bankingapi.walletapi.controller;

import com.bankingapi.walletapi.service.PdfStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final PdfStatementService pdfStatementService;

    @Autowired
    public StatementController(PdfStatementService pdfStatementService) {
        this.pdfStatementService = pdfStatementService;
    }

    @GetMapping("/pdf")
    @Operation(
            summary = "Download PDF statement of all transactions",
            description = "Generates and downloads a PDF statement containing all transactions"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF successfully generated and downloaded"
            ),
            @ApiResponse(
                    responseCode ="500",
                    description = "Internal server error"
            )
    })
    public void downloadPDFStatement(
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            HttpServletResponse response
    ) {
        try {
            byte[] pdfData = pdfStatementService.generateStatementPdf(accountId, from, to);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=statement.pdf");
            response.getOutputStream().write(pdfData);
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException("Error while generating PDF", e);
        }

    }

    @GetMapping("/monthly-pdf")
    @Operation(
            summary = "Download monthly PDF statement",
            description = "Generates a monthly statement as a PDF file"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF successfully generated"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    public void downloadMonthlyStatement(
            @RequestParam Long accountId,
            @RequestParam int year,
            @RequestParam int month,
            HttpServletResponse response
    ) {
        try {
            YearMonth yearMonth = YearMonth.of(year, month);
            byte[] pdf = pdfStatementService.generateMonthlyStatementPdf(accountId, yearMonth);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=statement.pdf");
            response.getOutputStream().write(pdf);
            response.flushBuffer();

        } catch (IOException e) {
            throw new RuntimeException("Error while generating monthly PDF", e);
        }
    }

}
