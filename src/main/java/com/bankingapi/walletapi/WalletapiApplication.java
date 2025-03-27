package com.bankingapi.walletapi;

import com.bankingapi.walletapi.model.BankAccount;
import com.bankingapi.walletapi.model.Transaction;
import com.bankingapi.walletapi.enums.TransactionType;
import com.bankingapi.walletapi.model.User;
import com.bankingapi.walletapi.repository.BankAccountRepository;
import com.bankingapi.walletapi.repository.TransactionRepository;
import com.bankingapi.walletapi.repository.UserRepository;
import com.bankingapi.walletapi.service.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class WalletapiApplication {
	public static void main(String[] args) {
		SpringApplication.run(WalletapiApplication.class, args);
	}
	@Bean
	CommandLineRunner initData(
			UserRepository userRepository,
			BankAccountRepository accountRepository,
			TransactionRepository transactionRepository,
			BankAccountService bankAccountService
	) {
		return args -> {

			//users
			User ella = new User();
			ella.setName("Ella Johnson");
			ella.setPassword("passw0rd");
			ella.setEmail("ella@example.com");
			ella.setCreatedAt(LocalDateTime.now().minusDays(30));
			ella = userRepository.save(ella);

			User john = new User();
			john.setName("John Parker");
			john.setPassword("Passw0rd!");
			john.setEmail("john@example.com");
			john.setCreatedAt(LocalDateTime.now().minusDays(25));
			john = userRepository.save(john);

			//accounts
			BankAccount account1 = new BankAccount();
			account1.setUser(ella);
			account1.setAccountNumber("123456789");
			account1.setAccountType("CHECKING");
			account1.setBalance(BigDecimal.valueOf(1000));
			account1 = accountRepository.save(account1);

			BankAccount account2 = new BankAccount();
			account2.setUser(ella);
			account2.setAccountNumber("987654321");
			account2.setAccountType("SAVINGS");
			account2.setBalance(BigDecimal.valueOf(500));
			account2 = accountRepository.save(account2);

			BankAccount account3 = new BankAccount();
			account3.setUser(john);
			account3.setAccountNumber("111111111");
			account3.setAccountType("CHECKING");
			account3.setBalance(BigDecimal.valueOf(600));
			account3 = accountRepository.save(account3);

			//transactions
			Transaction t1 = new Transaction();
			t1.setSenderAccount(account2);
			t1.setReceiverAccount(account1);
			t1.setAmount(BigDecimal.valueOf(100));
			t1.setTransactionType(TransactionType.DEBIT);
			t1.setCreatedAt(LocalDateTime.now().minusDays(3));
			t1.setDescription("Transfer to account #" + account1.getAccountNumber());
			transactionRepository.save(t1);

			Transaction t4 = new Transaction();
			t4.setSenderAccount(account2);
			t4.setReceiverAccount(account1);
			t4.setAmount(BigDecimal.valueOf(100));
			t4.setTransactionType(TransactionType.CREDIT);
			t4.setCreatedAt(LocalDateTime.now().minusDays(3));
			t4.setDescription("Transfer from account #" + account2.getAccountNumber());
			transactionRepository.save(t4);

			Transaction t2 = new Transaction();
			t2.setSenderAccount(null);
			t2.setReceiverAccount(account2);
			t2.setAmount(BigDecimal.valueOf(200));
			t2.setCreatedAt(LocalDateTime.now().minusDays(4));
			t2.setTransactionType(TransactionType.CREDIT);
			t2.setDescription("Deposit by Ella");
			transactionRepository.save(t2);

			Transaction t3 = new Transaction();
			t3.setSenderAccount(account3);
			t3.setReceiverAccount(null);
			t3.setAmount(BigDecimal.valueOf(250));
			t3.setCreatedAt(LocalDateTime.now().minusDays(3));
			t3.setTransactionType(TransactionType.DEBIT);
			t3.setDescription("Withdrawal by John");
			transactionRepository.save(t3);

			Transaction t5 = new Transaction();
			t5.setSenderAccount(account3);
			t5.setReceiverAccount(null);
			t5.setAmount(BigDecimal.valueOf(100));
			t5.setCreatedAt(LocalDateTime.now().minusDays(2));
			t5.setTransactionType(TransactionType.DEBIT);
			t5.setDescription("Another Withdrawal by John");
			transactionRepository.save(t5);

			Transaction t6 = new Transaction();
			t6.setSenderAccount(null);
			t6.setReceiverAccount(account3);
			t6.setAmount(BigDecimal.valueOf(700));
			t6.setCreatedAt(LocalDateTime.now().minusDays(1));
			t6.setTransactionType(TransactionType.CREDIT);
			t6.setDescription("Deposit by John");
			transactionRepository.save(t6);
		};
	}
}
