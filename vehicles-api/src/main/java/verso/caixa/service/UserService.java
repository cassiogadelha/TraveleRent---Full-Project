package verso.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.CreateUserDTO;
import verso.caixa.model.UserModel;
import verso.caixa.repository.UserDAO;
import verso.caixa.security.jwt.BCryptAdapter;

@ApplicationScoped
public class UserService {

    UserDAO userDAO;
    BCryptAdapter bCryptAdapter;

    public UserService(UserDAO userDAO, BCryptAdapter bCryptAdapter) {
        this.userDAO = userDAO;
        this.bCryptAdapter = bCryptAdapter;
    }

    public Response findByUsername(CreateUserDTO dto) {

        if (userDAO.findByUsername(dto.username()).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Usuário já existe!").build();
        }

        UserModel user = new UserModel();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setRoles(dto.roles());

        String hashedPassword = bCryptAdapter.hash(dto.password());
        user.setPassword(hashedPassword);

        userDAO.persist(user);

        return Response.status(Response.Status.CREATED).entity(user).build();
    }
}
