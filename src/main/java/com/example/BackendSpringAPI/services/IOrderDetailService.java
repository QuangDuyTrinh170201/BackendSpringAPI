package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.controllers.OrderDetailController;
import com.example.BackendSpringAPI.dtos.OrderDetailDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws Exception;
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);
}
