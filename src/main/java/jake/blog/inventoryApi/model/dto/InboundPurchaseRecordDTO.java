package jake.blog.inventoryApi.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class InboundPurchaseRecordDTO {
    private String customerID;
    private Set<Long> purchasedItems;
}
