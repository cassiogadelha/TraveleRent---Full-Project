package verso.caixa.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Optional;

@Getter
public class RemoteServiceException extends RuntimeException {

  private final String errorCode;
  private final String details;
  private final String path;
  private final Instant timestamp;
  private final int httpStatus;

  // Construtor padrão para casos com menos contexto
  public RemoteServiceException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
    this.details = null;
    this.path = null;
    this.timestamp = Instant.now();
    this.httpStatus = 500;
  }

  // Construtor completo
  public RemoteServiceException(String message, String errorCode, String details, String path, Instant timestamp, int httpStatus) {
    super(message);
    this.errorCode = errorCode;
    this.details = details;
    this.path = path;
    this.timestamp = timestamp != null ? timestamp : Instant.now();
    this.httpStatus = httpStatus;
  }

  public RemoteServiceException(String message, String errorCode, String details) {
    this(message, errorCode, details, "N/A", Instant.now(), 500);
  }


  public Optional<String> getDetails() {
    return Optional.ofNullable(details);
  }

  public Optional<String> getPath() {
    return Optional.ofNullable(path);
  }

  public String getSummary() {
    return String.format("[%s] %s (HTTP %d)", errorCode, getMessage(), httpStatus);
  }

  @Override
  public String toString() {
    return "RemoteServiceException{" +
            "message='" + getMessage() + '\'' +
            ", errorCode='" + errorCode + '\'' +
            ", httpStatus=" + httpStatus +
            ", path='" + path + '\'' +
            ", details='" + details + '\'' +
            ", timestamp=" + timestamp +
            '}';
  }

}

