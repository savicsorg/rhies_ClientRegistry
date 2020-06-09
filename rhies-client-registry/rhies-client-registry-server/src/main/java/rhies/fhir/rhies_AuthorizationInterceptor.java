package rhies.fhir;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.interceptor.auth.AuthorizationInterceptor;
import ca.uhn.fhir.rest.server.interceptor.auth.IAuthRule;
import ca.uhn.fhir.rest.server.interceptor.auth.RuleBuilder;

import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class rhies_AuthorizationInterceptor extends AuthorizationInterceptor {

    @Override
    public List<IAuthRule> buildRuleList(RequestDetails theRequestDetails) {

        boolean userIsAdmin = false;
        String authHeader = theRequestDetails.getHeader("Authorization");
        if (authHeader == null || authHeader.startsWith("Basic ") == false) {
            utils.error("Missing or invalid Authorization header value");
        } else {
            String base64 = authHeader.substring("Basic ".length());
            String base64decoded = new String(Base64.decodeBase64(base64));
            String[] parts = base64decoded.split("\\:");

            String username = parts[0];
            String password = parts[1];
            boolean result = false;

            try {
                result = rhies_PatientResourceProvider.authenticate(username, password);

            } catch (UnknownHostException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (result == false) {
                // throw new AuthenticationException("Invalid username or password");
                utils.error("Invalid username or password");
            } else {
                userIsAdmin = true;
            }

        }

        // If the user is an admin, allow everything
        if (userIsAdmin) {
            return new RuleBuilder()
                    .allowAll()
                    .build();
        }

        // By default, deny everything. This should never get hit, but it's 
        // good to be defensive
        return new RuleBuilder()
                .denyAll()
                .build();
    }

}
