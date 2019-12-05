package jake.blog.inventoryApi.service;

import jake.blog.inventoryApi.model.db.StoreItem;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ItemSeedingService {

    private final StoreItemRepository storeItemRepository;

    public ItemSeedingService(final StoreItemRepository storeItemRepository) {
        this.storeItemRepository = storeItemRepository;
    }

    @PostConstruct
    public void seed() {
        storeItemRepository.save(new StoreItem().setName("Coke").setCost(2.50f));
        storeItemRepository.save(new StoreItem().setName("Soap").setCost(5.20f));
        storeItemRepository.save(new StoreItem().setName("TV").setCost(255f));
        storeItemRepository.save(new StoreItem().setName("Toothbrush").setCost(5.25f));
        storeItemRepository.save(new StoreItem().setName("Computer").setCost(545.30f));
        storeItemRepository.save(new StoreItem().setName("Video game").setCost(35f));
        storeItemRepository.save(new StoreItem().setName("Soda").setCost(1.50f));
        storeItemRepository.save(new StoreItem().setName("Lochness Monsta (tree fiddy)").setCost(3.50f));
    }

}
