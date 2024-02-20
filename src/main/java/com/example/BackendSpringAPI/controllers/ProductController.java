package com.example.BackendSpringAPI.controllers;

import com.example.BackendSpringAPI.dtos.ProductDTO;
import com.example.BackendSpringAPI.dtos.ProductImageDTO;
import com.example.BackendSpringAPI.models.Product;
import com.example.BackendSpringAPI.models.ProductImage;
import com.example.BackendSpringAPI.repositories.ProductImageRepository;
import com.example.BackendSpringAPI.repositories.ProductRepository;
import com.example.BackendSpringAPI.responses.ProductListResponse;
import com.example.BackendSpringAPI.responses.ProductResponse;
import com.example.BackendSpringAPI.services.IProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final IProductService productService;
    private final ProductRepository productRepository;
    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult result){
        try{
            //save the product
            if(result.hasErrors()){
                List<String> errorMessage = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") Long productId, @ModelAttribute List<MultipartFile> files){
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
                return ResponseEntity.badRequest().body("You can only upload maximum 5 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
            String firstImageName = null; // Lưu tên của ảnh đầu tiên
            for(MultipartFile file : files){
                if(file.getSize() == 0){
                    continue;
                }
                // kiểm tra kích thước file và định dạng file
                if(file.getSize() > 10 * 1024 * 1024){
                    // kích thước > 10 mb
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large, maximum file size is 10MB");
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")){
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                // lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file);
                if (firstImageName == null) {
                    firstImageName = filename; // Lưu tên của ảnh đầu tiên
                }
                // lưu vào đối tượng product trong db
                ProductImage productImage = productService.createProductImage(existingProduct.getId(),
                        ProductImageDTO
                                .builder()
                                .imageUrl(filename)
                                .build());
                productImages.add(productImage);
            }
            // Kiểm tra thumbnail có null và có ảnh đầu tiên trước khi cập nhật
            if (firstImageName != null && existingProduct.getThumbnail() == null) {
                existingProduct.setThumbnail(firstImageName);
                productRepository.save(existingProduct); // Cập nhật trường thumbnail của sản phẩm
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    private String storeFile(MultipartFile file)throws IOException{
        if(!isImageFile(file) || file.getOriginalFilename() == null){
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        //thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFileName = UUID.randomUUID().toString() + "_" + filename;
        java.nio.file.Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        //Đường dẫn đến file đích
        Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()){
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }else{
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable long imageId) {
        try {
            productService.deleteImage(imageId);
            return ResponseEntity.ok(String.format("Image with id = %d deleted successfully", imageId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/all")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit){
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d", keyword, categoryId, page, limit));
        PageRequest pageRequest = PageRequest.of(page-1, limit, Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

    @GetMapping("/findByName")
    public ResponseEntity<ProductListResponse> getProductsWithName(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId) {

        List<ProductResponse> products = productService.searchWithName(keyword, categoryId);

        return ResponseEntity.ok(ProductListResponse.builder()
                .products(products)
                .totalPages(1) // Since we're not using pagination, total pages is always 1
                .build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId){
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id){
        try {
            productService.deleteProduct(id);
            return  ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //update product
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable long id, @RequestBody ProductDTO productDTO){
        try{
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/images/product_id/{productId}")
    public ResponseEntity<?> getImagesByProductId(@PathVariable Long productId) {
        try {
            List<ProductImage> productImages = productService.getImagesByProductId(productId);
            if (productImages.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving images for product with ID: " + productId);
        }
    }


    //@PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts(){
        Faker faker = new Faker();
        for(int i = 0; i < 1000000; i++){
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO
                    .builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long)faker.number().numberBetween(1, 4))
                    .build();
            try {
                productService.createProduct(productDTO);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake news :)))");
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<ProductListResponse> getProductsByCategory(
            @PathVariable("id") Long categoryId) throws Exception {
        List<ProductResponse> productsByCategory = productService.getProductByCategoryId(categoryId);
        int totalPages = 1;  // Assuming only one page for category-specific listing

        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(productsByCategory)
                .totalPages(totalPages)
                .build());
    }


    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids){
        try{
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
