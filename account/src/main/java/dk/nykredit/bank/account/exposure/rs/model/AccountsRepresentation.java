package dk.nykredit.bank.account.exposure.rs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.AccountServiceExposure;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.jackson.dataformat.hal.HALLink;
import dk.nykredit.jackson.dataformat.hal.annotation.EmbeddedResource;
import dk.nykredit.jackson.dataformat.hal.annotation.Link;
import dk.nykredit.jackson.dataformat.hal.annotation.Resource;

/**
 * Represents a set of accounts from the REST service exposure.
 */
@Resource
public class AccountsRepresentation {

    @Link
    private HALLink self;

    @EmbeddedResource("accounts")
    private Collection<AccountRepresentation> accounts;

    public AccountsRepresentation(List<Account> accounts, UriInfo uriInfo) {
        this.accounts = new ArrayList<>();
        this.accounts.addAll(accounts.stream().map(account -> new AccountRepresentation(account, uriInfo)).collect(Collectors.toList()));
        this.self = new HALLink.Builder(uriInfo.getBaseUriBuilder()
            .path(AccountServiceExposure.class)
            .build())
            .build();
    }

    public HALLink getSelf() {
        return self;
    }

    public Collection<AccountRepresentation> getAccounts() {
        return Collections.unmodifiableCollection(accounts);
    }
}
