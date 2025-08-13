package verso.caixa.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import verso.caixa.model.UserModel;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserDAO implements PanacheRepositoryBase<UserModel, UUID> {

    public Optional<UserModel> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

}
