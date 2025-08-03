package verso.caixa.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import verso.caixa.model.BookingModel;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class BookingDAO implements PanacheRepositoryBase<BookingModel, UUID> {
    public boolean existConflict(UUID incomingVehicleId, LocalDate incomingStartDate, LocalDate incomingEndDate) {
        return count(
                "vehicleId = :vehicleId AND startDate <= :endDate AND endDate >= :startDate",
                Parameters.with("vehicleId", incomingVehicleId)
                        .and("startDate", incomingStartDate)
                        .and("endDate", incomingEndDate)) > 0;

    }

    /*
    É uma forma abreviada que o Quarkus oferece via Panache, que permite escrever queries posicionais diretamente
    como String — ou seja, é uma abstração simplificada sobre JPQL.

    [vehicleId = ?1]
    é uma condição de filtro escrita em JPQL/Panache, e o equivalente direto em SQL seria:
    WHERE vehicle_id = ?

    [startDate <= ?3]
    é uma condição de filtro que compara a data de início da reserva existente com a data de término da nova reserva
    que você está tentando inserir.
    WHERE start_date <= '2025-08-15'





    */
}
