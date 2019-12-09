package jake.blog.inventoryApi.controllers;

import jake.blog.inventoryApi.mappers.DtoMapper;
import jake.blog.inventoryApi.model.db.Customer;
import jake.blog.inventoryApi.model.db.PurchaseRecord;
import jake.blog.inventoryApi.model.db.StoreItem;
import jake.blog.inventoryApi.model.dto.InboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.OutboundPurchaseRecordDTO;
import jake.blog.inventoryApi.persist.CustomerRepository;
import jake.blog.inventoryApi.persist.PurchaseRecordRepository;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.*;

@Log4j2
class StoreInventoryControllerTest {

    // dependencies
    private PurchaseRecordRepository purchaseRecordRepository;
    private StoreItemRepository storeItemRepository;
    private CustomerRepository customerRepository;
    private DtoMapper dtoMapper;

    // object being tested
    private StoreInventoryController objectUnderTest;

    // default mocks
    private Customer defaultCustomer;
    private String defaultCustomerID1;
    private String defaultCustomerID2;
    private String defaultCustomerID3;
    private String defaultCustomerID4;
    private String defaultCustomerID5;
    private InboundPurchaseRecordDTO defaultInboundCustomerPurchase;
    private Long defaultCustomerPurchaseRecordID;
    private Date defaultCustomerPurchaseRecordCreatedDate;
    private PurchaseRecord defaultCustomerPurchaseRecord;
    private Set<Long> defaultCustomerPurchaseItemIDs;
    private Long defaultCustomerPurchaseItemID1 = 1L;
    private StoreItem defaultCustomerPurchaseItem1 = createStoreItem(defaultCustomerPurchaseItemID1, "toothpaste", 5.37f);
    private Long defaultCustomerPurchaseItemID2 = 2L;
    private StoreItem defaultCustomerPurchaseItem2 = createStoreItem(defaultCustomerPurchaseItemID2, "TV", 500.46f);
    private Long defaultCustomerPurchaseItemID3 = 3L;
    private StoreItem defaultCustomerPurchaseItem3 = createStoreItem(defaultCustomerPurchaseItemID2, "Potato Chips", 1.11f);
    private Long defaultCustomerPurchaseItemID4 = 4L;
    private StoreItem defaultCustomerPurchaseItem4 = createStoreItem(defaultCustomerPurchaseItemID2, "Sneakers", 51.21f);
    private Float defaultCustomerPurchaseTotalCost = 558.15f; // the sum of the above
    private List<Customer> defaultListOfCustomers;

