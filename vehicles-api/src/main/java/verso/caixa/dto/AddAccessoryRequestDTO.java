package verso.caixa.dto;

import jakarta.validation.constraints.NotBlank;

public record AddAccessoryRequestDTO(
    @NotBlank
    String name
) {
}
