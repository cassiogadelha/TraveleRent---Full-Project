package verso.caixa.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import verso.caixa.model.VehicleModel;

import java.util.UUID;

@ApplicationScoped
public class VehicleDAO implements PanacheRepositoryBase<VehicleModel, UUID> { //tem que ser o Base para poder especificar o UUID
}
