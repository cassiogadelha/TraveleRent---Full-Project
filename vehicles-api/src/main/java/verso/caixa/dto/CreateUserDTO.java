package verso.caixa.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record CreateUserDTO (
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String email,
        @NotBlank
        Set<String> roles
) {
}
