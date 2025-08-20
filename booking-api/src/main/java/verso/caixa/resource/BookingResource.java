package verso.caixa.resource;

import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.exception.RemoteServiceException;
import verso.caixa.service.BookingService;

import java.util.UUID;

@Path("/api/v1/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {
    private final BookingService bookingService;
    private final SecurityIdentity securityIdentity;

    public BookingResource(BookingService bookingService, SecurityIdentity securityIdentity) {
        this.bookingService = bookingService;
        this.securityIdentity = securityIdentity;
    }

    @POST
    @Transactional
    public Response createBooking(@Valid CreateBookingRequestDTO dto){

        DefaultJWTCallerPrincipal principal = (DefaultJWTCallerPrincipal) securityIdentity.getPrincipal();
        UUID customerId = UUID.fromString(principal.getSubject());
        String customerName = principal.getName();

        return bookingService.createBooking(dto, customerId, customerName);
    }

    @GET
    @RolesAllowed("realm-admin")
    public Response findAllBookings(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size){

        return bookingService.getAllBookings(null, page, size, true);
    }

    @GET
    @Path("{id}")
    @RolesAllowed("realm-admin")
    public Response findById(@PathParam("id") UUID vehicleId){
        return Response.ok(bookingService.findById(vehicleId)).build();
    }

    @PATCH
    @Path("{id}")
    @Transactional
    public Response updateBookingPartially(@PathParam("id") UUID vehicleId, UpdateBookingStatusRequest dto){
        return bookingService.updateBooking(vehicleId, dto);
    }

    @GET
    @Path("my")
    public Response getMyBookings(@QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("10") int size) {

        DefaultJWTCallerPrincipal principal = (DefaultJWTCallerPrincipal) securityIdentity.getPrincipal();
        UUID customerId = UUID.fromString(principal.getSubject());

        return bookingService.getAllBookings(customerId, page, size, false);
    }

    @GET
    @Path("/teste-exception")
    public String throwsException() {
        throw new RemoteServiceException(
                "Erro de simulação",
                "ERRO_TESTE",
                "Esse é um erro gerado intencionalmente para testar o mapper",
                "/api/teste-exception",
                null,
                418   //"I'm a teapot"
        );
    }

}
