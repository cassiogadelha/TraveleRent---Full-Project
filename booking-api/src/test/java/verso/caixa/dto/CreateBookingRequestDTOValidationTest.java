package verso.caixa.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CreateBookingRequestDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenVehicleIdIsNull() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
                null,
                "João",
                LocalDate.now(),
                LocalDate.now().plusDays(1)
        );

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Veículo não informado")));
    }

    @Test
    void shouldFailWhenCustomerNameIsNull() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
                UUID.randomUUID(),
                null,
                LocalDate.now(),
                null
        );

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Informe o nome do cliente")));
    }

    @Test
    void shouldFailWhenStartDateIsInThePast() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
                null,
                "João",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1)
        );

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("início não pode ser anterior")));
    }

    @Test
    void shouldFailWhenEndDateIsNotInFuture() {
        CreateBookingRequestDTO dto = new CreateBookingRequestDTO(
                null,
                "João",
                LocalDate.now(),
                LocalDate.now()
        );

        Set<ConstraintViolation<CreateBookingRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("término não pode ser anterior")));
    }

}