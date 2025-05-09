package com.project.luvsick.service;
import com.project.luvsick.dto.OrderDTO;

import com.project.luvsick.exception.InsufficientStockException;
import com.project.luvsick.mapper.CustomerMapper;
import com.project.luvsick.mapper.OrderMapper;
import com.project.luvsick.model.*;
import com.project.luvsick.repo.CustomerRepository;
import com.project.luvsick.repo.OrderRepository;
import com.project.luvsick.repo.ProductRepository;
import com.project.luvsick.repo.ProductSizesRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
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
        Customer customer=customerRepository
                .findByEmail(orderDTO.getCustomerDTO().getEmail())
                .orElse(customerMapper.toCustomer(orderDTO.getCustomerDTO()));
        customerRepository.save(customer);
        List<Product> products=new ArrayList<>();
        Map<UUID,Integer> itemPerOrderQuantity=new HashMap<>();
        orderDTO.getProductUUIDS().forEach((uuid )->{
            Product product=productRepository.findById(uuid)
                    .orElseThrow(()->new  EntityNotFoundException("could not find a product with id"+uuid));
            products.add(product);
        } );
        List<ProductSizes> productSizesList=new ArrayList<>();
        final BigDecimal[] totalPrice = {new BigDecimal(0)};
        orderDTO.getProductSizesUUIDS().forEach((uuid,quantity )->{
            ProductSizes productSizes=productSizesRepository.findById(uuid)
                    .orElseThrow(()->new  EntityNotFoundException("could not find a productSize with id"+uuid));
            productSizesList.add(productSizes);
            if (productSizes.getQuantity()<orderDTO.getProductSizesUUIDS().get(productSizes.getId())){
                throw new InsufficientStockException("no enough Quantity");
            }
            productSizes.setQuantity(productSizes.getQuantity()-orderDTO.getProductSizesUUIDS().get(productSizes.getId()));
            productSizesRepository.save(productSizes);
            itemPerOrderQuantity.put(productSizes.getId(),orderDTO.getProductSizesUUIDS().get(productSizes.getId()));
            totalPrice[0] = totalPrice[0].add(
                    productSizes.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(100 - productSizes.getProduct().getDiscount()))
                            .divide(BigDecimal.valueOf(100)))
                            .multiply(BigDecimal.valueOf(orderDTO.getProductSizesUUIDS().get(productSizes.getId())));
        } );
             Order order=Order
                     .builder()
                     .customer(customer)
                     .products(products)
                     .status(OrderStatus.RECEIVED)
                     .totalPrice(totalPrice[0])
                     .itemPerQuantity(itemPerOrderQuantity)
                     .build();
             emailService.sendOrderRecievcedEmail(customer.getEmail());
        return orderRepository.save(order);
    }
}
