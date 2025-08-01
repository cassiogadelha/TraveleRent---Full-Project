package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import verso.caixa.enums.ErrorCode;

public class VehicleDeletionException extends BusinessException {
    public VehicleDeletionException(String message, ErrorCode errorCode) {
        super("Erro ao deletar veículo", message, errorCode.code());
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.CONFLICT;
    }
}
