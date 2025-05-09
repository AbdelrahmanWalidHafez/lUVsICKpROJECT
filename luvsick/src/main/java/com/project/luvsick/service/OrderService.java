package com.project.luvsick.service;

import com.project.luvsick.dto.OrderDTO;
import com.project.luvsick.model.Order;

public interface OrderService {
    public Order createNewOrder(OrderDTO orderDTO) ;

}
