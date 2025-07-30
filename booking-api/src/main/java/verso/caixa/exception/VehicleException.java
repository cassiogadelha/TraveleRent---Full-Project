package verso.caixa.exception;

import verso.caixa.enums.ErrorCode;

public class VehicleException extends BusinessException {
    public VehicleException(String message, ErrorCode errorCode) {
        super("Data para término de aluguel do veículo inválida!", message, errorCode.code());
    }
}
