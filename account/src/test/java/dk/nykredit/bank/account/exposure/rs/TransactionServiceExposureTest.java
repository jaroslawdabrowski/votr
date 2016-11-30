package dk.nykredit.bank.account.exposure.rs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dk.nykredit.bank.account.exposure.rs.model.TransactionRepresentation;
import dk.nykredit.bank.account.exposure.rs.model.TransactionsRepresentation;
import dk.nykredit.bank.account.model.Account;
import dk.nykredit.bank.account.model.Transaction;
import dk.nykredit.bank.account.persistence.AccountArchivist;
import dk.nykredit.nic.test.rs.UriBuilderFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceExposureTest {

    @Mock
    AccountArchivist archivist;

    @InjectMocks
    TransactionServiceExposure service;

    @Test
    public void testList() {
        UriInfo ui = mock(UriInfo.class);
        when(ui.getBaseUriBuilder()).then(new UriBuilderFactory(URI.create("http://mock")));

        Request request = mock(Request.class);

        Account account = mock(Account.class);
        when(account.getRegNo()).thenReturn("5479");
        when(account.getAccountNo()).thenReturn("123456");
        when(account.getTransactions())
            .thenReturn(new HashSet<>(Collections.singletonList(new Transaction(account, new BigDecimal("1234.42"), "description"))));
        when(archivist.getAccount("5479", "123456")).thenReturn(account);

        Response response = service.list("5479", "123456", ui, request);
        TransactionsRepresentation transactions = (TransactionsRepresentation) response.getEntity();

        assertEquals(1, transactions.getTransactions().size());
        assertEquals("http://mock/accounts/5479-123456/transactions", transactions.getSelf().getHref());
    }

    @Test
    public void testGet() {
        UriInfo ui = mock(UriInfo.class);
        when(ui.getBaseUriBuilder()).then(new UriBuilderFactory(URI.create("http://mock")));

        Request request = mock(Request.class);

        Account account = mock(Account.class);
        when(account.getRegNo()).thenReturn("5479");
        when(account.getAccountNo()).thenReturn("123456");
        Transaction dbTranscation = new Transaction(account, new BigDecimal("1234.42"), "description");
        when(archivist.getTransaction("5479", "123456", "xxx-yyy")).thenReturn(dbTranscation);

        Response response = service.get("5479", "123456", "xxx-yyy", ui, request);
        TransactionRepresentation transaction = (TransactionRepresentation) response.getEntity();

        assertEquals("1234.42", transaction.getAmount());
        assertEquals("http://mock/accounts/5479-123456/transactions/" + dbTranscation.getId(), transaction.getSelf().getHref());
    }

}
