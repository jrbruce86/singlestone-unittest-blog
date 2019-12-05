package jake.blog.inventoryApi.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StoreItemDTO {
    private String name;
    private String cost;
}
