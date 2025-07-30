package verso.caixa.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAcessoryRequestDTO(
    @NotBlank
    String name
) {
}
