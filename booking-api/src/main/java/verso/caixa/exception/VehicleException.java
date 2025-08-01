package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import verso.caixa.enums.ErrorCode;

public class VehicleException extends BusinessException {
    public VehicleException(String message, ErrorCode errorCode) {
        super("Problema ao procurar o veículo!", message, errorCode.code());
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.NOT_FOUND;
    }
}
