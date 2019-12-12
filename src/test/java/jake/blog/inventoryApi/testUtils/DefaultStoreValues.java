package jake.blog.inventoryApi.testUtils;

import jake.blog.inventoryApi.model.db.Customer;
import jake.blog.inventoryApi.model.db.PurchaseRecord;
import jake.blog.inventoryApi.model.db.StoreItem;
import jake.blog.inventoryApi.model.dto.InboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.StoreItemDTO;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import org.mockito.Mockito;

import java.util.*;

public class DefaultStoreValues {
    public static Customer defaultCustomer;
    public static String defaultCustomerID1;
    public static String defaultCustomerID2;
    public static String defaultCustomerID3;
    public static String defaultCustomerID4;
    public static String defaultCustomerID5;
    public static InboundPurchaseRecordDTO defaultInboundCustomerPurchase;
    public static Long defaultCustomerPurchaseRecordID = 123L;
    public static String defaultCustomerPurchaseRecordIDString = "123";
    public static Date defaultCustomerPurchaseRecordCreatedDate;
    public static PurchaseRecord defaultCustomerPurchaseRecord;
    public static Set<Long> defaultCustomerPurchaseItemIDs;
    public static Set<StoreItemDTO> defaultCustomerPurchaseStoreItemDTOs;
    public static Long defaultCustomerPurchaseItemID1 = 1L;
    public static StoreItem defaultCustomerPurchaseItem1 = createStoreItem(defaultCustomerPurchaseItemID1, "toothpaste", 5.37f);
    public static StoreItemDTO defaultCustomerPurchaseStoreItemDTO1 = new StoreItemDTO().setName("toothpaste").setCost("$5.37");
    public static Long defaultCustomerPurchaseItemID2 = 2L;
    public static StoreItem defaultCustomerPurchaseItem2 = createStoreItem(defaultCustomerPurchaseItemID2, "TV", 500.46f);
    public static StoreItemDTO defaultCustomerPurchaseStoreItemDTO2 = new StoreItemDTO().setName("TV").setCost("$500.46");
    public static Long defaultCustomerPurchaseItemID3 = 3L;
    public static StoreItem defaultCustomerPurchaseItem3 = createStoreItem(defaultCustomerPurchaseItemID2, "Potato Chips", 1.11f);
    public static StoreItemDTO defaultCustomerPurchaseStoreItemDTO3 = new StoreItemDTO().setName("Potato Chips").setCost("$1.11");
    public static Long defaultCustomerPurchaseItemID4 = 4L;
    public static StoreItem defaultCustomerPurchaseItem4 = createStoreItem(defaultCustomerPurchaseItemID2, "Sneakers", 51.21f);
    public static StoreItemDTO defaultCustomerPurchaseStoreItemDTO4 = new StoreItemDTO().setName("Sneakers").setCost("$51.21");
    public static Float defaultCustomerPurchaseTotalCost = 558.15f; // the sum of the above
    public static String defaultCustomerPurchaseTotalCostString = "$558.15";
    public static List<Customer> defaultListOfCustomers;

    public static void initialize(final StoreItemRepository storeItemRepository) {
        defaultCustomerID1 = "Bilbo Baggins";
        defaultCustomerID2 = "Jerry Seinfeld";
        defaultCustomerID3 = "Newman";
        defaultCustomerID4 = "Kramer";
        defaultCustomerID5 = "Gandalf";
        defaultCustomer = new Customer().setCustomerID(defaultCustomerID1);
        defaultCustomerPurchaseItemIDs = new HashSet<>();
        defaultCustomerPurchaseItemIDs.add(defaultCustomerPurchaseItemID1);
        defaultCustomerPurchaseItemIDs.add(defaultCustomerPurchaseItemID2);
        defaultCustomerPurchaseItemIDs.add(defaultCustomerPurchaseItemID3);
        defaultCustomerPurchaseItemIDs.add(defaultCustomerPurchaseItemID4);
        defaultCustomerPurchaseStoreItemDTOs = new HashSet<>();
        defaultCustomerPurchaseStoreItemDTOs.add(defaultCustomerPurchaseStoreItemDTO1);
        defaultCustomerPurchaseStoreItemDTOs.add(defaultCustomerPurchaseStoreItemDTO2);
        defaultCustomerPurchaseStoreItemDTOs.add(defaultCustomerPurchaseStoreItemDTO3);
        defaultCustomerPurchaseStoreItemDTOs.add(defaultCustomerPurchaseStoreItemDTO4);
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID1)).thenReturn(Optional.of(defaultCustomerPurchaseItem1));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID2)).thenReturn(Optional.of(defaultCustomerPurchaseItem2));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID3)).thenReturn(Optional.of(defaultCustomerPurchaseItem3));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID4)).thenReturn(Optional.of(defaultCustomerPurchaseItem4));
        defaultInboundCustomerPurchase = new InboundPurchaseRecordDTO()
                .setCustomerID(defaultCustomerID1)
                .setPurchasedItems(defaultCustomerPurchaseItemIDs);
        defaultCustomerPurchaseRecordCreatedDate = new Date();
        defaultCustomerPurchaseRecord = new PurchaseRecord().setCustomerID(defaultCustomerID1)
                .setPurchaseID(defaultCustomerPurchaseRecordID)
                .setCreatedDate(defaultCustomerPurchaseRecordCreatedDate)
                .setPurchasedItemIDs(defaultCustomerPurchaseItemIDs);
        defaultListOfCustomers = new ArrayList<>();
        defaultListOfCustomers.add(new Customer().setCustomerID(defaultCustomerID1));
        defaultListOfCustomers.add(new Customer().setCustomerID(defaultCustomerID2));
        defaultListOfCustomers.add(new Customer().setCustomerID(defaultCustomerID3));
        defaultListOfCustomers.add(new Customer().setCustomerID(defaultCustomerID4));
        defaultListOfCustomers.add(new Customer().setCustomerID(defaultCustomerID5));
    }

    public static StoreItem createStoreItem(final Long itemID, final String name, final float cost) {
        return new StoreItem().setItemID(itemID).setName(name).setCost(cost);
    }

    public static PurchaseRecord createNewPurchaseRecord(final Long purchaseID, final String customerID, final Date createdDate, final Long[] itemIDs) {
        final Set<Long> purchaseItemIDs = new HashSet();
        for(int i = 0; i < itemIDs.length; ++i) {
            purchaseItemIDs.add(itemIDs[i]);
        }
        return new PurchaseRecord()
                .setPurchaseID(purchaseID)
                .setCreatedDate(createdDate)
                .setCustomerID(customerID)
                .setPurchasedItemIDs(purchaseItemIDs);
    }
}
