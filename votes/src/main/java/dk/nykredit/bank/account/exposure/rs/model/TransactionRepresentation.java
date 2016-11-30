package dk.nykredit.bank.account.exposure.rs.model;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.bank.account.model.Vote;
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

    public TransactionRepresentation(Vote vote, UriInfo uriInfo) {
        this.id = vote.getId();
        this.description = vote.getDescription();
        this.amount = vote.getAmount().toPlainString();
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(TransactionServiceExposure.class)
            .path(TransactionServiceExposure.class, "get")
            .build(vote.getPoll().getGroupId(), vote.getPoll().getAccountNo(), vote.getId()))
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
