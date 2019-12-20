package jake.blog.inventoryApi.controllers;

import jake.blog.inventoryApi.mappers.DtoMapper;
import jake.blog.inventoryApi.model.db.Customer;
import jake.blog.inventoryApi.model.db.PurchaseRecord;
import jake.blog.inventoryApi.model.dto.OutboundPurchaseRecordDTO;
import jake.blog.inventoryApi.persist.CustomerRepository;
import jake.blog.inventoryApi.persist.PurchaseRecordRepository;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import jake.blog.inventoryApi.testUtils.DefaultStoreValues;
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

    @BeforeEach
    public void setup() {
        purchaseRecordRepository = Mockito.mock(PurchaseRecordRepository.class);
        storeItemRepository = Mockito.mock(StoreItemRepository.class);
        customerRepository = Mockito.mock(CustomerRepository.class);
        dtoMapper = Mockito.mock(DtoMapper.class);

        objectUnderTest = new StoreInventoryController(purchaseRecordRepository,
                storeItemRepository, customerRepository, dtoMapper);

        DefaultStoreValues.initialize(storeItemRepository);
    }

    @Test
    public void purchaseExistingCustomerSuccessful() {
        /**
         * Setup
         */
        // stub the customer lookup to simulate existing customer
        Mockito.when(customerRepository.findById(DefaultStoreValues.defaultCustomerID1)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomer));

        // stub the dto mapper operation to return the expected result
        final PurchaseRecord expectedResult = Mockito.mock(PurchaseRecord.class);
        Mockito.when(dtoMapper.toPurchaseRecord(DefaultStoreValues.defaultInboundCustomerPurchase)).thenReturn(expectedResult);

        // stub the save operation to save successfully returning the saved result
        Mockito.when(purchaseRecordRepository.save(expectedResult)).thenReturn(expectedResult);

        /**
         * Exercise
         */
        final PurchaseRecord actualResult = objectUnderTest.purchase(DefaultStoreValues.defaultInboundCustomerPurchase);

        /**
         * Verify
         */
        Assertions.assertTrue(expectedResult == actualResult, "The returned result does not match the expected result");
        Mockito.verify(purchaseRecordRepository, Mockito.times(1)).save(expectedResult);
        Mockito.verify(customerRepository, Mockito.times(1)).findById(DefaultStoreValues.defaultCustomerID1);
        Mockito.verify(dtoMapper, Mockito.times(1)).toPurchaseRecord(DefaultStoreValues.defaultInboundCustomerPurchase);
        Mockito.verify(customerRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void purchaseNonExistingCustomerSuccessful() {
        /**
         * Setup
         */
        // stub the customer lookup to simulate non existing customer
        Mockito.when(customerRepository.findById(DefaultStoreValues.defaultCustomerID1)).thenReturn(Optional.empty());

        // stub the dto mapper operation return the expect result
        final PurchaseRecord expectedResult = Mockito.mock(PurchaseRecord.class);
        Mockito.when(dtoMapper.toPurchaseRecord(DefaultStoreValues.defaultInboundCustomerPurchase)).thenReturn(expectedResult);

        // stub the save operation to save successfully returning saved result
        Mockito.when(purchaseRecordRepository.save(expectedResult)).thenReturn(expectedResult);

        /**
         * Exercise
         */
        final PurchaseRecord actualResult = objectUnderTest.purchase(DefaultStoreValues.defaultInboundCustomerPurchase);

        /**
         * Verify
         */
        Assertions.assertTrue(expectedResult == actualResult, "The returned result does not match the expected result");
        final ArgumentCaptor<Customer> expectedSavedCustomer = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerRepository, Mockito.times(1)).save(expectedSavedCustomer.capture());
        Assertions.assertEquals(expectedSavedCustomer.getValue().getCustomerID(), DefaultStoreValues.defaultCustomerID1);
        Mockito.verify(customerRepository, Mockito.times(1)).findById(DefaultStoreValues.defaultCustomerID1);
        Mockito.verify(dtoMapper, Mockito.times(1)).toPurchaseRecord(DefaultStoreValues.defaultInboundCustomerPurchase);
        Mockito.verify(purchaseRecordRepository, Mockito.times(1)).save(expectedResult);
    }

