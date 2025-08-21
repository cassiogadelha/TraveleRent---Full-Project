package verso.caixa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import verso.caixa.dto.CreateBookingRequestDTO;
import verso.caixa.dto.ResponseBookingDTO;
import verso.caixa.model.BookingModel;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface BookingMapper {
    BookingModel toEntity(CreateBookingRequestDTO dto);

    ResponseBookingDTO toResponseDTO(BookingModel booking);

    List<ResponseBookingDTO> toResponseDTOList(List<BookingModel> vehicles);
}