package verso.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Parameters;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tb_maintenance")
@NoArgsConstructor
public class MaintenanceModel extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID maintenanceId;

    @Column(name = "created_at")
    Instant createdAt;
    @Column(name = "reason")
    String problemDescription;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private VehicleModel vehicleModel;

    public MaintenanceModel(String problemDescription, VehicleModel vehicleModel) {
        this.createdAt = Instant.now();
        this.problemDescription = problemDescription;
        this.vehicleModel = vehicleModel;
    }

    public void setVehicle(VehicleModel vehicle) {
        this.vehicleModel = vehicle;
    }

    public static MaintenanceModel findByVehicleAndMaintenanceId(UUID vehicleId, UUID maintenanceId) {
        return MaintenanceModel.find(
                //consulta personalizada na base de dados usando a API do Panache, buscando uma instância da entidade MaintenanceModel
                "vehicleModel.vehicleId = :vehicleId AND maintenanceId = :maintenanceId",
                Parameters.with("vehicleId", vehicleId).and("maintenanceId", maintenanceId)
        ).firstResult();

        /*
        - MaintenanceModel.find(...): faz uma consulta JPQL na tabela vinculada à entidade MaintenanceModel
        - "vehicleId = :vehicleId AND id = :maintenanceId": é a cláusula WHERE da consulta
        - Parameters.with(...).and(...): define os valores dos parâmetros :vehicleId e :maintenanceId
        - .firstResult(): retorna o primeiro resultado encontrado — ou null se não houver nenhum

         */

        /*
        SELECT m
        FROM MaintenanceModel m
        WHERE m.vehicleId = :vehicleId AND m.id = :maintenanceId
         */
    }

}
