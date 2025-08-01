package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import verso.caixa.enums.ErrorCode;

public class IllegalEndDateException extends BusinessException {
    public IllegalEndDateException(String message, ErrorCode errorCode) {
        super("Data para término de aluguel do veículo inválida!", message, errorCode.code());
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.CONFLICT;
    }
}
