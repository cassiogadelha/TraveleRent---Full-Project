package verso.caixa.client;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

@Priority(Interceptor.Priority.APPLICATION)
public class AuthHeaderFilter implements ClientRequestFilter {

    //@Inject
    //KeycloakAdminService keycloakAdminService;

    @Override
    public void filter(ClientRequestContext requestContext) {
        //String token = keycloakAdminService.getAdminToken();
        //requestContext.getHeaders().add("Authorization", "Bearer " + token);
    }
}