package verso.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_acessory")
@Getter
@Setter
@NoArgsConstructor
public class AccessoryModel extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID accessoryId;

    @Column(name = "name")
    String name;

    @ManyToMany(mappedBy = "accessories")
    private Set<VehicleModel> vehicles = new HashSet<>();

    public AccessoryModel(String name) {
        this.name = name;
    }

    public void addVehicle(VehicleModel vehicleModel) {
        this.vehicles.add(vehicleModel);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccessoryModel && this.name.equals(((AccessoryModel) obj).name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "id=" + accessoryId +
                ", name='" + name + '\'' +
                '}';
    }
}
