package dk.nykredit.bank.account.exposure.rs;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.model.TransactionRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.TransactionsRepresentation;
import dk.nykredit.bank.account.model.Poll;
import dk.nykredit.bank.account.model.Vote;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.rs.EntityResponseBuilder;
import dk.nykredit.nic.core.logging.LogDuration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * REST exposure of account transactions.
 */
@Stateless
@Path("/accounts/{regNo}-{accountNo}/transactions")
@Api(value = "/accounts/{regNo}-{accountNo}/transactions", authorizations = @Authorization("oauth2"))
public class TransactionServiceExposure {
    private static final String CONCEPT_NAME = "transaction";
    private static final String CONCEPT_VERSION = "1.0.0";

    @EJB
    private AccountArchivist archivist;

    @GET
    @Produces({ "application/hal+json" })
    @LogDuration(limit = 100)
    @ApiOperation(value = "List all transactions on account", nickname = "listTransactions")
    public Response list(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo,
                         @Context UriInfo uriInfo, @Context Request request) {
        Poll poll = archivist.getAccount(regNo, accountNo);
        return new EntityResponseBuilder<>(poll.getVotes(), transactions -> new TransactionsRepresentation(poll, uriInfo))
            .maxAge(10)
            .build(request);
    }

    @GET
    @Path("{id}")
    @Produces({ "application/hal+json" })
    @LogDuration(limit = 100)
    @ApiOperation(value = "Get single transaction on account", nickname = "getTransaction")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "No transaction found.")
    })
    public Response get(@PathParam("regNo") String regNo, @PathParam("accountNo") String accountNo, @PathParam("id") String id,
                        @Context UriInfo uriInfo, @Context Request request) {
        Vote vote = archivist.getTransaction(regNo, accountNo, id);
        return new EntityResponseBuilder<>(vote, t -> new TransactionRepresentation(t, uriInfo))
            .maxAge(24 * 60 * 60)
            .name(CONCEPT_NAME)
            .version(CONCEPT_VERSION)
            .build(request);
    }
}
