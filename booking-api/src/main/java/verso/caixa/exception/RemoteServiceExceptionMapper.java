package verso.caixa.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;

@Provider
public class RemoteServiceExceptionMapper implements ExceptionMapper<RemoteServiceException> {

    @Override
    public Response toResponse(RemoteServiceException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("errorCode", ex.getErrorCode());
        body.put("details", ex.getDetails().orElse("N/A"));
        body.put("path", ex.getPath().orElse("N/A"));
        body.put("timestamp", ex.getTimestamp().toString());
        body.put("httpStatus", ex.getHttpStatus());

        return Response.status(ex.getHttpStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
