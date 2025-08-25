package verso.caixa.repository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import verso.caixa.model.VehicleStatus;

import java.util.UUID;

@ApplicationScoped
public class VehicleStatusDAO implements PanacheRepositoryBase<VehicleStatus, UUID> {
}
