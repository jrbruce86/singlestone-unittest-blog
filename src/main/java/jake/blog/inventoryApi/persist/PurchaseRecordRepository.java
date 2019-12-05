package jake.blog.inventoryApi.persist;

import jake.blog.inventoryApi.model.db.PurchaseRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseRecordRepository  extends CrudRepository<PurchaseRecord, Long> {
    List<PurchaseRecord> findAllByCustomerID(final String customerID);
}

