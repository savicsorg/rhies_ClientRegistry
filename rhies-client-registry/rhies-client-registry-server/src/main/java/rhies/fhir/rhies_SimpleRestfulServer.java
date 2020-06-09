package rhies.fhir;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import static rhies.fhir.rhies_PatientResourceProvider.createProperties;

@WebServlet("/*")
public class rhies_SimpleRestfulServer extends RestfulServer {

    @Override
    protected void initialize() throws ServletException {
        createProperties();
        // Create a context for the appropriate version
        setFhirContext(FhirContext.forR4());

        // Register resource providers
        registerProvider(new rhies_PatientResourceProvider());

        // Format the responses in nice HTML
        registerInterceptor(new ResponseHighlighterInterceptor());

         // Register an authorization interceptor against the client
        registerInterceptor(new rhies_AuthorizationInterceptor());
    }
}
