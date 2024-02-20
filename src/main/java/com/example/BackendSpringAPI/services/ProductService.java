package com.example.BackendSpringAPI.services;

import com.example.BackendSpringAPI.controllers.ProductController;
import com.example.BackendSpringAPI.dtos.ProductDTO;
import com.example.BackendSpringAPI.dtos.ProductImageDTO;
import com.example.BackendSpringAPI.exceptions.DataNotFoundException;
import com.example.BackendSpringAPI.exceptions.InvalidParamException;
import com.example.BackendSpringAPI.models.Category;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.models.ProductImage;
import com.example.BackendSpringAPI.repositories.CategoryRepository;
import com.example.BackendSpringAPI.repositories.ProductImageRepository;
import com.example.BackendSpringAPI.repositories.ProductRepository;
import com.example.BackendSpringAPI.responses.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category of this product with id: " + productDTO.getCategoryId()));
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if(optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product with id =" + productId);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        //lâý danh sách sản phẩm theo page và limit
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    public List<ProductResponse> searchWithName(String keyword, Long categoryId) {
        // Lấy danh sách sản phẩm không phân trang dựa trên từ khóa và categoryId
        List<Product> productList = productRepository.searchProductWithKeyname(keyword, categoryId);
        return productList.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(long imageId) {
        Optional<ProductImage> imageOptional = productImageRepository.findById(imageId);
        if (imageOptional.isPresent()) {
            ProductImage imageToDelete = imageOptional.get();
            // Xóa file từ hệ thống file trước
            String imageUrl = imageToDelete.getImageUrl();
            if (imageUrl != null) {
                Path imagePath = Paths.get("uploads/" + imageUrl);
                try {
                    Files.deleteIfExists(imagePath);
                } catch (IOException e) {

                    logger.error("Failed to delete image file: " + imageUrl);
                }
            }
            productImageRepository.delete(imageToDelete);
        } else {
            throw new IllegalArgumentException("Image not found with id: " + imageId);
        }
    }


    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if(existingProduct != null){
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find category of this product with id: " + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id: " + productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        //không cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
            throw new InvalidParamException("Number of image must be <= "+ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    @Override
    public List<ProductResponse> getProductByCategoryId(Long categoryId) throws Exception {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + categoryId));

        List<Product> productsByCategory = productRepository.findAllByCategory(category);

        return productsByCategory.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
    }


    @Override
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds){
        return productRepository.findProductsByIds(productIds);
    }
}
