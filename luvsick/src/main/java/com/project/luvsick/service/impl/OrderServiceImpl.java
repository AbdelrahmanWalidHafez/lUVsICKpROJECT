package com.project.luvsick.service.impl;
import com.project.luvsick.dto.CustomerDTO;
import com.project.luvsick.dto.OrderDTO;

import com.project.luvsick.dto.OrderResponseDTO;
import com.project.luvsick.exception.InsufficientStockException;
import com.project.luvsick.mapper.CustomerMapper;
import com.project.luvsick.mapper.OrderMapper;
import com.project.luvsick.model.*;
import com.project.luvsick.repo.CustomerRepository;
import com.project.luvsick.repo.OrderRepository;
import com.project.luvsick.repo.ProductRepository;
import com.project.luvsick.repo.ProductSizesRepository;
import com.project.luvsick.service.EmailService;
import com.project.luvsick.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final CustomerMapper customerMapper;
    private final ProductSizesRepository productSizesRepository;

    @Override
    @Transactional
    public Order createNewOrder(OrderDTO orderDTO) {
        Customer customer=handleCustomer(orderDTO.getCustomerDTO());
        List<Product> products=fetchProducts(orderDTO.getProductUUIDS());
        Map<UUID, Integer> itemPerOrderQuantity = new HashMap<>();
        List<ProductSizes> productSizesList = new ArrayList<>();
        BigDecimal totalPrice = processProductSizes(orderDTO.getProductSizesUUIDS(), itemPerOrderQuantity, productSizesList);
             Order order=Order
                     .builder()
                     .customer(customer)
                     .products(products)
                     .status(OrderStatus.RECEIVED)
                     .totalPrice(totalPrice)
                     .itemPerQuantity(itemPerOrderQuantity)
                     .build();
             log.info("order is created");
       emailService.sendOrderReceivedEmail(customer.getEmail(),order);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public List<OrderResponseDTO> getOrders(OrderStatus orderStatus, int pageNum, String sortDir, String sortField) {
        int pageSize=10;
        Pageable pageable= PageRequest.of(
                pageNum-1,pageSize,sortDir.
                        equalsIgnoreCase("asc")? Sort.by(sortField)
                        .ascending():Sort.by(sortField)
                        .descending());
        Page<Order>order;
        if (orderStatus==null) {
            order=orderRepository.findAll(pageable);
            return order
                    .getContent()
                    .stream()
                    .map(orderMapper::toDto)
                    .collect(Collectors.toList());
        }
        order=orderRepository.findAllByStatus(orderStatus,pageable);
        return order
                .getContent()
                .stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public void updateStatus(UUID id, OrderStatus orderStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Order Found With Id " + id));
        order.setStatus(orderStatus);
        orderRepository.save(order);
        log.info("order updated");
        emailService.sendNewOrderStatusEmail(order.getCustomer().getEmail(),orderStatus);
    }
    /**
     * Retrieves an existing Customer by email or creates a new one from the given CustomerDTO.
     * <p>
     * If a customer with the provided email exists, it is returned.
     * Otherwise, a new Customer entity is created from the DTO and saved to the repository.
     * </p>
     *
     * @param customerDTO the data transfer object containing customer information
     * @return the existing or newly saved Customer entity
     */
    private Customer handleCustomer(CustomerDTO customerDTO){
        Customer customer = customerRepository
                .findByEmail(customerDTO.getEmail())
                .orElseGet(() -> customerMapper.toCustomer(customerDTO));
        return customerRepository.save(customer);
    }
    /**
     * Fetches a list of products by their UUIDs.
     *
     * @param productUUIDS the list of product UUIDs to fetch
     * @return a list of Product entities corresponding to the given UUIDs
     */
    private List<Product> fetchProducts(List<UUID> productUUIDS){
        return productRepository.findAllById(productUUIDS);
    }
    /**
     * Processes product sizes for an order, updating stock quantities and calculating the total price.
     * <p>
     * This method verifies if the requested quantities are available in stock,
     * updates the stock quantities accordingly, and accumulates the total price of the order.
     * The updated product sizes are saved to the repository within a transactional context.
     * </p>
     *
     * @param productSizesUUIDS a map of ProductSizes UUIDs to requested quantities
     * @param itemPerOrderQuantity a map that will be populated with product size UUIDs and their ordered quantities
     * @param productSizesList a list to accumulate ProductSizes entities with updated quantities
     * @return the total price for all requested product sizes and quantities
     * @throws EntityNotFoundException if a ProductSizes entity cannot be found for a given UUID
     * @throws InsufficientStockException if the requested quantity exceeds available stock
     */
    @Transactional
    private BigDecimal processProductSizes(
            Map<UUID, Integer> productSizesUUIDS,
            Map<UUID, Integer> itemPerOrderQuantity,
            List<ProductSizes> productSizesList) {

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<UUID, Integer> entry : productSizesUUIDS.entrySet()) {
            UUID uuid = entry.getKey();
            Integer requestedQuantity = entry.getValue();
            ProductSizes productSizes = productSizesRepository.findById(uuid)
                    .orElseThrow(() -> new EntityNotFoundException("Could not find a productSize with id: " + uuid));

            if (productSizes.getQuantity() < requestedQuantity) {
                throw new InsufficientStockException("Insufficient stock for product size: " + uuid);
            }
            productSizes.setQuantity(productSizes.getQuantity() - requestedQuantity);
            productSizesList.add(productSizes);
            itemPerOrderQuantity.put(uuid, requestedQuantity);
            BigDecimal itemPrice = calculateItemPrice(productSizes.getProduct(), requestedQuantity);
            totalPrice = totalPrice.add(itemPrice);
        }

        productSizesRepository.saveAll(productSizesList);
        return totalPrice;
    }
    /**
     * Calculates the total price for a given product and quantity, taking into account any discount.
     *
     * @param product the Product entity
     * @param quantity the quantity ordered
     * @return the total price as a BigDecimal after applying the product discount
     */
    private BigDecimal calculateItemPrice(Product product, Integer quantity) {
        return product.getPrice()
                .multiply(BigDecimal.valueOf(100 - product.getDiscount()))
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(quantity));
    }
}
