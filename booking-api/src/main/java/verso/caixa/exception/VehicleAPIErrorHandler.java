package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import verso.caixa.dto.ErrorResponseDTO;

@Provider
public class VehicleAPIErrorHandler implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        int statusCode = response.getStatus();

        ErrorResponseDTO errorMessage = null;

        try {
            errorMessage = response.readEntity(ErrorResponseDTO.class);
        } catch (Exception e) {
            // fallback simples caso o corpo não seja mapeável
            return new RemoteServiceException(
                    "Erro inesperado na API de veículos",
                    "ERRO_DESCONHECIDO",
                    "Falha ao ler corpo da resposta: " + e.getMessage()
            );
        }

        // Usa os campos do DTO para criar a exceção
        return new RemoteServiceException(
                errorMessage.title(),
                errorMessage.errorCode(),
                errorMessage.details(),
                errorMessage.path(),
                errorMessage.timestamp(),
                errorMessage.statusCode()
        );

    }
}
