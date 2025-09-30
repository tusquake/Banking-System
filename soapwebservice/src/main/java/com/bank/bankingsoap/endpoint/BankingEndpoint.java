package com.bank.bankingsoap.endpoint;

import com.bank.bankingsoap.entity.Account;
import com.bank.bankingsoap.entity.Transaction;
import com.bank.bankingsoap.generated.*;
import com.bank.bankingsoap.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Endpoint
public class BankingEndpoint {

    private static final String NAMESPACE_URI = "http://bank.com/banking";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private BankingService bankingService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createAccountRequest")
    @ResponsePayload
    public CreateAccountResponse createAccount(@RequestPayload CreateAccountRequest request) {
        try {
            Account account = bankingService.createAccount(
                    request.getAccountHolderName(),
                    request.getAccountType().value(),
                    request.getInitialBalance()
            );

            CreateAccountResponse response = new CreateAccountResponse();
            response.setAccount(mapToAccountType(account));
            response.setMessage("Account created successfully");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error creating account: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAccountRequest")
    @ResponsePayload
    public GetAccountResponse getAccount(@RequestPayload GetAccountRequest request) {
        try {
            Account account = bankingService.getAccount(request.getAccountNumber());

            GetAccountResponse response = new GetAccountResponse();
            response.setAccount(mapToAccountType(account));
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving account: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "depositRequest")
    @ResponsePayload
    public DepositResponse deposit(@RequestPayload DepositRequest request) {
        try {
            Transaction transaction = bankingService.deposit(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            DepositResponse response = new DepositResponse();
            response.setTransaction(mapToTransactionType(transaction));
            response.setNewBalance(transaction.getBalanceAfter());
            response.setMessage("Deposit successful");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing deposit: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "withdrawRequest")
    @ResponsePayload
    public WithdrawResponse withdraw(@RequestPayload WithdrawRequest request) {
        try {
            Transaction transaction = bankingService.withdraw(
                    request.getAccountNumber(),
                    request.getAmount()
            );

            WithdrawResponse response = new WithdrawResponse();
            response.setTransaction(mapToTransactionType(transaction));
            response.setNewBalance(transaction.getBalanceAfter());
            response.setMessage("Withdrawal successful");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing withdrawal: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "transferRequest")
    @ResponsePayload
    public TransferResponse transfer(@RequestPayload TransferRequest request) {
        try {
            Transaction[] transactions = bankingService.transfer(
                    request.getFromAccountNumber(),
                    request.getToAccountNumber(),
                    request.getAmount()
            );

            TransferResponse response = new TransferResponse();
            response.setFromTransaction(mapToTransactionType(transactions[0]));
            response.setToTransaction(mapToTransactionType(transactions[1]));
            response.setMessage("Transfer successful");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error processing transfer: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTransactionHistoryRequest")
    @ResponsePayload
    public GetTransactionHistoryResponse getTransactionHistory(@RequestPayload GetTransactionHistoryRequest request) {
        try {
            List<Transaction> transactions = bankingService.getTransactionHistory(request.getAccountNumber());

            GetTransactionHistoryResponse response = new GetTransactionHistoryResponse();
            for (Transaction transaction : transactions) {
                response.getTransactions().add(mapToTransactionType(transaction));
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving transaction history: " + e.getMessage());
        }
    }

    private com.bank.bankingsoap.generated.Account mapToAccountType(Account account) {
        com.bank.bankingsoap.generated.Account accountType = new com.bank.bankingsoap.generated.Account();
        accountType.setId(account.getId());
        accountType.setAccountNumber(account.getAccountNumber());
        accountType.setAccountHolderName(account.getAccountHolderName());
        accountType.setAccountType(AccountType.fromValue(account.getAccountType()));
        accountType.setBalance(account.getBalance());
        accountType.setStatus(AccountStatus.fromValue(account.getStatus()));
        accountType.setCreatedDate(account.getCreatedDate().format(FORMATTER));
        return accountType;
    }

    private com.bank.bankingsoap.generated.Transaction mapToTransactionType(Transaction transaction) {
        com.bank.bankingsoap.generated.Transaction transactionType = new com.bank.bankingsoap.generated.Transaction();
        transactionType.setId(transaction.getId());
        transactionType.setTransactionId(transaction.getTransactionId());
        transactionType.setAccountNumber(transaction.getAccountNumber());
        transactionType.setTransactionType(TransactionType.fromValue(transaction.getTransactionType()));
        transactionType.setAmount(transaction.getAmount());
        transactionType.setBalanceAfter(transaction.getBalanceAfter());
        transactionType.setTimestamp(transaction.getTimestamp().format(FORMATTER));
        transactionType.setDescription(transaction.getDescription());
        return transactionType;
    }
}