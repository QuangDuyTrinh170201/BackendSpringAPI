package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.OrderDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;
    Order getOrder(Long id);
    Order updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(Long id);
    List<Order> findByUserId(Long userId);

    public List<Order> getOrdersByKeywordNotPaging(String keyword);
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable);
}
