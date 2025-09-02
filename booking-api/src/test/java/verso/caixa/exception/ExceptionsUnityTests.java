package verso.caixa.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import verso.caixa.dto.ErrorResponseDTO;
import verso.caixa.enums.ErrorCode;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExceptionsUnityTests {

    private void injectUriInfo(BusinessExceptionHandler handler, UriInfo uriInfo) {
        try {
            Field field = BusinessExceptionHandler.class.getDeclaredField("uriInfo");
            field.setAccessible(true);
            field.set(handler, uriInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldMapBookingNotFoundExceptionToNotFoundResponse() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn("/api/v1/bookings/123");

        BusinessExceptionHandler handler = new BusinessExceptionHandler();
        injectUriInfo(handler, uriInfo);

        BookingNotFoundException ex = new BookingNotFoundException("Agendamento não existe", ErrorCode.NULL_BOOKING);

        Response response = handler.toResponse(ex);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getEntity();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Agendamento não encontrado!", body.title());
        assertEquals("Agendamento não existe", body.details());
        assertEquals(ErrorCode.NULL_BOOKING.code(), body.errorCode());
        assertEquals("/api/v1/bookings/123", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void shouldMapIllegalEndDateExceptionToConflictResponse() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn("/api/v1/bookings");

        BusinessExceptionHandler handler = new BusinessExceptionHandler();
        injectUriInfo(handler, uriInfo);

        IllegalEndDateException ex = new IllegalEndDateException("Data final inválida", ErrorCode.INVALID_END_DATE);

        Response response = handler.toResponse(ex);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getEntity();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Data para término de aluguel do veículo inválida!", body.title());
        assertEquals("Data final inválida", body.details());
        assertEquals(ErrorCode.INVALID_END_DATE.code(), body.errorCode());
        assertEquals("/api/v1/bookings", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void shouldMapIllegalBookingStatusToConflictResponse() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn("/api/v1/bookings/status");

        BusinessExceptionHandler handler = new BusinessExceptionHandler();
        injectUriInfo(handler, uriInfo);

        IllegalBookingStatus ex = new IllegalBookingStatus("Status inválido", ErrorCode.INVALID_STATUS);

        Response response = handler.toResponse(ex);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getEntity();

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertEquals("Data para término de aluguel do veículo inválida!", body.title());
        assertEquals("Status inválido", body.details());
        assertEquals(ErrorCode.INVALID_STATUS.code(), body.errorCode());
        assertEquals("/api/v1/bookings/status", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void shouldMapVehicleExceptionToNotFoundResponse() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn("/api/v1/vehicles/abc");

        BusinessExceptionHandler handler = new BusinessExceptionHandler();
        injectUriInfo(handler, uriInfo);

        VehicleException ex = new VehicleException("Veículo não encontrado", ErrorCode.NULL_VEHICLE);

        Response response = handler.toResponse(ex);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getEntity();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals("Problema ao procurar o veículo!", body.title());
        assertEquals("Veículo não encontrado", body.details());
        assertEquals(ErrorCode.NULL_VEHICLE.code(), body.errorCode());
        assertEquals("/api/v1/vehicles/abc", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void shouldThrowAndCatchBusinessException() {
        try {
            throw new BusinessException("Título", "Mensagem", "ERR001");
        } catch (BusinessException ex) {
            assertEquals("Título", ex.getTitle());
            assertEquals("Mensagem", ex.getMessage());
            assertEquals("ERR001", ex.getErrorCode());
            assertEquals(Response.Status.BAD_REQUEST, ex.getHttpStatus());
        }
    }

    @Test
    void shouldThrowAndCatchBookingNotFoundException() {
        try {
            throw new BookingNotFoundException("Não encontrado", ErrorCode.NULL_BOOKING);
        } catch (BookingNotFoundException ex) {
            assertEquals("Agendamento não encontrado!", ex.getTitle());
            assertEquals("Não encontrado", ex.getMessage());
            assertEquals(ErrorCode.NULL_BOOKING.code(), ex.getErrorCode());
            assertEquals(Response.Status.NOT_FOUND, ex.getHttpStatus());
        }
    }

    @Test
    void shouldHandleBusinessExceptionViaMapper() {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn("/api/test");

        BusinessExceptionHandler handler = new BusinessExceptionHandler();
        injectUriInfo(handler, uriInfo);

        BusinessException ex = new BusinessException("Erro genérico", "Falha", "GEN001");

        Response response = handler.toResponse(ex);
        ErrorResponseDTO body = (ErrorResponseDTO) response.getEntity();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Erro genérico", body.title());
        assertEquals("Falha", body.details());
        assertEquals("GEN001", body.errorCode());
        assertEquals("/api/test", body.path());
        assertNotNull(body.timestamp());
    }
}
