package com.project.luvsick.mapper;

import com.project.luvsick.dto.CustomerDTO;
import com.project.luvsick.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
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

    public CustomerDTO toDto(Customer customer) {
        return CustomerDTO
                .builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .city(customer.getCity())
                .buildingNumber(customer.getBuildingNumber())
                .name(customer.getName())
                .street(customer.getStreet())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
