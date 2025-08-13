package verso.caixa.security.jwt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.microprofile.jwt.Claims;
import io.smallrye.jwt.build.Jwt;

/**
 * A utility class to generate and print a JWT token string to stdout.
 */
public class GenerateToken {

    /**
     * Generates and prints a JWT token.
     */
    public static void main(String[] args) {
        String token = Jwt.issuer("https://example.com/issuer")
                .upn("jdoe@quarkus.io")
                .groups(new HashSet<>(List.of("User")))
                .claim(Claims.birthdate.name(), "2001-07-13")
                .sign();

        System.out.println(token);
        System.exit(0);
    }
}
