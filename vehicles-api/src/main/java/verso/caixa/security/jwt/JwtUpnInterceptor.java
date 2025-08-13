package verso.caixa.security.jwt;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtUpnInterceptor implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (jwt != null && jwt.getClaim("upn") != null) {
            Object groups = jwt.getClaim("groups");
            System.out.println("Tipo de groups: " + (groups != null ? groups.getClass() : "null"));
            putIfPresent(requestContext, "upn");
            putIfPresent(requestContext, "email");
            putIfPresent(requestContext, "birthdate");
            putIfPresent(requestContext, "sub");
            putIfPresent(requestContext, "groups");
        }
    }

    private void putIfPresent(ContainerRequestContext ctx, String claimName) {
        Object claim = jwt.getClaim(claimName);
        if (claim != null) {
            ctx.setProperty(claimName, claim);
        }
    }
}
