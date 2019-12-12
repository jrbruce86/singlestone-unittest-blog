package jake.blog.inventoryApi.mappers;

import jake.blog.inventoryApi.model.db.PurchaseRecord;
import jake.blog.inventoryApi.model.dto.InboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.OutboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.StoreItemDTO;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import jake.blog.inventoryApi.testUtils.DefaultStoreValues;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Log4j2
class DtoMapperTest {

    private StoreItemRepository storeItemRepository;

    private DtoMapper objectUnderTest;

    @BeforeEach
    void setup() {
        storeItemRepository = Mockito.mock(StoreItemRepository.class);
        objectUnderTest = new DtoMapper(storeItemRepository);

        DefaultStoreValues.initialize(storeItemRepository);
    }

    @Test
    void toPurchaseRecordSuccess() {
        /**
         * Setup
         */
        final InboundPurchaseRecordDTO inboundPurchaseRecordDTO = DefaultStoreValues.defaultInboundCustomerPurchase;
        final PurchaseRecord expectedResult = DefaultStoreValues.defaultCustomerPurchaseRecord.setCreatedDate(null).setPurchaseID(null);

        /**
         * Exercise
         */
        final PurchaseRecord actualResult = objectUnderTest.toPurchaseRecord(inboundPurchaseRecordDTO);

        /**
         * Verify
         */
        Assertions.assertEquals(expectedResult, actualResult);
    }

    @Test
    void toPurchaseRecordFailStoreItemNotFound() {
        /**
         * Setup
         */
        // augment the default purchase with a rogue store item id with no matching item in the repository
        final long randomStoreItemID = 100;
        final Set<Long> purchasedItemIDs = DefaultStoreValues.defaultCustomerPurchaseItemIDs;
        purchasedItemIDs.add(randomStoreItemID);
        Mockito.when(storeItemRepository.findById(randomStoreItemID)).thenReturn(Optional.empty());
        final InboundPurchaseRecordDTO inboundPurchaseRecordDTO = new InboundPurchaseRecordDTO()
                .setCustomerID(DefaultStoreValues.defaultCustomerID1)
                .setPurchasedItems(purchasedItemIDs);

        /**
         * Exercise
         */
        try {
            final PurchaseRecord actualResult = objectUnderTest.toPurchaseRecord(inboundPurchaseRecordDTO);
        } catch(final NoSuchElementException e) {
            /**
             * Verify
             */
            log.error("Exception: ", e);
            Assertions.assertTrue(true, "Exception occurred as expected when item not found in store repository");
            return;
        }
        /**
         * Verify
         */
        Assertions.assertTrue(false, "Exception did not occur as expected when item not found in store repository");
    }

    @Test
    void toOutboundPurchaseRecordDtoSuccess() {
        /**
         * Setup
         */
        final PurchaseRecord purchaseRecord = DefaultStoreValues.defaultCustomerPurchaseRecord;
        final Float totalCost = DefaultStoreValues.defaultCustomerPurchaseTotalCost;
        final OutboundPurchaseRecordDTO expectedResult = new OutboundPurchaseRecordDTO()
                .setCustomerID(DefaultStoreValues.defaultCustomer.getCustomerID())
                .setPurchaseDate(DefaultStoreValues.defaultCustomerPurchaseRecord.getCreatedDate())
                .setPurchaseID(DefaultStoreValues.defaultCustomerPurchaseRecordIDString)
                .setPurchaseTotalCost(DefaultStoreValues.defaultCustomerPurchaseTotalCostString)
                .setStoreItems(DefaultStoreValues.defaultCustomerPurchaseStoreItemDTOs);

        /**
         * Exercise
         */
        final OutboundPurchaseRecordDTO actualResult = objectUnderTest.toOutboundPurchaseRecordDTO(purchaseRecord, totalCost);

        /**
         * Verify
         */
        Assertions.assertEquals(expectedResult, actualResult);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID1);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID2);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID3);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID4);
    }

    @Test
    void toOutboundPurchaseRecordDtoMissedItemSuccess() {
        /**
         * Setup
         */
        // Set up a purchase record with a purchased item id that doesn't map to a store item
        final PurchaseRecord purchaseRecord = DefaultStoreValues.defaultCustomerPurchaseRecord;
        final Set<Long> purchaseItemIDs = DefaultStoreValues.defaultCustomerPurchaseItemIDs;
        purchaseItemIDs.add(9999L);
        Mockito.when(storeItemRepository.findById(9999L)).thenReturn(Optional.empty());
        final Float totalCost = DefaultStoreValues.defaultCustomerPurchaseTotalCost;
        final Set<StoreItemDTO> storeItemDTOS = DefaultStoreValues.defaultCustomerPurchaseStoreItemDTOs;
        final OutboundPurchaseRecordDTO expectedResult = new OutboundPurchaseRecordDTO()
                .setCustomerID(DefaultStoreValues.defaultCustomer.getCustomerID())
                .setPurchaseDate(DefaultStoreValues.defaultCustomerPurchaseRecord.getCreatedDate())
                .setPurchaseID(DefaultStoreValues.defaultCustomerPurchaseRecordIDString)
                .setPurchaseTotalCost(DefaultStoreValues.defaultCustomerPurchaseTotalCostString)
                .setStoreItems(DefaultStoreValues.defaultCustomerPurchaseStoreItemDTOs);

        /**
         * Exercise
         */
        final OutboundPurchaseRecordDTO actualResult = objectUnderTest.toOutboundPurchaseRecordDTO(purchaseRecord, totalCost);

        /**
         * Verify
         */
        Assertions.assertEquals(expectedResult, actualResult);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID1);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID2);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID3);
        Mockito.verify(storeItemRepository).findById(DefaultStoreValues.defaultCustomerPurchaseItemID4);
    }

}