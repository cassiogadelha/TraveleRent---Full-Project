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
    UUID id;

    String status;

    public VehicleStatus(UUID id, String status) {
        this.id = id;
        this.status = status;
    }
}
