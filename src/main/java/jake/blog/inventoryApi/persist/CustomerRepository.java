package jake.blog.inventoryApi.persist;

import jake.blog.inventoryApi.model.db.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, String> {
}
