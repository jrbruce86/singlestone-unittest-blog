package jake.blog.inventoryApi.model.db;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Embeddable
@Data
@Accessors(chain = true)
@Entity
@Table(name = "Customer")
public class Customer {
    @Id
    private String customerID;
}
