package jake.blog.inventoryApi.controllers;

import jake.blog.inventoryApi.mappers.DtoMapper;
import jake.blog.inventoryApi.model.db.PurchaseRecord;
import jake.blog.inventoryApi.model.db.Customer;
import jake.blog.inventoryApi.model.dto.InboundPurchaseRecordDTO;
import jake.blog.inventoryApi.model.dto.OutboundPurchaseRecordDTO;
import jake.blog.inventoryApi.persist.CustomerRepository;
import jake.blog.inventoryApi.persist.PurchaseRecordRepository;
import jake.blog.inventoryApi.persist.StoreItemRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/store")
@Log4j2
public class StoreInventoryController {

    private final PurchaseRecordRepository purchaseRecordRepository;
    private final StoreItemRepository storeItemRepository;
    private final CustomerRepository customerRepository;
    private final DtoMapper dtoMapper;

    StoreInventoryController(final PurchaseRecordRepository purchaseRecordRepository,
                             final StoreItemRepository storeItemRepository,
                             final CustomerRepository customerRepository,
                             final DtoMapper dtoMapper) {
        this.purchaseRecordRepository = purchaseRecordRepository;
        this.storeItemRepository = storeItemRepository;
        this.customerRepository = customerRepository;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/purchase")
    public PurchaseRecord purchase(final @RequestBody InboundPurchaseRecordDTO purchaseRecordDTO) {
        if(customerRepository.findById(purchaseRecordDTO.getCustomerID()).isEmpty()) {
            customerRepository.save(new Customer().setCustomerID(purchaseRecordDTO.getCustomerID()));
        }
        return purchaseRecordRepository.save(dtoMapper.toPurchaseRecord(purchaseRecordDTO));
    }

    @GetMapping("/purchase/{purchaseID}")
    public OutboundPurchaseRecordDTO getPurchase(final @PathVariable("purchaseID") Long purchaseID) {
        final Optional<PurchaseRecord> purchase = purchaseRecordRepository.findById(purchaseID);
        if(purchase.isEmpty()) {
            throw new RuntimeException(String.format("Could not find purchase with id, %d", purchaseID));
        }
        log.error("Actual found record: {}", purchase.get());
        return dtoMapper.toOutboundPurchaseRecordDTO(purchase.get(), getPurchaseTotalCost(purchase.get()));
    }

    @GetMapping("customers")
    public List<String> customers() {
        final List<String> customers = new ArrayList<>();
        customerRepository.findAll().forEach(customer -> customers.add(customer.getCustomerID()));
        return customers;
    }

    @GetMapping("{customerID}/purchases")
    public List<OutboundPurchaseRecordDTO> customerPurchases(final @PathVariable("customerID") String customerID) {
        if(customerRepository.findById(customerID).isEmpty()) {
            throw new RuntimeException(String.format("Could not find customer, %s, in our database", customerID));
        }
        final List<PurchaseRecord> purchaseRecords = purchaseRecordRepository.findAllByCustomerID(customerID);
        final List<OutboundPurchaseRecordDTO> result = new ArrayList<>();
        for(final PurchaseRecord purchaseRecord : purchaseRecords) {
            result.add(dtoMapper.toOutboundPurchaseRecordDTO(purchaseRecord, getPurchaseTotalCost(purchaseRecord)));
        }
        return result.stream()
                .peek(record -> log.error("On record {}", record))
                .sorted((t0, t1) -> Float.valueOf(t0.getPurchaseTotalCost()) > Float.valueOf(t1.getPurchaseTotalCost()) ? -1 : 1)
                .peek(record -> log.error("post sorting.. on record {}", record))
                .collect(Collectors.toList());
    }

    @GetMapping("{customerID}/totalSpent")
    public String customerTotalSpent(final @PathVariable("customerID") String customerID) {
        if(customerRepository.findById(customerID).isEmpty()) {
            throw new RuntimeException(String.format("Could not find customer, %s, in our database", customerID));
        }
        return String.format("$%.2f", purchaseRecordRepository.findAllByCustomerID(customerID).stream()
                .mapToDouble(purchaseRecord -> getPurchaseTotalCost(purchaseRecord))
                .reduce(Double::sum)
                .getAsDouble());
    }

    private Float getPurchaseTotalCost(final PurchaseRecord purchaseRecord) {
        return Math.round(purchaseRecord.getPurchasedItemIDs().stream()
                .map(itemID -> storeItemRepository.findById(itemID))
                .map(item -> BigDecimal.valueOf(item.get().getCost()))
                .reduce(BigDecimal::add).get().floatValue() * 100f) / 100f;
    }
}
