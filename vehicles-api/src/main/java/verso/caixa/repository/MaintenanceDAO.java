package verso.caixa.repository;

import jakarta.enterprise.context.ApplicationScoped;
import verso.caixa.model.MaintenanceModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class MaintenanceDAO implements PanacheRepository<MaintenanceModel> {
}
