package verso.caixa.enums;

public enum ErrorCode {

    // Veículo
    VEHICLE_NOT_FOUND("VEHICLE-001"),
    VEHICLE_RENTED_DELETE_DENIED("VEHICLE-002"),

    //Auth
    UNAUTHORIZED_USER("AUTH_001"),
    UNAUTHORIZED_ROLE("AUTH_002");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}

