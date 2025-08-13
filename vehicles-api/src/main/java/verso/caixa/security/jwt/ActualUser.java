package verso.caixa.security.jwt;


import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;

import java.util.List;
import java.util.Set;

@RequestScoped
public class ActualUser {@Context
ContainerRequestContext context;

    public String getUpn() {
        Object upn = context.getProperty("upn");
        return upn != null ? upn.toString() : "desconhecido";
    }

    public String getEmail() {
        return getClaimAsString("email");
    }

    public String getBirthdate() {
        return getClaimAsString("birthdate");
    }

    public String getSub() {
        return getClaimAsString("sub");
    }

    @SuppressWarnings("unchecked")
    public List<String> getGroups() {
        Object groups = context.getProperty("groups");

        if (groups instanceof List) {
            return (List<String>) groups;
        } else if (groups instanceof Set) {
            return List.copyOf((Set<String>) groups); // converte Set para List
        } else if (groups instanceof String) {
            return List.of((String) groups);
        }

        return List.of();
    }

    private String getClaimAsString(String name) {
        Object value = context.getProperty(name);
        return value != null ? value.toString() : "desconhecido";
    }

}
