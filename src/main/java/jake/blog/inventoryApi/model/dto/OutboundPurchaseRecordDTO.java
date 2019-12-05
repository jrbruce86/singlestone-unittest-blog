package jake.blog.inventoryApi.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Set;

@Data
@Accessors(chain = true)
public class OutboundPurchaseRecordDTO {
    private String customerID;
    private Set<StoreItemDTO> storeItems;
    private Date purchaseDate;
    private String purchaseID;
    private String purchaseTotalCost;
}
