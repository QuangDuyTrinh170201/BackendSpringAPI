package com.example.BackendSpringAPI.repositories;

import com.example.BackendSpringAPI.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //tìm đơn hàng của 1 user nào đó
    List<Order> findByUserId(Long userId);

    @Query("SELECT o FROM Order o WHERE (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% OR o.address LIKE %:keyword% OR o.note LIKE %:keyword% "+
            "OR o.email LIKE %:keyword%)")
    Page<Order> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR o.fullName LIKE %:keyword% OR o.address LIKE %:keyword%)" +
            "OR o.note LIKE %:keyword%")
    List<Order> findByKeywordNotPaging(String keyword);
}
