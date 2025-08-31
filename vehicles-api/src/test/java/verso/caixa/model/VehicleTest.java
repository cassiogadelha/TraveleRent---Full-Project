package verso.caixa.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import verso.caixa.enums.VehicleStatusEnum;

public class VehicleTest {
    private Long id;
    private String model;
    private VehicleStatusEnum status;
    private int year;
    private String engine;

    /**
     * 1. Quando um vehicle for criado ele deve possuir um ID válido
     * 2. Quando um vehicle for criado ele deve ser criado com o status o AVAILABLE
     * 3. Quando o Vehicle AVAILABLE ele só pode ir pra RENTED ou UNDER_MAINTENANCE
     * 4. Quando eu criar um Vehicle ele não pode ter model, year, engine (vazios ou nulos)
     * 5. (opcional) o ano deve ser atual (2025 <= year)
     */

    @Test
    void shouldCreateVehicleWithValidParameters() {
        VehicleModel vehicle = new VehicleModel("Chevrolet", "Onix", 2022, "1.0 Turbo");
        Assertions.assertEquals("Chevrolet", vehicle.getBrand());
        Assertions.assertEquals("Onix", vehicle.getModel());
        Assertions.assertEquals(2022, vehicle.getYear());
        Assertions.assertEquals("1.0 Turbo", vehicle.getEngine());
        Assertions.assertEquals(VehicleStatusEnum.AVAILABLE, vehicle.getStatus());
    }

    @Test
    void shouldAddMaintenanceAndChangeStatus() {
        VehicleModel vehicle = new VehicleModel("Renault", "Sandero", 2019, "1.6");
        MaintenanceModel maintenance = new MaintenanceModel();

        vehicle.moveForMaintenance(maintenance);

        Assertions.assertEquals(VehicleStatusEnum.UNDER_MAINTENANCE, vehicle.getStatus());
        Assertions.assertTrue(vehicle.getMaintenances().contains(maintenance));
        Assertions.assertEquals(vehicle, maintenance.getVehicleModel());
    }

    @Test
    void shouldAddAccessory() {
        VehicleModel vehicle = new VehicleModel("Honda", "Civic", 2023, "2.0");
        AccessoryModel accessory = new AccessoryModel();
        vehicle.addAccessory(accessory);

        Assertions.assertTrue(vehicle.getAccessories().contains(accessory));
        Assertions.assertTrue(accessory.getVehicles().contains(vehicle));
    }
}