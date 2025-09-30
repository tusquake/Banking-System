package com.bank.bankingsoap.service;

import com.bank.bankingsoap.entity.Account;
import com.bank.bankingsoap.entity.Transaction;
import com.bank.bankingsoap.repository.AccountRepository;
import com.bank.bankingsoap.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BankingService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Account createAccount(String accountHolderName, String accountType, BigDecimal initialBalance) {
        String accountNumber = generateAccountNumber();

        Account account = new Account(
                accountNumber,
                accountHolderName,
                accountType,
                initialBalance,
                "ACTIVE",
                LocalDateTime.now()
        );

        return accountRepository.save(account);
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = getAccount(accountNumber);
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                generateTransactionId(),
                accountNumber,
                "DEPOSIT",
                amount,
                newBalance,
                LocalDateTime.now(),
                "Deposit to account"
        );

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = getAccount(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                generateTransactionId(),
                accountNumber,
                "WITHDRAWAL",
                amount,
                newBalance,
                LocalDateTime.now(),
                "Withdrawal from account"
        );

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction[] transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }

        Account fromAccount = getAccount(fromAccountNumber);
        Account toAccount = getAccount(toAccountNumber);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Deduct from sender
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
        fromAccount.setBalance(fromNewBalance);
        accountRepository.save(fromAccount);

        Transaction fromTransaction = new Transaction(
                generateTransactionId(),
                fromAccountNumber,
                "TRANSFER_OUT",
                amount,
                fromNewBalance,
                LocalDateTime.now(),
                "Transfer to " + toAccountNumber
        );
        transactionRepository.save(fromTransaction);

        // Add to receiver
        BigDecimal toNewBalance = toAccount.getBalance().add(amount);
        toAccount.setBalance(toNewBalance);
        accountRepository.save(toAccount);

        Transaction toTransaction = new Transaction(
                generateTransactionId(),
                toAccountNumber,
                "TRANSFER_IN",
                amount,
                toNewBalance,
                LocalDateTime.now(),
                "Transfer from " + fromAccountNumber
        );
        transactionRepository.save(toTransaction);

        return new Transaction[]{fromTransaction, toTransaction};
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        // Verify account exists
        getAccount(accountNumber);
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}