package verso.caixa.service;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.dto.UpdateBookingStatusRequest;
import verso.caixa.enums.BookingStatusEnum;
import verso.caixa.enums.ErrorCode;
import verso.caixa.exception.BookingNotFoundException;
import verso.caixa.exception.IllegalEndDateException;
import verso.caixa.exception.VehicleException;
import verso.caixa.kafka.BookingEmitterWrapper;
import verso.caixa.kafka.VehicleProducerDTO;
import verso.caixa.mapper.BookingMapper;
import verso.caixa.model.BookingModel;
import verso.caixa.model.VehicleStatus;
import verso.caixa.repository.BookingDAO;
import verso.caixa.repository.VehicleStatusDAO;
import verso.caixa.twilio.SmsService;

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
    SmsService smsService;
    VehicleStatusDAO vehicleStatusDAO;
    VehicleStatusService vehicleStatusService;
    BookingEmitterWrapper bookingEmitterWrapper;

    /*@RestClient
    private final VehicleAPIClient vehicleAPIClient;*/
    public BookingService(BookingMapper bookingMapper,
                          BookingDAO bookingDAO,
                          //@RestClient VehicleAPIClient vehicleAPIClient,
                          SecurityIdentity securityIdentity,
                          SmsService smsService,
                          VehicleStatusDAO vehicleStatusDAO,
                          VehicleStatusService vehicleStatusService,
                          BookingEmitterWrapper bookingEmitterWrapper) {
        this.securityIdentity = securityIdentity;
        this.bookingMapper = bookingMapper;
        this.bookingDAO = bookingDAO;
        //this.vehicleAPIClient = vehicleAPIClient;
        this.smsService = smsService;
        this.vehicleStatusDAO = vehicleStatusDAO;
        this.vehicleStatusService = vehicleStatusService;
        this.bookingEmitterWrapper = bookingEmitterWrapper;
    }

    @Transactional
    @CacheInvalidateAll(cacheName = "all-bookings")
    public Response createBooking(@NotNull CreateBookingRequestDTO dto, UUID customerId, String customerName) {

        if (dto.endDate().isBefore(dto.startDate()))
            throw new IllegalEndDateException("A data de término não pode ser anterior a de início", ErrorCode.INVALID_END_DATE);

        //VehicleAPIClient.Vehicle vehicle = vehicleAPIClient.findVehicleById(dto.vehicleId());
        //atualmente, VehicleAPIClient não está sendo usado para criar booking

        VehicleStatus vehicleStatus = vehicleStatusDAO.findById(dto.vehicleId());

        if (vehicleStatus == null) {
            throw new VehicleException("O veículo não existe!", ErrorCode.NULL_VEHICLE);
        }

        if (vehicleStatus.getStatus().equals("UNDER_MAINTENANCE")) {
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
    public List<ResponseBookingDTO> getBookingRawList(@CacheKey UUID customerId, @CacheKey int page, @CacheKey int size, @CacheKey boolean isAdmin) {

        List<BookingModel> bookings = isAdmin
                ? bookingDAO.findAll().page(Page.of(page, size)).list()
                : bookingDAO.findByCustomerId(customerId, page, size);

        return bookingMapper.toResponseDTOList(bookings);
    }

    public Response getAllBookings(UUID customerId, int page, int size, boolean isAdmin) {
        List<ResponseBookingDTO> bookingListRaw = getBookingRawList(customerId, page, size, isAdmin);

        if (bookingListRaw.isEmpty()) {
            String message = isAdmin
                    ? "A lista de agendamentos está vazia."
                    : "Você não possui nenhum agendamento ainda.";

            Map<String, String> response = Map.of("mensagem", message);
            return Response.ok(response).build();
        }

        return Response.ok(bookingListRaw).build();
    }

    public Response findById(UUID bookingId) {
        BookingModel bookingModelFound = bookingDAO.findById(bookingId);

        if (bookingModelFound == null) {
            return Response.status(404).build();
        }

        ResponseBookingDTO dto = bookingMapper.toResponseDTO(bookingModelFound);

        return Response.ok(dto).build();
    }

    @CacheInvalidateAll(cacheName = "all-bookings")
    @Transactional
    public Response checkBooking(UUID bookingId, UpdateBookingStatusRequest dto) {
        BookingModel bookingModel = bookingDAO.findById(bookingId);

        if (bookingModel == null) throw new BookingNotFoundException("Erro ao editar agendamento.", ErrorCode.NULL_BOOKING);

        try {

            bookingModel.setStatus(dto.newStatus());
            emitStatusChange(bookingModel);

        } catch (RuntimeException e) {

            e.printStackTrace();

            Log.info("ENTROU NO CATCH");

            return Response.status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }

        return Response.noContent().build();
    }

    private void emitStatusChange(BookingModel booking) {
        switch (booking.getStatus()) {
            case ACTIVATED -> bookingEmitterWrapper.sendActivated(booking);
            case FINISHED -> bookingEmitterWrapper.sendFinished(booking);
            case CANCELED -> bookingEmitterWrapper.sendCanceled(booking);
        }
    }

    @Transactional
    @CacheInvalidateAll(cacheName = "all-bookings")
    public void checkVehicle(VehicleProducerDTO dto) {

        UUID vehicleId = dto.vehicleId();

        VehicleStatus vehicleStatus = vehicleStatusDAO.findById(vehicleId);

        if (vehicleStatus != null) {
            vehicleStatusService.changeVehicleStatus(dto);
        }

        BookingModel possibleBooking = bookingDAO.findByVehicleId(vehicleId);

        if (possibleBooking == null) return;

        possibleBooking.setStatus(BookingStatusEnum.CANCELED);

        smsService.sendCancellationNotice("+5574999254283");
    }
}
