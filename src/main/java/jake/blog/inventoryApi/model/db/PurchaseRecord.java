package jake.blog.inventoryApi.model.db;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;

@Entity
@Table(name = "Purchases")
@Data
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class PurchaseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long purchaseID;

    private String customerID; // Links to a CustomerReporsitory record

    private HashSet<Long> purchasedItemIDs; // Links to a set of StoreItemRepository records

    @CreatedDate
    private Date createdDate;
}
