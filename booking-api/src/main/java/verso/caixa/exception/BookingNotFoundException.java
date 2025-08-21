package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import verso.caixa.enums.ErrorCode;

public class BookingNotFoundException extends BusinessException {
    public BookingNotFoundException(String message, ErrorCode errorCode) {
        super("Agendamento não encontrado!", message, errorCode.code());
    }

    @Override
    public Response.Status getHttpStatus() {
        return Response.Status.NOT_FOUND;
    }
}
