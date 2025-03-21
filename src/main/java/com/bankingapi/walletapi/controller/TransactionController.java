package com.bankingapi.walletapi.controller;

import com.bankingapi.walletapi.dto.TransferRequest;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.model.User;
import com.bankingapi.walletapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import com.bankingapi.walletapi.dto.TransactionResponse;
import com.bankingapi.walletapi.dto.TransferRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @Operation(
            summary = "Get all transactions",
            description = "Returns all transactions"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue ="0") int page,
            @RequestParam(defaultValue ="10") int size
            ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> paginatedTransactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(paginatedTransactions);

    }


    @Operation(
            summary = "Get transaction by its ID",
            description = "Returns a transaction by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transaction successfully returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Transaction.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Transaction not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }


    @Operation(
            summary = "Transfer funds between accounts",
            description = "Creates a new transaction by transferring funds"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Funds successfully transferred",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid transfer request"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferFunds(@RequestBody @Valid TransferRequest request) {
        TransactionResponse result = transactionService.transferFunds(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @Operation(
            summary = "Get transactions for a bank account",
            description = "Returns a list of all transactions associated with a specific account"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(@PathVariable Long accountId) {
        List<TransactionResponse> history = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(history);
    }


    @Operation(
            summary = "Filter transactions by date range",
            description = "Returns a list of all transactions within a specific date range"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid date range"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/filter/date")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByDateRange(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        List<TransactionResponse> filtered = transactionService.getTransactionsByDateRange(fromDate, toDate);
        return ResponseEntity.ok(filtered);
    }


    @Operation(
            summary = "Filter transactions by amount range",
            description = "Returns a list of all transactions within a specific amount range"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid amount range"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @GetMapping("/filter/amount)")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAmountRange(
            @RequestParam("minAmount") BigDecimal minAmount,
            @RequestParam("maxAmount") BigDecimal maxAmount
    ) {
        List<TransactionResponse> result = transactionService.getTransactionsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/csv")
    public void exportTransactionsToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");
        transactionService.exportTransactionsToCsv(response.getWriter());
    }

}
