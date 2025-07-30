package verso.caixa.exception;

import verso.caixa.enums.ErrorCode;

public class VehicleDeletionException extends BusinessException {
    public VehicleDeletionException(String message, ErrorCode errorCode) {
        super("Erro ao deletar veículo", message, errorCode.code());
    }
}
