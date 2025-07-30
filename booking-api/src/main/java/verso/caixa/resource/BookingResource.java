package verso.caixa.resource;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.service.BookingService;

import java.util.UUID;

@Path("/api/v1/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {
    private final BookingService bookingService;

    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @POST
    @Transactional
    public Response createBooking(@Valid CreateBookingRequestDTO dto){
        return bookingService.createBooking(dto);
    }

    @GET
    public Response findAllBookings(@QueryParam("page") @DefaultValue("0") int page,
                                    @QueryParam("size") @DefaultValue("10") int size){

        return bookingService.getBookingList(page, size);
    }

    @GET
    @Path("{id}")
    public Response findById(@PathParam("id") UUID vehicleId){
        return Response.ok(bookingService.findById(vehicleId)).build();
    }

    @PATCH
    @Path("{id}")
    @Transactional
    public Response updateVehiclePartially(@PathParam("id") UUID vehicleId, UpdateBookingStatusRequest dto){
        return bookingService.updateVehicle(vehicleId, dto);
    }
}
