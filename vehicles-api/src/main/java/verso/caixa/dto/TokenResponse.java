package verso.caixa.dto;

public record TokenResponse(
        String token
) {
    public TokenResponse(String token) {
        this.token = token;
    }
}
