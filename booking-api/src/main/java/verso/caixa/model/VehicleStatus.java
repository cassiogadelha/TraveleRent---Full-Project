package verso.caixa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_vehicle_status")
public class VehicleStatus {

    @Id
    UUID vehicleId;

    String vehicleStatus;

    public VehicleStatus(UUID vehicleId, String vehicleStatus) {
        this.vehicleId = vehicleId;
        this.vehicleStatus = vehicleStatus;
    }
}
