package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import verso.caixa.enums.ErrorCode;

public class VehicleNotFoundException extends BusinessException {
    public VehicleNotFoundException(String message, ErrorCode errorCode) {
        super("Erro ao buscar veículo.", message, errorCode.code());
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.NOT_FOUND;
    }
}
