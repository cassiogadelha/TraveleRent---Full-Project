package verso.caixa.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import verso.caixa.model.BookingModel;

import java.util.UUID;

@ApplicationScoped
public class BookingDAO implements PanacheRepositoryBase<BookingModel, UUID> {
}
