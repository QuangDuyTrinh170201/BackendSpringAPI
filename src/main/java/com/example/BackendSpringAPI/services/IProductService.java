package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.ProductDTO;
import com.example.BackendSpringAPI.dtos.ProductImageDTO;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.models.ProductImage;
import com.example.BackendSpringAPI.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);

    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;
}
