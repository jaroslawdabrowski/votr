package dk.nykredit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import dk.nykredit.bank.account.exposure.rs.AccountServiceExposure;
import dk.nykredit.bank.account.exposure.rs.TransactionServiceExposure;
import dk.nykredit.nic.rs.JaxRsRuntime;
import dk.nykredit.nic.rs.filter.RESTLogFilter;
import dk.nykredit.nic.rs.jersey.DisableJerseyMOXyFeature;

/**
 * Assembling the votr-services application by including the relevant resource.
 */
@ApplicationPath("/")
public class ServicesApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.addAll(Arrays.asList(
                AccountServiceExposure.class,
                TransactionServiceExposure.class)
        );

        classes.add(RESTLogFilter.class);
        JaxRsRuntime.configure(classes);

        return classes;
    }
}
