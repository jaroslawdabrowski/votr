package dk.nykredit.bank.account.exposure.rs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.AccountServiceExposure;
import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Poll;
import dk.nykredit.bank.account.model.Vote;
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

    public AccountRepresentation(Poll poll, Set<Vote> votes, UriInfo uriInfo) {
        this(poll, uriInfo);
        this.transactions = new ArrayList<>();
        this.transactions.addAll(votes.stream()
            .map(transaction -> new TransactionRepresentation(transaction, uriInfo))
            .collect(Collectors.toList()));
    }

    public AccountRepresentation(Poll poll, UriInfo uriInfo) {
        this.regNo = poll.getGroupId();
        this.accountNo = poll.getAccountNo();
        this.name = poll.getName();
        this.transactionsResource = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .build(poll.getGroupId(), poll.getAccountNo())).build();
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(AccountServiceExposure.class)
            .path(AccountServiceExposure.class, "get")
            .build(poll.getGroupId(), poll.getAccountNo()))
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
