package jake.blog.inventoryApi.mappers;

import jake.blog.inventoryApi.model.db.PurchaseRecord;
import jake.blog.inventoryApi.model.db.StoreItem;
import jake.blog.inventoryApi.model.dto.InboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.OutboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.StoreItemDTO;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import javassist.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class DtoMapper {

    private final StoreItemRepository storeItemRepository;

    DtoMapper(final StoreItemRepository storeItemRepository) {
        this.storeItemRepository = storeItemRepository;
    }

    public PurchaseRecord toPurchaseRecord(final InboundPurchaseRecordDTO purchaseRecordDTO) {
        // 1. Verify that the purchased items are valid and if so map them to a list
        final Set<Long> itemIDs = new HashSet<>();
        final List<Long> failureList = new ArrayList<>();
        for (Long itemID : purchaseRecordDTO.getPurchasedItems()) {
            if(storeItemRepository.findById(itemID).isEmpty()) {
                failureList.add(itemID);
            } else {
                itemIDs.add(itemID);
            }
        }
        if(failureList.size() > 0) {
            throw new NoSuchElementException(String.format("The following items could not be found so the purchase is being aborted. %s", failureList));
        }

        // 2. Return the purchase record
        return new PurchaseRecord().setCustomerID(purchaseRecordDTO.getCustomerID())
                .setPurchasedItemIDs(itemIDs);
    }

    public OutboundPurchaseRecordDTO toOutboundPurchaseRecordDTO(final PurchaseRecord purchaseRecord, final Float totalCost) {
        final Set<StoreItemDTO> storeItemDTOS = new HashSet<>();
        for(final Long itemID : purchaseRecord.getPurchasedItemIDs()) {
            final Optional<StoreItem> storeItem = storeItemRepository.findById(itemID);
            if(storeItem.isEmpty()) {
                log.error("The item with id {} could not be found!!!", itemID);
                continue;
            }
            storeItemDTOS.add(toStoreItemDTO(storeItem.get()));
        }
        return new OutboundPurchaseRecordDTO()
                .setCustomerID(purchaseRecord.getCustomerID())
                .setStoreItems(storeItemDTOS)
                .setPurchaseDate(purchaseRecord.getCreatedDate())
                .setPurchaseID(purchaseRecord.getPurchaseID().toString())
                .setPurchaseTotalCost(String.format("$%s", String.valueOf(totalCost)));
    }

    public StoreItemDTO toStoreItemDTO(final StoreItem storeItem) {
        return new StoreItemDTO()
                .setCost(String.format("$%s", storeItem.getCost().toString()))
                .setName(storeItem.getName());
    }
}
