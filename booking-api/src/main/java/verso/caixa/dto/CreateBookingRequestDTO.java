package verso.caixa.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import verso.caixa.validations.NoBookingTimeConflict;

import java.time.LocalDate;
import java.util.UUID;

@NoBookingTimeConflict
public record CreateBookingRequestDTO(
        @NotNull(message = "Veículo não informado!")
        UUID vehicleId,

        @NotNull(message = "Informe o nome do cliente.")
        String customerName,

        @NotNull(message = "Informe a data de início do  aluguel.")
        @FutureOrPresent(message = "A data de início não pode ser anterior a hoje.")
        LocalDate startDate,

        @NotNull(message = "Informe a data de início término do aluguel.")
        @Future(message = "A data de término não pode ser anterior a de início")
        LocalDate endDate
) {
}
