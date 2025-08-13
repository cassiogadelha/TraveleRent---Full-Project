package verso.caixa.security.jwt;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import static org.eclipse.microprofile.jwt.Claims.birthdate;

@Path("/secured")
@RequestScoped //The @RequestScoped scope is required to enable injection of the birthdate claim as a String.
public class TokenSecuredResource {

    @Inject
    JsonWebToken jwt;
    /*
    The JsonWebToken interface is injected, providing access to claims associated with the current authenticated token.
    This interface extends java.security.Principal.
     */

    @Inject
    @Claim(standard = Claims.birthdate)
    String birthdate;
    /*
    The birthdate claim is injected as a String. This highlights why the @RequestScoped scope is mandatory.
     */
    @GET
    @Path("permit-all")
    @PermitAll
    /*
    The @PermitAll is a standard Jakarta security annotation. It indicates that the given endpoint is accessible by all
    callers, whether authenticated or not.
     */
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@Context SecurityContext ctx) {
        /*
        The Jakarta REST SecurityContext is injected to inspect the security state of the request.
        The getResponseString() function generates the response.
         */
        return getResponseString(ctx);
    }

    @GET
    @Path("roles-allowed")
    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowed(@Context SecurityContext ctx) {
        return getResponseString2(ctx) + ", birthdate: " + jwt.getClaim("birthdate").toString();
    }

    @GET
    @Path("roles-allowed-admin")
    @RolesAllowed("Admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloRolesAllowedAdmin(@Context SecurityContext ctx) {
        return getResponseString3(ctx) + ", birthdate: " + birthdate; //The injected birthdate claim is directly used to construct the response.
    }

    private String getResponseString3(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private String getResponseString2(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) { //Checks if the call is insecure by checking if the request user/caller Principal against null.
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {   //Ensures the names in the Principal and
                                                                                // JsonWebToken match because the
                                                                                // JsonWebToken represents the current Principal.
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName(); //Retrieves the name of the Principal.
        }
        return String.format("hello %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
        /*
        Builds a response containing the caller’s name, the isSecure() and getAuthenticationScheme() states of the
        request SecurityContext, and whether a non-null JsonWebToken was injected.
         */
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}