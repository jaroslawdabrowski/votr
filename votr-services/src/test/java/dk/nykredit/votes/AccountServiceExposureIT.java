package dk.nykredit.votes;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import dk.nykredit.nic.core.diagnostic.ContextInfo;
import dk.nykredit.nic.core.diagnostic.DiagnosticContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class AccountServiceExposureIT {

    private DiagnosticContext dCtx;

    @Before
    public void setupLogToken() {
        dCtx = new DiagnosticContext(new ContextInfo() {
            @Override
            public String getLogToken() {
                return "junit-" + System.currentTimeMillis();
            }

            @Override
            public void setLogToken(String s) {

            }
        });
        dCtx.start();
    }

    @After
    public void removeLogToken() {
        dCtx.stop();
    }

    @Test
    public void testListAccounts() {
        WebTarget target = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class).target("http://localhost:7001/votr");
        Map<String, Object> response = target.path("accounts")
            .request()
            .accept("application/hal+json")
            .header("X-Client-Version", "1.0.0")
            .header("X-Log-Token", DiagnosticContext.getLogToken())
            .get(Map.class);

        assertNotNull(response.get("_embedded"));
        Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
        assertTrue(((List) embedded.get("accounts")).size() >= 2);
    }

    @Test
    public void testGetAccount() {
        WebTarget target = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class).target("http://localhost:7001/votr");
        Map<String, Object> response = target.path("accounts").path("5479-1234567")
            .request()
            .accept("application/hal+json")
            .header("X-Client-Version", "1.0.0")
            .header("X-Log-Token", DiagnosticContext.getLogToken())
            .get(Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("Checking account", response.get("name"));
    }

    @Test
    public void testCreateAccount() throws Exception {
        String accessToken = requestAccessToken("advisor1");

        int accountNo = ThreadLocalRandom.current().nextInt(9999999);

        WebTarget bankServices =
            ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class).target("http://localhost:7001/votr");
        Map<String, String> accountCreate = new ConcurrentHashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", Integer.toString(accountNo));
        accountCreate.put("name", "Savings account");
        Map<String, Object> response = bankServices.path("accounts").path("5479-" + accountNo)
            .request()
            .accept("application/hal+json")
            .header("X-Client-Version", "1.0.0")
            .header("X-Log-Token", DiagnosticContext.getLogToken())
            .header("Authorization", "Bearer " + accessToken)
            .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals(Integer.toString(accountNo), response.get("accountNo"));
        assertEquals("Savings account", response.get("name"));
    }


    @Test
    public void testCreateAccountAccessDenied() throws Exception {
        String accessToken = requestAccessToken("customer1");

        WebTarget bankServices =
            ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class).target("http://localhost:7001/votr");
        Map<String, String> accountCreate = new ConcurrentHashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "5555555");
        accountCreate.put("name", "Checking account");
        Response response = bankServices.path("accounts").path("5479-5555555")
            .request(MediaType.APPLICATION_JSON_TYPE)
            .accept("application/hal+json")
            .header("X-Client-Version", "1.0.0")
            .header("X-Log-Token", DiagnosticContext.getLogToken())
            .header("Authorization", "Bearer " + accessToken)
            .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Response.class);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateAccount() throws Exception {
        String accessToken = requestAccessToken("advisor1");

        WebTarget bankServices =
            ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class).target("http://localhost:7001/votr");
        Map<String, String> accountCreate = new ConcurrentHashMap<>();
        accountCreate.put("regNo", "5479");
        accountCreate.put("accountNo", "1234567");
        accountCreate.put("name", "new account name");
        Map<String, Object> response = bankServices.path("accounts").path("5479-" + "1234567")
            .request()
            .accept("application/hal+json")
            .header("X-Client-Version", "1.0.0")
            .header("X-Log-Token", DiagnosticContext.getLogToken())
            .header("Authorization", "Bearer " + accessToken)
            .put(Entity.entity(accountCreate, MediaType.APPLICATION_JSON_TYPE), Map.class);

        assertEquals("5479", response.get("regNo"));
        assertEquals("1234567", response.get("accountNo"));
        assertEquals("new account name", response.get("name"));
    }

    private String requestAccessToken(final String username) throws UnsupportedEncodingException {
        WebTarget oauth2Service = ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class).target("http://localhost:7001/security");
        MultivaluedMap<String, String> request = new MultivaluedHashMap<>();
        request.putSingle("grant_type", "client_credentials");
        String credentials = Base64.getEncoder().encodeToString((username + ":passw0rd").getBytes("UTF-8"));
        Map<String, String> oauthResponse = oauth2Service.path("oauth2/token")
            .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
            .header("Authorization", "Basic " + credentials)
            .header("X-Log-Token", DiagnosticContext.getLogToken())
            .header("X-Client-Version", "1.0.0")
            .post(Entity.form(request), Map.class);
        return oauthResponse.get("access_token");
    }
}
