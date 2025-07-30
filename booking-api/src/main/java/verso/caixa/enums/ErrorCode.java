package verso.caixa.enums;

public enum ErrorCode {
    // Booking
    INVALID_END_DATE("DATE-001"),

    NULL_VEHICLE("VEHICLE-001"),

    UNAVAILABLE_VEHICLE("VEHICLE-002"),

    INVALID_STATUS("STATUS-001");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
