package jake.blog.inventoryApi.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class InboundPurchaseRecordDTO {
    private String customerID;
    private Set<Long> purchasedItems;
}
