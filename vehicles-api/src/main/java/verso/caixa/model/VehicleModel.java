package verso.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import verso.caixa.enums.VehicleStatusEnum;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "tb_vehicle")
public class VehicleModel extends PanacheEntityBase{

    private static final Map<VehicleStatusEnum, Set<VehicleStatusEnum>> VEHICLE_STATE_MACHINE = new HashMap<>() {
    };

    static {
        VEHICLE_STATE_MACHINE.put(VehicleStatusEnum.AVAILABLE, Set.of(VehicleStatusEnum.RENTED, VehicleStatusEnum.UNDER_MAINTENANCE));
        VEHICLE_STATE_MACHINE.put(VehicleStatusEnum.RENTED, Set.of(VehicleStatusEnum.AVAILABLE, VehicleStatusEnum.UNDER_MAINTENANCE));
        VEHICLE_STATE_MACHINE.put(VehicleStatusEnum.UNDER_MAINTENANCE, Set.of(VehicleStatusEnum.AVAILABLE));
    }

    /*==================================== RELATIONSHIPS ===========================================*/
    @OneToMany(mappedBy = "vehicleModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MaintenanceModel> maintenances = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "vehicle_accessory",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            /*Especifica que a tabela tb_maintenance terá uma coluna vehicle_id apontando para o veículo correspondente.
            Ou seja, é ali que o banco sabe qual manutenção pertence a qual carro.*/

            inverseJoinColumns = @JoinColumn(name = "accessory_id"))
            /*na tabela de junção "vehicle_accessory":
                       - A coluna "accessory_id" representa a chave estrangeira que aponta para a tabela AccessoryModel
                       - Ou seja, ela vincula cada veículo aos acessórios correspondentes*/

    private final Set<AccessoryModel> accessories = new HashSet<>();
    /*------------------------------------------------------------------*/

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID vehicleId;

    @Column(name = "car_title")
    private String carTitle;

    private String brand;

    @Column(name = "vehicle_year")
    private Integer year;

    private String engine;

    @Enumerated(EnumType.STRING)
    private VehicleStatusEnum status = VehicleStatusEnum.AVAILABLE;

    private String model;

    protected VehicleModel() {}

    public VehicleModel(String brand, String model, int year, String engine) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.engine = engine;
    }

    public boolean isRented() {
        return this.getStatus().equals(VehicleStatusEnum.RENTED);
    }

    public void setStatus(VehicleStatusEnum incomingStatus) {
        Set<VehicleStatusEnum> possibleStatus = VEHICLE_STATE_MACHINE.get(this.status);

        if (incomingStatus.equals(this.status)) {
            return;
        }

        if (possibleStatus.contains(incomingStatus)) {
            this.status = incomingStatus;
        } else {
            throw new IllegalArgumentException("Validation error, possible status are: " + possibleStatus);
        }
    }

    public void moveForMaintenance(MaintenanceModel maintenanceModel) {
        this.setStatus(VehicleStatusEnum.UNDER_MAINTENANCE);
        maintenanceModel.setVehicle(this);
        this.maintenances.add(maintenanceModel);
    }

    public void addAccessory(AccessoryModel accessoryModel) {
        this.accessories.add(accessoryModel);
        accessoryModel.addVehicle(this);
    }

}

