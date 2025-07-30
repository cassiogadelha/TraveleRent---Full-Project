package verso.caixa.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jetbrains.annotations.NotNull;
import verso.caixa.client.VehicleAPIClient;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.IllegalEndDateException;
import verso.caixa.exception.VehicleException;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.repository.BookingDAO;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Getter
@ApplicationScoped //cria somente uma instancia durante todo o ciclo de vida da aplicação
public class BookingService {

    BookingMapper bookingMapper;
    BookingDAO bookingDAO;
    private VehicleAPIClient vehicleAPIClient;

    public BookingService(BookingMapper bookingMapper, BookingDAO bookingDAO, @RestClient VehicleAPIClient vehicleAPIClient){

        this.bookingMapper = bookingMapper;
        this.bookingDAO = bookingDAO;
        this.vehicleAPIClient = vehicleAPIClient;
    }

    @Transactional
    public Response createBooking(@NotNull CreateBookingRequestDTO dto) {

        if (dto.endDate().isBefore(dto.startDate()))
            throw new IllegalEndDateException("A data de término não pode ser anterior a de início", ErrorCode.INVALID_END_DATE);

        VehicleAPIClient.Vehicle vehicle = vehicleAPIClient.findVehicleById(dto.vehicleId());

        if (vehicle == null) {
            throw new VehicleException("O veículo não existe!", ErrorCode.NULL_VEHICLE);
        }

        if (!vehicle.status().equals("AVAILABLE")) {
            throw new VehicleException("O veículo não está disponível para aluguel.", ErrorCode.UNAVAILABLE_VEHICLE);
        }

        BookingModel newBooking = bookingMapper.toEntity(dto);

        bookingDAO.persist(newBooking);

        URI location = URI.create("/api/v1/bookings/" + newBooking.getBookingId());

        return Response.created(location)
                .entity(newBooking)
                .build();
    }

    public Response getBookingList(int page, int size) {
        PanacheQuery<BookingModel> bookings = bookingDAO.findAll();
        bookings.page(Page.of(page, size));

        if (bookings.list().isEmpty()) {
            Map<String, String> response = Map.of("mensagem", "A lista de agendamentos está vazia."); //cria um map imutavel para ser convertido facilmente em Json
            return Response.ok(response).build();
        } else {
            return Response.ok(bookingMapper.toResponseDTOList(bookings.list())).build();
        }
    }

    public Response findById(UUID bookingId) {
        BookingModel bookingModelFound = bookingDAO.findById(bookingId);

        if (bookingModelFound == null) {
            return Response.status(404).build();
        }

        ResponseBookingDTO dto = bookingMapper.toResponseDTO(bookingModelFound);

        return Response.ok(dto).build();
    }

    public Response updateVehicle(UUID bookingId, UpdateBookingStatusRequest dto) {
        BookingModel bookingModel = bookingDAO.findById(bookingId);

        if (bookingModel == null) return Response.status(404).build();

        try {
            bookingModel.setStatus(dto.newStatus());
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.noContent().build();
    }
}