//    @Test
//    public void getPurchaseSuccessful() {
//        /**
//         * Setup
//         */
//        final Long inputPurchaseID = 3L;
//        // Stub the purchase lookup to succeed
//        final PurchaseRecord foundRecord = Mockito.mock(PurchaseRecord.class);
//        Mockito.when(purchaseRecordRepository.findById(inputPurchaseID)).thenReturn(Optional.of(foundRecord));
//
//        // Stub the store bought items in the found purchase record
//        Mockito.when(foundRecord.getPurchasedItemIDs()).thenReturn(DefaultStoreValues.defaultCustomerPurchaseItemIDs);
//        Mockito.when(storeItemRepository.findById(DefaultStoreValues.defaultCustomerPurchaseItemID1)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomerPurchaseItem1));
//        Mockito.when(storeItemRepository.findById(DefaultStoreValues.defaultCustomerPurchaseItemID2)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomerPurchaseItem2));
//        Mockito.when(storeItemRepository.findById(DefaultStoreValues.defaultCustomerPurchaseItemID3)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomerPurchaseItem3));
//        Mockito.when(storeItemRepository.findById(DefaultStoreValues.defaultCustomerPurchaseItemID4)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomerPurchaseItem4));
//        final float expectedTotalCost = DefaultStoreValues.defaultCustomerPurchaseTotalCost;
//
//        // Stub the dto mapper to successfully convert the database record to output format
//        final OutboundPurchaseRecordDTO expectedResult = Mockito.mock(OutboundPurchaseRecordDTO.class);
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(Mockito.any(PurchaseRecord.class), Mockito.anyFloat())).thenReturn(expectedResult);
//        log.error("Expected found record: {}", foundRecord);
//
//        /**
//         * Exercise
//         */
//        final OutboundPurchaseRecordDTO actualResult = objectUnderTest.getPurchase(inputPurchaseID);
//        log.error("Actual result: {}", actualResult);
//
//        /**
//         * Verify
//         */
//        Assertions.assertTrue(expectedResult == actualResult);
//        ArgumentCaptor<PurchaseRecord> purchaseRecordArg = ArgumentCaptor.forClass(PurchaseRecord.class);
//        ArgumentCaptor<Float> totalCostArg = ArgumentCaptor.forClass(Float.class);
//        Mockito.verify(dtoMapper, Mockito.times(1)).toOutboundPurchaseRecordDTO(purchaseRecordArg.capture(), totalCostArg.capture());
//        log.error("total cost calculated: {}", totalCostArg.getValue());
//        Assertions.assertEquals((float)totalCostArg.getValue(), expectedTotalCost, "Total cost calculation error.");
//    }
//
//    @Test
//    public void getPurchaseFailure() {
//        /**
//         * Setup
//         */
//        final Long inputPurchaseID = 3L;
//        // Stub the purchase lookup to fail
//        Mockito.when(purchaseRecordRepository.findById(inputPurchaseID)).thenReturn(Optional.empty());
//
//        /**
//         * Exercise
//         */
//        try {
//            objectUnderTest.getPurchase(inputPurchaseID);
//        } catch(final Exception e) {
//            /**
//             * Verify
//             */
//            Assertions.assertTrue(true, "The exception was thrown when the purchase could not be found as expected.");
//            return;
//        }
//        /**
//         * Verify
//         */
//        Assertions.assertFalse(true, "An exception expected to be thrown when the purchase lookup fails, but it was not.");
//    }
//
//    @Test
//    public void customersLookupSuccess() {
//        /**
//         * Setup
//         */
//        Mockito.when(customerRepository.findAll()).thenReturn(DefaultStoreValues.defaultListOfCustomers);
//
//        /**
//         * Exercise
//         */
//        final List<String> actualResult = objectUnderTest.customers();
//
//
//        /**
//         * Verify
//         */
//        Assertions.assertEquals(actualResult.get(0), DefaultStoreValues.defaultCustomerID1);
//        Assertions.assertEquals(actualResult.get(1), DefaultStoreValues.defaultCustomerID2);
//        Assertions.assertEquals(actualResult.get(2), DefaultStoreValues.defaultCustomerID3);
//        Assertions.assertEquals(actualResult.get(3), DefaultStoreValues.defaultCustomerID4);
//        Assertions.assertEquals(actualResult.get(4), DefaultStoreValues.defaultCustomerID5);
//        Assertions.assertTrue(actualResult.size() == DefaultStoreValues.defaultListOfCustomers.size());
//        Assertions.assertTrue(DefaultStoreValues.defaultListOfCustomers.size() == 5);
//    }
//
//    @Test
//    public void customerPurchasesLookupSuccess() {
//        /**
//         * Setup
//         */
//        // Stub the customer lookup to succeed
//        final String customerID = DefaultStoreValues.defaultCustomerID1;
//        final Customer customer = DefaultStoreValues.defaultCustomer;
//        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomer));
//
//        // Stub the purchase record lookup to return various purchase records
//        final PurchaseRecord purchaseRecord1 = DefaultStoreValues.defaultCustomerPurchaseRecord;
//        final Long[] purchase2ItemIDs =  {DefaultStoreValues.defaultCustomerPurchaseItemID3, DefaultStoreValues.defaultCustomerPurchaseItemID4};
//        final PurchaseRecord purchaseRecord2 = DefaultStoreValues.createNewPurchaseRecord(321L, customerID, new Date(), purchase2ItemIDs);
//        final Long[] purchase3ItemIDs = {DefaultStoreValues.defaultCustomerPurchaseItemID3, DefaultStoreValues.defaultCustomerPurchaseItemID1};
//        final PurchaseRecord purchaseRecord3 = DefaultStoreValues.createNewPurchaseRecord(542L, customerID, new Date(), purchase3ItemIDs);
//        final List<PurchaseRecord> purchaseRecordList = new ArrayList<>();
//        purchaseRecordList.add(purchaseRecord3);// $6.48
//        purchaseRecordList.add(purchaseRecord1); // total of $558.15
//        purchaseRecordList.add(purchaseRecord2); // $52.32
//        Mockito.when(purchaseRecordRepository.findAllByCustomerID(customerID)).thenReturn(purchaseRecordList);
//
//        // Stub the dto mapper to return a separate mock for each input
//        final OutboundPurchaseRecordDTO outboundPurchase1 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("558.15");
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord1, 558.15f)).thenReturn(outboundPurchase1);
//        final OutboundPurchaseRecordDTO outboundPurchase2 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("52.32");
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord2, 52.32f)).thenReturn(outboundPurchase2);
//        final OutboundPurchaseRecordDTO outboundPurchase3 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("6.48");
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord3, 6.48f)).thenReturn(outboundPurchase3);
//
//        /**
//         * Exercise
//         */
//        final List<OutboundPurchaseRecordDTO> actualResult = objectUnderTest.customerPurchases(DefaultStoreValues.defaultCustomerID1);
//
//        /**
//         * Verify
//         */
//        // Make sure the correct arguments were passed into the mapper and that the results are in the correct order (sorted by cost decsending)
//        final ArgumentCaptor<PurchaseRecord> purchaseRecordArgumentCaptor = ArgumentCaptor.forClass(PurchaseRecord.class);
//        final ArgumentCaptor<Float> floatArgumentCaptor = ArgumentCaptor.forClass(Float.class);
//        Mockito.verify(dtoMapper, Mockito.times(3)).toOutboundPurchaseRecordDTO(purchaseRecordArgumentCaptor.capture(), floatArgumentCaptor.capture());
//        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(0).floatValue(), 6.48f);
//        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(1).floatValue(), 558.15f);
//        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(2).floatValue(), 52.32f);
//        log.error("Actual result: {}", actualResult);
//        Assertions.assertTrue(actualResult.get(0) == outboundPurchase1, String.format("Expected %s but got %s",outboundPurchase1, actualResult.get(0)));
//        Assertions.assertTrue(actualResult.get(1) == outboundPurchase2);
//        Assertions.assertTrue(actualResult.get(2) == outboundPurchase3);
//    }
//
//    @Test
//    void customerPurchasesLookupSuccessButMissingStoreItem() {
//        // Stub the customer lookup to succeed
//        final String customerID = DefaultStoreValues.defaultCustomerID1;
//        final Customer customer = DefaultStoreValues.defaultCustomer;
//        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomer));
//
//        // Stub the purchase record lookup to return various purchase records and include items not in the store repository
//        final PurchaseRecord purchaseRecord1 = DefaultStoreValues.defaultCustomerPurchaseRecord;
//        final Long[] purchase2ItemIDs =  {DefaultStoreValues.defaultCustomerPurchaseItemID3, DefaultStoreValues.defaultCustomerPurchaseItemID4, 55L};
//        final PurchaseRecord purchaseRecord2 = DefaultStoreValues.createNewPurchaseRecord(321L, customerID, new Date(), purchase2ItemIDs);
//        final Long[] purchase3ItemIDs = {DefaultStoreValues.defaultCustomerPurchaseItemID3, DefaultStoreValues.defaultCustomerPurchaseItemID1, 76L};
//        final PurchaseRecord purchaseRecord3 = DefaultStoreValues.createNewPurchaseRecord(542L, customerID, new Date(), purchase3ItemIDs);
//        final List<PurchaseRecord> purchaseRecordList = new ArrayList<>();
//        purchaseRecordList.add(purchaseRecord3);// $6.48
//        purchaseRecordList.add(purchaseRecord1); // total of $558.15
//        purchaseRecordList.add(purchaseRecord2); // $52.32
//        Mockito.when(purchaseRecordRepository.findAllByCustomerID(customerID)).thenReturn(purchaseRecordList);
//
//        // Stub the dto mapper to return a separate mock for each input
//        final OutboundPurchaseRecordDTO outboundPurchase1 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("558.15");
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord1, 558.15f)).thenReturn(outboundPurchase1);
//        final OutboundPurchaseRecordDTO outboundPurchase2 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("52.32");
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord2, 52.32f)).thenReturn(outboundPurchase2);
//        final OutboundPurchaseRecordDTO outboundPurchase3 = new OutboundPurchaseRecordDTO().setPurchaseTotalCost("6.48");
//        Mockito.when(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord3, 6.48f)).thenReturn(outboundPurchase3);
//
//        /**
//         * Exercise
//         */
//        final List<OutboundPurchaseRecordDTO> actualResult = objectUnderTest.customerPurchases(DefaultStoreValues.defaultCustomerID1);
//
//        /**
//         * Verify
//         */
//        // Make sure the correct arguments were passed into the mapper and that the results are in the correct order (sorted by cost decsending)
//        final ArgumentCaptor<PurchaseRecord> purchaseRecordArgumentCaptor = ArgumentCaptor.forClass(PurchaseRecord.class);
//        final ArgumentCaptor<Float> floatArgumentCaptor = ArgumentCaptor.forClass(Float.class);
//        Mockito.verify(dtoMapper, Mockito.times(3)).toOutboundPurchaseRecordDTO(purchaseRecordArgumentCaptor.capture(), floatArgumentCaptor.capture());
//        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(0).floatValue(), 6.48f);
//        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(1).floatValue(), 558.15f);
//        Assertions.assertEquals(floatArgumentCaptor.getAllValues().get(2).floatValue(), 52.32f);
//        log.error("Actual result: {}", actualResult);
//        Assertions.assertTrue(actualResult.get(0) == outboundPurchase1, String.format("Expected %s but got %s",outboundPurchase1, actualResult.get(0)));
//        Assertions.assertTrue(actualResult.get(1) == outboundPurchase2);
//        Assertions.assertTrue(actualResult.get(2) == outboundPurchase3);
//    }
//
//    @Test
//    public void customerPurchaseLookupFailCustomerNotFound() {
//        /**
//         * Setup
//         */
//        // stub the customer lookup to simulate non existing customer
//        final String customerID = DefaultStoreValues.defaultCustomerID1;
//        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.empty());
//
//        /**
//         * Exercise
//         */
//        try {
//            final List<OutboundPurchaseRecordDTO> actualResult = objectUnderTest.customerPurchases(customerID);
//        } catch(final Exception e) {
//            /**
//             * Verify
//             */
//            Assertions.assertTrue(true, "Exception was thrown as expected when no customer present.");
//            return;
//        }
//        /**
//         * Verify
//         */
//        Assertions.assertFalse(true, "Exception was not thrown as expected when no customer present");
//    }
//
//    @Test
//    void customerTotalSpentSuccess() {
//        /**
//         * Setup
//         */
//        final String customerID = DefaultStoreValues.defaultCustomerID1;
//        // stub the customer lookup to succeed
//        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.of(DefaultStoreValues.defaultCustomer));
//        // stub the customer's purchase lookup to return various purchases
//        final PurchaseRecord purchaseRecord1 = DefaultStoreValues.defaultCustomerPurchaseRecord;
//        final Long[] purchase2ItemIDs =  {DefaultStoreValues.defaultCustomerPurchaseItemID3, DefaultStoreValues.defaultCustomerPurchaseItemID4};
//        final PurchaseRecord purchaseRecord2 = DefaultStoreValues.createNewPurchaseRecord(321L, customerID, new Date(), purchase2ItemIDs);
//        final Long[] purchase3ItemIDs = {DefaultStoreValues.defaultCustomerPurchaseItemID3, DefaultStoreValues.defaultCustomerPurchaseItemID1};
//        final PurchaseRecord purchaseRecord3 = DefaultStoreValues.createNewPurchaseRecord(542L, customerID, new Date(), purchase3ItemIDs);
//        final List<PurchaseRecord> purchaseRecordList = new ArrayList<>();
//        purchaseRecordList.add(purchaseRecord3);// $6.48
//        purchaseRecordList.add(purchaseRecord1); // total of $558.15
//        purchaseRecordList.add(purchaseRecord2); // $52.32
//        Mockito.when(purchaseRecordRepository.findAllByCustomerID(customerID)).thenReturn(purchaseRecordList);
//
//        /**
//         * Exercise
//         */
//        final String actualResult = objectUnderTest.customerTotalSpent(customerID);
//
//        /**
//         * Verify
//         */
//        Mockito.verify(customerRepository, Mockito.times(1)).findById(customerID);
//        Mockito.verify(purchaseRecordRepository, Mockito.times(1)).findAllByCustomerID(customerID);
//        final String expectedResult = "$616.95";
//        Assertions.assertEquals(actualResult, expectedResult);
//    }
//
//    @Test
//    void customerTotalSpentFailCustomerNotFound() {
//        /**
//         * Setup
//         */
//        // stub the customer lookup to simulate non existing customer
//        final String customerID = DefaultStoreValues.defaultCustomerID1;
//        Mockito.when(customerRepository.findById(customerID)).thenReturn(Optional.empty());
//
//        /**
//         * Exercise
//         */
//        try {
//            final String actualResult = objectUnderTest.customerTotalSpent(customerID);
//        } catch(final Exception e) {
//            /**
//             * Verify
//             */
//            Assertions.assertTrue(true, "Exception was thrown as expected when no customer present.");
//            return;
//        }
//        /**
//         * Verify
//         */
//        Assertions.assertFalse(true, "Exception was not thrown as expected when no customer present");
//    }


}