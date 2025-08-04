package verso.caixa.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import verso.caixa.dto.ErrorResponseDTO;

@Provider
public class RemoteServiceExceptionMapper implements ExceptionMapper<RemoteServiceException> {

    /*
    O Jakarta REST (JAX-RS) tem um mecanismo interno que identifica quando uma exceção é lançada dentro
    do ciclo de vida de uma requisição HTTP. Quando isso acontece:

- A exceção explode durante o processamento do endpoint (ex: GET /teste-exception)
- O container verifica se existe um ExceptionMapper registrado para o tipo da exceção
- Se encontrar (no caso, RemoteServiceExceptionMapper), ele usa o método toResponse(...)
- Esse método constrói a Response manualmente, como em ErrorResponseDTO na linha 29
- A resposta final é enviada ao cliente com o corpo JSON + status definido(418, etc.)

     */

    @Override
    public Response toResponse(RemoteServiceException ex) {
        ErrorResponseDTO dto = new ErrorResponseDTO(
                ex.getMessage(),
                ex.getDetails().orElse("N/A"),
                ex.getHttpStatus(),
                ex.getPath().orElse("N/A"),
                ex.getTimestamp(),
                ex.getErrorCode()


        );

        return Response.status(ex.getHttpStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(dto)
                .build();
    }
}