    @BeforeEach
    public void setup() {
        purchaseRecordRepository = Mockito.mock(PurchaseRecordRepository.class);
        storeItemRepository = Mockito.mock(StoreItemRepository.class);
        customerRepository = Mockito.mock(CustomerRepository.class);
        dtoMapper = Mockito.mock(DtoMapper.class);

        objectUnderTest = new StoreInventoryController(purchaseRecordRepository,
                storeItemRepository, customerRepository, dtoMapper);

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
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID1)).thenReturn(Optional.of(defaultCustomerPurchaseItem1));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID2)).thenReturn(Optional.of(defaultCustomerPurchaseItem2));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID3)).thenReturn(Optional.of(defaultCustomerPurchaseItem3));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID4)).thenReturn(Optional.of(defaultCustomerPurchaseItem4));
        defaultInboundCustomerPurchase = new InboundPurchaseRecordDTO()
                .setCustomerID(defaultCustomerID1)
                .setPurchasedItems(defaultCustomerPurchaseItemIDs);
        defaultCustomerPurchaseRecordID = 123L;
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

    @Test
    public void purchaseExistingCustomerSuccessful() {
        /**
         * Setup
         */
        // stub the customer lookup to simulate existing customer
        Mockito.when(customerRepository.findById(defaultCustomerID1)).thenReturn(Optional.of(defaultCustomer));

        // stub the dto mapper operation to return the expected result
        final PurchaseRecord expectedResult = Mockito.mock(PurchaseRecord.class);
        Mockito.when(dtoMapper.toPurchaseRecord(defaultInboundCustomerPurchase)).thenReturn(expectedResult);

        // stub the save operation to save successfully returning the saved result
        Mockito.when(purchaseRecordRepository.save(expectedResult)).thenReturn(expectedResult);

        /**
         * Exercise
         */
        final PurchaseRecord actualResult = objectUnderTest.purchase(defaultInboundCustomerPurchase);

        /**
         * Verify
         */
        Assertions.assertTrue(expectedResult == actualResult, "The returned result does not match the expected result");
        Mockito.verify(purchaseRecordRepository, Mockito.times(1)).save(expectedResult);
        Mockito.verify(customerRepository, Mockito.times(1)).findById(defaultCustomerID1);
        Mockito.verify(dtoMapper, Mockito.times(1)).toPurchaseRecord(defaultInboundCustomerPurchase);
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void purchaseNonExistingCustomerSuccessful() {
        /**
         * Setup
         */
        // stub the customer lookup to simulate non existing customer
        Mockito.when(customerRepository.findById(defaultCustomerID1)).thenReturn(Optional.empty());

        // stub the dto mapper operation return the expect result
        final PurchaseRecord expectedResult = Mockito.mock(PurchaseRecord.class);
        Mockito.when(dtoMapper.toPurchaseRecord(defaultInboundCustomerPurchase)).thenReturn(expectedResult);

        // stub the save operation to save successfully returning saved result
        Mockito.when(purchaseRecordRepository.save(expectedResult)).thenReturn(expectedResult);

        /**
         * Exercise
         */
        final PurchaseRecord actualResult = objectUnderTest.purchase(defaultInboundCustomerPurchase);

        /**
         * Verify
         */
        Assertions.assertTrue(expectedResult == actualResult, "The returned result does not match the expected result");
        Mockito.verify(customerRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(customerRepository, Mockito.times(1)).findById(defaultCustomerID1);
        Mockito.verify(dtoMapper, Mockito.times(1)).toPurchaseRecord(defaultInboundCustomerPurchase);
        Mockito.verify(purchaseRecordRepository, Mockito.times(1)).save(expectedResult);
    }

    @Test
    public void getPurchaseSuccessful() {
        /**
         * Setup
         */
        final Long inputPurchaseID = 3L;
        // Stub the purchase lookup to succeed
        final PurchaseRecord foundRecord = Mockito.mock(PurchaseRecord.class);
        Mockito.when(purchaseRecordRepository.findById(inputPurchaseID)).thenReturn(Optional.of(foundRecord));

        // Stub the store bought items in the found purchase record
        Mockito.when(foundRecord.getPurchasedItemIDs()).thenReturn(defaultCustomerPurchaseItemIDs);
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID1)).thenReturn(Optional.of(defaultCustomerPurchaseItem1));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID2)).thenReturn(Optional.of(defaultCustomerPurchaseItem2));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID3)).thenReturn(Optional.of(defaultCustomerPurchaseItem3));
        Mockito.when(storeItemRepository.findById(defaultCustomerPurchaseItemID4)).thenReturn(Optional.of(defaultCustomerPurchaseItem4));
        final float expectedTotalCost = defaultCustomerPurchaseTotalCost;

        // Stub the dto mapper to successfully convert the database record to output format
        final OutboundPurchaseRecordDTO expectedResult = Mockito.mock(OutboundPurchaseRecordDTO.class);
        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(Mockito.any(PurchaseRecord.class), Mockito.anyFloat())).thenReturn(expectedResult);
        log.error("Expected found record: {}", foundRecord);

        /**
         * Exercise
         */
        final OutboundPurchaseRecordDTO actualResult = objectUnderTest.getPurchase(inputPurchaseID);
        log.error("Actual result: {}", actualResult);

        /**
         * Verify
         */
        Assertions.assertTrue(expectedResult == actualResult);
        ArgumentCaptor<PurchaseRecord> purchaseRecordArg = ArgumentCaptor.forClass(PurchaseRecord.class);
        ArgumentCaptor<Float> totalCostArg = ArgumentCaptor.forClass(Float.class);
        Mockito.verify(dtoMapper, Mockito.times(1)).toOutboundPurchaseRecordDTO(purchaseRecordArg.capture(), totalCostArg.capture());
        log.error("total cost calculated: {}", totalCostArg.getValue());
        Assertions.assertEquals((float)totalCostArg.getValue(), expectedTotalCost, "Total cost calculation error.");
    }

    @Test
    public void getPurchaseFailure() {
        /**
         * Setup
         */
        final Long inputPurchaseID = 3L;
        // Stub the purchase lookup to fail
        Mockito.when(purchaseRecordRepository.findById(inputPurchaseID)).thenReturn(Optional.empty());

        /**
         * Exercise
         */
        try {
            objectUnderTest.getPurchase(inputPurchaseID);
        } catch(final Exception e) {
            /**
             * Verify
             */
            Assertions.assertTrue(true, "The exception was thrown when the purchase could not be found as expected.");
            return;
        }
        /**
         * Verify
         */
        Assertions.assertFalse(true, "An exception expected to be thrown when the purchase lookup fails, but it was not.");
    }

    @Test
    public void customersLookupSuccess() {
        /**
         * Setup
         */
        Mockito.when(customerRepository.findAll()).thenReturn(defaultListOfCustomers);

        /**
         * Exercise
         */
        final List<String> actualResult = objectUnderTest.customers();


        /**
         * Verify
         */
        Assertions.assertEquals(actualResult.get(0), defaultCustomerID1);
        Assertions.assertEquals(actualResult.get(1), defaultCustomerID2);
        Assertions.assertEquals(actualResult.get(2), defaultCustomerID3);
        Assertions.assertEquals(actualResult.get(3), defaultCustomerID4);
        Assertions.assertEquals(actualResult.get(4), defaultCustomerID5);
        Assertions.assertTrue(actualResult.size() == defaultListOfCustomers.size());
        Assertions.assertTrue(defaultListOfCustomers.size() == 5);
    }

    @Test
    public void customerPurchasesLookupSuccess() {
        /**
         * Setup
         */
        // Stub the customer lookup to succeed
        final String customerID = defaultCustomerID1;
        final Customer customer = defaultCustomer;
        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.of(defaultCustomer));

        // Stub the purchase record lookup to return various purchase records
        final PurchaseRecord purchaseRecord1 = defaultCustomerPurchaseRecord;
        final Long[] purchase2ItemIDs =  {defaultCustomerPurchaseItemID3, defaultCustomerPurchaseItemID4};
        final PurchaseRecord purchaseRecord2 = createNewPurchaseRecord(321L, customerID, new Date(), purchase2ItemIDs);
        final Long[] purchase3ItemIDs = {defaultCustomerPurchaseItemID3, defaultCustomerPurchaseItemID1};
        final PurchaseRecord purchaseRecord3 = createNewPurchaseRecord(542L, customerID, new Date(), purchase3ItemIDs);
        final List<PurchaseRecord> purchaseRecordList = new ArrayList<>();
        purchaseRecordList.add(purchaseRecord3);// $6.48
        purchaseRecordList.add(purchaseRecord1); // total of $558.15
        purchaseRecordList.add(purchaseRecord2); // $52.32
        Mockito.when(purchaseRecordRepository.findAllByCustomerID(customerID)).thenReturn(purchaseRecordList);

        // Stub the dto mapper to return a separate mock for each input
        final OutboundPurchaseRecordDTO outboundPurchase1 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("558.15");
        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord1, 558.15f)).thenReturn(outboundPurchase1);
        final OutboundPurchaseRecordDTO outboundPurchase2 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("52.32");
        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord2, 52.32f)).thenReturn(outboundPurchase2);
        final OutboundPurchaseRecordDTO outboundPurchase3 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("6.48");
        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord3, 6.48f)).thenReturn(outboundPurchase3);

        /**
         * Exercise
         */
        final List<OutboundPurchaseRecordDTO> actualResult = objectUnderTest.customerPurchases(defaultCustomerID1);

        /**
         * Verify
         */
        // Make sure the correct arguments were passed into the mapper and that the results are in the correct order (sorted by cost decsending)
        final ArgumentCaptor<PurchaseRecord> purchaseRecordArgumentCaptor = ArgumentCaptor.forClass(PurchaseRecord.class);
        final ArgumentCaptor<Float> floatArgumentCaptor = ArgumentCaptor.forClass(Float.class);
        Mockito.verify(dtoMapper, Mockito.times(3)).toOutboundPurchaseRecordDTO(purchaseRecordArgumentCaptor.capture(), floatArgumentCaptor.capture());
        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(0).floatValue(), 6.48f);
        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(1).floatValue(), 558.15f);
        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(2).floatValue(), 52.32f);
        log.error("Actual result: {}", actualResult);
        Assertions.assertTrue(actualResult.get(0) == outboundPurchase1, String.format("Expected %s but got %s",outboundPurchase1, actualResult.get(0)));
        Assertions.assertTrue(actualResult.get(1) == outboundPurchase2);
        Assertions.assertTrue(actualResult.get(2) == outboundPurchase3);
    }

    @Test
    public void customerPurchaseLookupFailCustomerNotFound() {
        /**
         * Setup
         */
        // stub the customer lookup to simulate non existing customer
        final String customerID = defaultCustomerID1;
        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.empty());

        /**
         * Exercise
         */
        try {
            final List<OutboundPurchaseRecordDTO> actualResult = objectUnderTest.customerPurchases(customerID);
        } catch(final Exception e) {
            /**
             * Verify
             */
            Assertions.assertTrue(true, "Exception was thrown as expected when no customer present.");
            return;
        }
        /**
         * Verify
         */
        Assertions.assertFalse(true, "Exception was not thrown as expected when no customer present");
    }

    @Test
    void customerTotalSpent() {
        Assertions.assertTrue(true);
    }

    private StoreItem createStoreItem(final Long itemID, final String name, final float cost) {
        return new StoreItem().setItemID(itemID).setName(name).setCost(cost);
    }

    private PurchaseRecord createNewPurchaseRecord(final Long purchaseID, final String customerID, final Date createdDate, final Long[] itemIDs) {
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