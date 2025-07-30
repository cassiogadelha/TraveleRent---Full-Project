package verso.caixa.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String title;
    private final String errorCode;

    public BusinessException(String title, String message, String errorCode) {
        super(message);
        this.title = title;
        this.errorCode = errorCode;
    }
}

