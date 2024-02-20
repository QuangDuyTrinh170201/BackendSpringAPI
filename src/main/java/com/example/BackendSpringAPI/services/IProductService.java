package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.dtos.ProductDTO;
import com.example.BackendSpringAPI.dtos.ProductImageDTO;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.models.ProductImage;
import com.example.BackendSpringAPI.responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(long id) throws Exception;
    Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);

    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;

    public List<ProductResponse> getProductByCategoryId(Long categoryId) throws Exception;

    public List<Product> findProductsByIds(List<Long> productIds);

    public List<ProductImage> getImagesByProductId(Long productId);

    public List<ProductResponse> searchWithName(String keyword, Long categoryId);

    public void deleteImage(long imageId);
}
