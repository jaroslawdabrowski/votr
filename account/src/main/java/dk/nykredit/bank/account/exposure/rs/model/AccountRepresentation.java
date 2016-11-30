package dk.nykredit.bank.account.exposure.rs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.AccountServiceExposure;
import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

/**
 * Represents a single as returned from REST service.
 */
@Resource
public class AccountRepresentation {
    private String regNo;
    private String accountNo;
    private String name;

    @EmbeddedResource("transactions")
    private Collection<TransactionRepresentation> transactions;

    @Link("account:transactions")
    private HALLink transactionsResource;

    @Link
    private HALLink self;

    public AccountRepresentation(Account account, Set<Transaction> transactions, UriInfo uriInfo) {
        this(account, uriInfo);
        this.transactions = new ArrayList<>();
        this.transactions.addAll(transactions.stream()
            .map(transaction -> new TransactionRepresentation(transaction, uriInfo))
            .collect(Collectors.toList()));
    }

    public AccountRepresentation(Account account, UriInfo uriInfo) {
        this.regNo = account.getRegNo();
        this.accountNo = account.getAccountNo();
        this.name = account.getName();
        this.transactionsResource = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .build(account.getRegNo(), account.getAccountNo())).build();
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(AccountServiceExposure.class)
            .path(AccountServiceExposure.class, "get")
            .build(account.getRegNo(), account.getAccountNo()))
            .build();
    }

    public String getRegNo() {
        return regNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getName() {
        return name;
    }

    public Collection<TransactionRepresentation> getTransactions() {
        if (transactions == null) {
            return null;
        } else {
            return Collections.unmodifiableCollection(transactions);
        }
    }

    public HALLink getTransactionsResource() {
        return transactionsResource;
    }

    public HALLink getSelf() {
        return self;
    }
}
