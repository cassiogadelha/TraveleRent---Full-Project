package verso.caixa.dto;

import java.util.UUID;

public record AccessoryResponseDTO(
        UUID accessoryId,
        String name
) {
}
