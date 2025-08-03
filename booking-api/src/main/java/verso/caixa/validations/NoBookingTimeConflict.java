package verso.caixa.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingConflictValidator.class)
@Documented
public @interface NoBookingTimeConflict {
    String message() default "Já existe uma reserva conflitante para este veículo e período.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
