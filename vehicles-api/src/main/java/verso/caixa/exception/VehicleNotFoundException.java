package verso.caixa.exception;

import verso.caixa.enums.ErrorCode;

public class VehicleNotFoundException extends BusinessException {
    public VehicleNotFoundException(String message, ErrorCode errorCode) {
        super("Erro ao buscar veículo.", message, errorCode.code());
    }
}
