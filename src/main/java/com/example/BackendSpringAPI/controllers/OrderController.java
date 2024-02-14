package com.example.BackendSpringAPI.controllers;

import com.example.BackendSpringAPI.dtos.OrderDTO;
import com.example.BackendSpringAPI.models.Order;
import com.example.BackendSpringAPI.responses.OrderListResponse;
import com.example.BackendSpringAPI.responses.OrderResponse;
import com.example.BackendSpringAPI.services.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO, BindingResult result){
        try{
            if(result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Order orderResponse = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}") //Thêm đường dẫn cho user_id
    //GET http://localhost:1702/api/v1/orders/user/4
    public ResponseEntity<?> getOrders(@Valid @PathVariable("user_id") Long userId){
        try{
            List<Order> orders = orderService.findByUserId(userId);
            return ResponseEntity.ok(orders);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    //GET http://localhost:1702/api/v1/orders/4
    public ResponseEntity<?> getOrder(@Valid @PathVariable("id") Long orderId){
        try{
            Order existingOrder = orderService.getOrder(orderId);
            return ResponseEntity.ok(OrderResponse.fromOrder(existingOrder));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@Valid @PathVariable long id, @Valid @RequestBody OrderDTO orderDTO){
        try{
            Order order = orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Valid @PathVariable Long id){
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<OrderListResponse> getOrderByKeyword(
            @RequestParam(defaultValue="", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ){
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id").ascending());
        Page<OrderResponse> orderPage = orderService.getOrdersByKeyword(keyword, pageRequest).map(OrderResponse::fromOrder);

        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok(OrderListResponse
                .builder()
                .orders(orderResponses)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/get-orders-by-keyword-not-paging")
    public ResponseEntity<OrderListResponse> getOrderByKeywordNotPaging(
            @RequestParam(defaultValue="", required = false) String keyword
    ){
        // Gọi phương thức service để lấy danh sách đơn hàng
        List<OrderResponse> orderResponses = orderService.getOrdersByKeywordNotPaging(keyword).stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());

        // Trả về danh sách đơn hàng
        return ResponseEntity.ok(OrderListResponse
                .builder()
                .orders(orderResponses)
                .build());
    }
}
