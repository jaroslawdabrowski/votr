package dk.nykredit.bank.account.exposure.rs;

import java.util.List;
import java.util.Optional;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.model.AccountRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.AccountUpdateRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.AccountsRepresentation;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.core.logging.LogDuration;
import dk.nykredit.nic.rs.EntityResponseBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 * Exposing account as REST service.
 */
@Stateless
@Path("/accounts")
@PermitAll
@DeclareRoles("advisor")
@Api(value = "/accounts", authorizations = @Authorization("oauth2"))
public class AccountServiceExposure {
    private static final String CONCEPT_NAME = "account";
    private static final String CONCEPT_VERSION = "1.0.0";
    @EJB
    private AccountArchivist archivist;

    @GET
    @Produces({ "application/hal+json" })
    @LogDuration(limit = 100)
    @ApiOperation(value = "List all accounts", nickname = "listAccounts")
    public Response list(@Context UriInfo uriInfo, @Context Request request) {
        List<Account> accounts = archivist.listAccounts();
        return new EntityResponseBuilder<>(accounts, list -> new AccountsRepresentation(list, uriInfo))
            .maxAge(10)
            .build(request);
    }

    @GET
    @Path("{regNo}-{accountNo}")
    @Produces({ "application/hal+json" })
    @LogDuration(limit = 100)
    @ApiOperation(value = "Get single account", nickname = "getAccount")
    @ApiResponses(value = {
        @ApiResponse(code = 404, message = "No account found.")
    })
    public Response get(@PathParam("regNo") @Pattern(regexp = "^[0-9]{4}$") String regNo,
                        @PathParam("accountNo") @Pattern(regexp = "^[0-9]+$") String accountNo,
                        @Context UriInfo uriInfo, @Context Request request) {
        Account account = archivist.getAccount(regNo, accountNo);
        return new EntityResponseBuilder<>(account, acc -> new AccountRepresentation(acc, acc.getTransactions(), uriInfo))
            .name(CONCEPT_NAME)
            .version(CONCEPT_VERSION)
            .maxAge(60)
            .build(request);
    }

    @PUT
    @RolesAllowed("advisor")
    @Path("{regNo}-{accountNo}")
    @Produces({ "application/hal+json" })
    @Consumes(MediaType.APPLICATION_JSON)
    @LogDuration(limit = 100)
    @ApiOperation(value = "Create new or update existing account", nickname = "updateAccount")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "No updating possible")
    })
    public Response createOrUpdate(@PathParam("regNo") @Pattern(regexp = "^[0-9]{4}$") String regNo,
                           @PathParam("accountNo") @Pattern(regexp = "^[0-9]+$") String accountNo,
                           @Valid AccountUpdateRepresentation account,
                           @Context UriInfo uriInfo, @Context Request request) {
        if (!regNo.equals(account.getRegNo()) || !accountNo.equals(account.getAccountNo())) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Optional<Account> acc = archivist.findAccount(regNo, accountNo);
        Account a;
        if (acc.isPresent()) {
            a = acc.get();
            a.setName(account.getName());
        } else {
            a = new Account(regNo, accountNo, account.getName());
        }
        archivist.save(a);
        return new EntityResponseBuilder<>(a, acm -> new AccountRepresentation(acm, uriInfo))
            .name(CONCEPT_NAME)
            .version(CONCEPT_VERSION)
            .maxAge(60)
            .build(request);
    }

}
