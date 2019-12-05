package jake.blog.inventoryApi.model.db;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "StoreItems")
@Embeddable
@Data
@Accessors(chain = true)
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long itemID;
    private String name;
    private Float cost;
}
