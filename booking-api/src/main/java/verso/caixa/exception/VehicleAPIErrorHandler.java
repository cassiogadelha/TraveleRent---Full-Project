package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import verso.caixa.dto.ErrorResponseDTO;

import java.time.Instant;

@Provider
public class VehicleAPIErrorHandler implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        int statusCode = response.getStatus();

        ErrorResponseDTO errorMessage = null;

        if (statusCode == 401 || statusCode == 403) {
            try {
                errorMessage = response.readEntity(ErrorResponseDTO.class);
                return new RemoteServiceException(
                        errorMessage.title(),
                        errorMessage.errorCode(),
                        errorMessage.details(),
                        errorMessage.path(),
                        errorMessage.timestamp(),
                        errorMessage.statusCode()
                );
            } catch (Exception e) {
                return new RemoteServiceException(
                        "Acesso não autorizado à API de veículos",
                        "ACESSO_NEGADO",
                        "Verifique se o token de autenticação está presente e válido",
                        "/api/v1/bookings",
                        Instant.now(),
                        statusCode
                );
            }
        }


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
