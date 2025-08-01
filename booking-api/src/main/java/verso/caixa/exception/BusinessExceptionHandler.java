package verso.caixa.exception;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import verso.caixa.dto.ErrorResponseDTO;

import java.time.Instant;

@Provider //- registra essa classe no Quarkus como um interceptador global de exceções.
//Sempre que uma exceção do tipo BusinessException for lançada e não capturada por try/catch, esse mapper será acionado.

public class BusinessExceptionHandler implements ExceptionMapper<BusinessException> {

    //ExceptionMapper<T> é uma interface do Jakarta REST (JAX-RS) usada para converter exceções em respostas HTTP.

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(BusinessException exception) {
        int statusCode = exception.getHttpStatus().getStatusCode();

        String path = uriInfo.getPath(); // ex: /api/v1/vehicles/...
        Instant timestamp = Instant.now();

        ErrorResponseDTO response = new ErrorResponseDTO(
                exception.getTitle(),
                exception.getMessage(),
                statusCode,
                path,
                timestamp,
                exception.getErrorCode()
        );

        return Response.status(statusCode).entity(response).build();
    }

}

