package verso.caixa.validations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.repository.BookingDAO;

@ApplicationScoped
public class BookingConflictValidator implements ConstraintValidator<NoBookingTimeConflict, CreateBookingRequestDTO> {

    @Inject
    private BookingDAO bookingDAO;

    @Override
    public boolean isValid(CreateBookingRequestDTO dto, ConstraintValidatorContext context) {
        if (dto.vehicleId() == null || dto.startDate() == null || dto.endDate() == null) {
            return true; // Deixa outras validações lidarem com isso
        }

        return !bookingDAO.existConflict(dto.vehicleId(), dto.startDate(), dto.endDate());
    }
}

