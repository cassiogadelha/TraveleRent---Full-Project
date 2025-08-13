package verso.caixa.security.jwt;

import java.util.*;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.Claims;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.JsonWebToken;
import verso.caixa.dto.TokenResponse;
import verso.caixa.dto.UserDTO;
import verso.caixa.model.UserModel;
import verso.caixa.repository.UserDAO;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    UserDAO userDAO;
    BCryptAdapter bCryptAdapter;
    JsonWebToken jwt;

    public AuthResource(UserDAO userDAO, BCryptAdapter bCryptAdapter, JsonWebToken jwt) {
        this.userDAO = userDAO;
        this.bCryptAdapter = bCryptAdapter;
        this.jwt = jwt;
    }

    @POST
    @Path("/login")
    @Transactional
    public Response login(UserDTO dto, @Context SecurityContext ctx) {

        System.out.println("PRINCIPAL: " + ctx.getUserPrincipal());

        Optional<UserModel> userOpt = userDAO.findByUsername(dto.username());

        if (userOpt.isEmpty()) {
            UserModel user = new UserModel();
            user.setUsername("cassioADM");
            user.setEmail("gadelha.cassio@verso.com");
            user.setPassword(bCryptAdapter.hash("cassioADM"));
            user.setRoles(Set.of("Admin"));

            userDAO.persist(user);
            userOpt = Optional.of(user);
        }

        if (!bCryptAdapter.verify(dto.password(), userOpt.get().getPassword())) {
            System.out.println("PASSWORD DTO: " + dto.password() + "PASSWORD USER OPT:" + userOpt.get().getPassword());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas").build();
        }

        String token = Jwt.issuer("https://vehicles.api.auth")
                .upn("vehicles-api@quarkus.io")
                //.groups(new HashSet<>(List.of("Admin")))
                .groups(userOpt.get().getRoles())
                .expiresAt(System.currentTimeMillis() / 1000 + 3600)
                .claim(Claims.birthdate.name(), "2025-08-12")
                .sign();

        return Response.ok(new TokenResponse(token)).build();
    }
}