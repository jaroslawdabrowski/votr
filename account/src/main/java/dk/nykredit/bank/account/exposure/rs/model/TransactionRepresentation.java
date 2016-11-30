package dk.nykredit.bank.account.exposure.rs.model;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

/**
 * Represents a single transaction as returned by the REST service.
 */
@Resource
public class TransactionRepresentation {
    private String id;
    private String description;
    private String amount;

    @Link
    private HALLink self;

    public TransactionRepresentation(Transaction transaction, UriInfo uriInfo) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount().toPlainString();
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .path(TransactionServiceExposure.class, "get")
            .build(transaction.getAccount().getRegNo(), transaction.getAccount().getAccountNo(), transaction.getId()))
            .build();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }

    public HALLink getSelf() {
        return self;
    }
}
