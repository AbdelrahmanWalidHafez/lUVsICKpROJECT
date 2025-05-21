package com.project.luvsick.mapper;

import com.project.luvsick.dto.CustomerDTO;
import com.project.luvsick.model.Customer;
import org.springframework.stereotype.Component;

/**
 * @author Abdelrahman Walid Hafez
 */
@Component
public class CustomerMapper {
    /**
     * Converts a {@link CustomerDTO} object to a {@link Customer} entity.
     *
     * @param customerDTO the data transfer object containing customer data
     * @return a {@link Customer} entity built from the DTO data
     */
    public Customer toCustomer(CustomerDTO customerDTO){
        return Customer
                .builder()
                .email(customerDTO.getEmail())
                .City(customerDTO.getCity())
                .BuildingNumber(customerDTO.getBuildingNumber())
                .flatNumber(customerDTO.getFlatNumber())
                .name(customerDTO.getName())
                .Street(customerDTO.getStreet())
                .phoneNumber(customerDTO.getPhoneNumber())
                .build();
    }
    /**
     * Converts a {@link Customer} entity to a {@link CustomerDTO} object.
     *
     * @param customer the customer entity to convert
     * @return a {@link CustomerDTO} built from the entity data
     */
    public CustomerDTO toDto(Customer customer) {
        return CustomerDTO
                .builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .city(customer.getCity())
                .buildingNumber(customer.getBuildingNumber())
                .flatNumber(customer.getFlatNumber())
                .name(customer.getName())
                .street(customer.getStreet())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
