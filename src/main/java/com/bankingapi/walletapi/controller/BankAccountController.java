package com.bankingapi.walletapi.controller;
import com.bankingapi.walletapi.dto.BankAccountRequest;
import com.bankingapi.walletapi.dto.BankAccountResponse;
import com.bankingapi.walletapi.dto.DepositWithdrawRequest;
import com.bankingapi.walletapi.model.User;
import com.bankingapi.walletapi.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.repository.BankAccountRepository;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @Operation(
            summary = "Get all bank accounts",
            description = "Returns a list of all bank accounts in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful retrieval of bank accounts",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllAccounts());
    }


    @Operation(
            summary = "Get a bank account by ID",
            description = "Returns a specific bank account by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bank account successfully retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bank account not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id));
    }


    @Operation(
            summary = "Create a new bank account",
            description = "Creates a new bank account using the request data"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bank account successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(
            @Valid @RequestBody BankAccountRequest request) {
       BankAccountResponse createdAccount = bankAccountService.createAccount(request);
       return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }


    @Operation(
            summary = "Delete a bank account",
            description = "Deletes a bank account by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Bank account successfully deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bank account not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Deposit funds into a bank account",
            description = "Deposits funds into a bank account by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Funds successfully deposited.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid deposit request"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bank account was not found."),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @PostMapping("/{id}/deposit")
    public ResponseEntity<BankAccountResponse> deposit(
            @PathVariable Long id,
            @RequestBody @Valid DepositWithdrawRequest request
    ) {
        return ResponseEntity.ok(bankAccountService.depositFunds(id, request));
    }


    @Operation(
            summary = "Withdraw funds from a bank account",
            description = "Withdraws funds from a bank account by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Funds successfully withdrawn",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BankAccountResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid withdrawal request"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Bank account not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<BankAccountResponse> withdraw(
            @PathVariable Long id,
            @RequestBody @Valid DepositWithdrawRequest request
    ) {
        return ResponseEntity.ok(bankAccountService.withdrawFunds(id, request));
    }

    @PutMapping("/{id}/freeze")
    public ResponseEntity<Void> freezeAccount(@PathVariable Long id) {
        bankAccountService.freezeAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/unfreeze")
    public ResponseEntity<Void> unfreezeAccount(@PathVariable Long id) {
        bankAccountService.unfreezeAccount(id);
        return ResponseEntity.noContent().build();
    }

}
