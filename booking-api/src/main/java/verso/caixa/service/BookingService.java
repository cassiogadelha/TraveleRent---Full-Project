package verso.caixa.service;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jetbrains.annotations.NotNull;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.dto.VehicleStatusChangedEvent;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.BookingNotFoundException;
import verso.caixa.exception.IllegalEndDateException;
import verso.caixa.exception.VehicleException;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.repository.BookingDAO;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@ApplicationScoped //cria somente uma instancia durante todo o ciclo de vida da aplicação
public class BookingService {

    BookingMapper bookingMapper;
    BookingDAO bookingDAO;
    SecurityIdentity securityIdentity;

    @RestClient
    private final VehicleAPIClient vehicleAPIClient;

    @Inject
    @Channel("booking-in")
    Emitter<BookingModel> activatedEmitter;

    @Inject
    @Channel("booking-out")
    Emitter<BookingModel> finishedEmitter;

    @Inject
    @Channel("booking-cancel")
    Emitter<BookingModel> canceledEmitter;


    public BookingService(BookingMapper bookingMapper, BookingDAO bookingDAO, @RestClient VehicleAPIClient vehicleAPIClient, SecurityIdentity securityIdentity) {
        this.securityIdentity = securityIdentity;
        this.bookingMapper = bookingMapper;
        this.bookingDAO = bookingDAO;
        this.vehicleAPIClient = vehicleAPIClient;
    }

    @Transactional
    @CacheInvalidate(cacheName = "all-bookings")
    public Response createBooking(@NotNull CreateBookingRequestDTO dto, UUID customerId, String customerName) {

        if (dto.endDate().isBefore(dto.startDate()))
            throw new IllegalEndDateException("A data de término não pode ser anterior a de início", ErrorCode.INVALID_END_DATE);

        VehicleAPIClient.Vehicle vehicle = vehicleAPIClient.findVehicleById(dto.vehicleId());

        if (vehicle == null) {
            throw new VehicleException("O veículo não existe!", ErrorCode.NULL_VEHICLE);
        }

        Log.info("VEHICLE STATUS: " + vehicle.status());
        if (vehicle.status().equals("UNDER_MAINTENANCE")) {
            throw new VehicleException("O veículo está em manutenção, portanto, indisponível para aluguel!.", ErrorCode.UNAVAILABLE_VEHICLE);
        }

        BookingModel newBooking = bookingMapper.toEntity(dto);
        newBooking.setCustomerName(customerName);
        newBooking.setCustomerId(customerId);

        bookingDAO.persist(newBooking);

        URI location = URI.create("/api/v1/bookings/" + newBooking.getBookingId());

        return Response.created(location)
                .entity(bookingMapper.toResponseDTO(newBooking))
                .build();
    }

    @CacheResult(cacheName = "all-bookings")
    public Response getAllBookings(UUID customerId, int page, int size, boolean isAdmin) {
        List<BookingModel> bookings;

        if (isAdmin) {
            bookings = bookingDAO.findAll()
                    .page(Page.of(page, size))
                    .list();
        } else {
            bookings = bookingDAO.findByCustomerId(customerId, page, size);
        }

        if (bookings.isEmpty()) {
            String message = isAdmin
                    ? "A lista de agendamentos está vazia."
                    : "Você não possui nenhum agendamento ainda.";

            Map<String, String> response = Map.of("mensagem", message);
            return Response.ok(response).build();
        }

        return Response.ok(bookingMapper.toResponseDTOList(bookings)).build();
    }

    public Response findById(UUID bookingId) {
        BookingModel bookingModelFound = bookingDAO.findById(bookingId);

        if (bookingModelFound == null) {
            return Response.status(404).build();
        }

        ResponseBookingDTO dto = bookingMapper.toResponseDTO(bookingModelFound);

        return Response.ok(dto).build();
    }

    @CacheInvalidate(cacheName = "all-bookings")
    public Response checkBooking(UUID bookingId, UpdateBookingStatusRequest dto) {
        BookingModel bookingModel = bookingDAO.findById(bookingId);

        if (bookingModel == null) throw new BookingNotFoundException("Erro ao editar agendamento.", ErrorCode.NULL_BOOKING);

        try {

            bookingModel.setStatus(dto.newStatus());
            emitStatusChange(bookingModel);

        } catch (RuntimeException e) {

            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.noContent().build();
    }

    private void emitStatusChange(BookingModel booking) {
        switch (booking.getStatus()) {
            case ACTIVATED -> activatedEmitter.send(booking);
            case FINISHED -> finishedEmitter.send(booking);
            case CANCELED -> canceledEmitter.send(booking);
        }
    }

    @Transactional
    public void checkVehicle(UUID vehicleId) {

        BookingModel possibleBooking = bookingDAO.findByVehicleId(vehicleId);

        if (possibleBooking != null) {
            possibleBooking.setStatus(BookingStatusEnum.CANCELED);
        }

        System.out.println("AGENDAMENTO CANCELADO!!! VEÍCULO ENTROU EM MANUTENÇÃO");
        //NOTIFICAR USUÁRIO

    }
}
