package dk.nykredit.bank.account.exposure.rs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Poll;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

/**
 * Represents a set of transactions as returned by the REST service.
 */
@Resource
public class TransactionsRepresentation {
    @EmbeddedResource("transactions")
    private Collection<TransactionRepresentation> transactions;

    @Link
    private HALLink self;

    public TransactionsRepresentation(Poll poll, UriInfo uriInfo) {
        transactions = new ArrayList<>();
        transactions.addAll(poll.getVotes().stream()
            .map(transaction -> new TransactionRepresentation(transaction, uriInfo))
            .collect(Collectors.toList()));
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .build(poll.getGroupId(), poll.getAccountNo()))
            .build();
    }

    public Collection<TransactionRepresentation> getTransactions() {
        return Collections.unmodifiableCollection(transactions);
    }

    public HALLink getSelf() {
        return self;
    }
}
