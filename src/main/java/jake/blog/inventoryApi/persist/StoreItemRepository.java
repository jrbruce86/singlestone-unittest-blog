package jake.blog.inventoryApi.persist;

import jake.blog.inventoryApi.model.db.StoreItem;
import org.springframework.data.repository.CrudRepository;

public interface StoreItemRepository extends CrudRepository<StoreItem, Long> {
}
