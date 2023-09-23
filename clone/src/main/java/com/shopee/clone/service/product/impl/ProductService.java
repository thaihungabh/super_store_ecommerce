package com.shopee.clone.service.product.impl;

import com.shopee.clone.DTO.product.*;
import com.shopee.clone.DTO.product.request.ProductRequestCreate;
import com.shopee.clone.DTO.product.update.ProductRequestEdit;
import com.shopee.clone.DTO.product.response.*;
import com.shopee.clone.entity.*;
import com.shopee.clone.repository.CategoryRepository;
import com.shopee.clone.repository.product.ProductItemRepository;
import com.shopee.clone.repository.product.ProductRepository;
import com.shopee.clone.service.product.IProductService;
import com.shopee.clone.util.ResponseObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ProductItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    public ProductService(ProductRepository productRepository,
                          ProductItemRepository itemRepository,
                          ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResponseEntity<?> getAllProductBelongWithShop(Long shopId) {
        try{
            List<ProductEntity> productEntities = productRepository.findAll();
            List<ProductResponseDTO> productResponseDTOs = mappingProductEntityListToProductDTOs(productEntities);

            ProductResponseObject<List<ProductResponseDTO>> productsResponse = new ProductResponseObject<>();
            productsResponse.setData(productResponseDTOs);

            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("SUCCESS")
                                    .message("Get Products Success")
                                    .results(productsResponse)
                                    .build()
                    );
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message(e.getMessage())
                                    .results("")
                                    .build()
                    );
        }
    }

    @Override
    public ResponseEntity<?> getAllProductByCategoryId(Long categoryId) {
        try{
            if(categoryRepository.existsById(categoryId)){
                List<ProductEntity> productEntities = productRepository.findProductsByCategoryId(categoryId);
                List<ProductResponseDTO> productResponseDTOs = mappingProductEntityListToProductDTOs(productEntities);

                ProductResponseObject<List<ProductResponseDTO>> productsResponse = new ProductResponseObject<>();
                productsResponse.setData(productResponseDTOs);

                return ResponseEntity
                        .status(HttpStatusCode.valueOf(200))
                        .body(
                                ResponseObject
                                        .builder()
                                        .status("SUCCESS")
                                        .message("Get Products By Category Success")
                                        .results(productsResponse)
                                        .build()
                        );
            }
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message(e.getMessage())
                                    .results("")
                                    .build()
                    );
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getAllProductPaging(Pageable pageable) {
        try{
            Page<ProductEntity> productEntities = productRepository.findProducts(pageable);
            List<ProductResponseDTO> productResponseDTOs = mappingProductEntityListToProductDTOs(productEntities);

            ProductResponseObject<List<ProductResponseDTO>> productsResponse = new ProductResponseObject<>();
            productsResponse.setData(productResponseDTOs);

            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("SUCCESS")
                                    .message("Get Products Success")
                                    .results(productsResponse)
                                    .build()
                    );
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message(e.getMessage())
                                    .results("")
                                    .build()
                    );
        }
    }

    /*
     * Nhận vào List ProductItemResponseDTO Rỗng và itemEntity -> convert Img-OptionType-OptionValue
     * sang ProductItemResponseDTO - sau đó add vào List ProductItemResponseDTO
     * */
    private void mappingSpecialImg_OptionWithProductItem(List<ProductItemResponseDTO> productItemResponseDTOList,
                                                         ProductItemEntity productItemEntity) {
        List<OptionValue> optionValues;
        List<ImageProduct> imageProducts = productItemEntity.getImageProductList()
                .stream()
                .map(imageProductEntity -> ImageProduct.builder()
                        .imgProductId(imageProductEntity.getImgProductId())
                        .imgPublicId(imageProductEntity.getImgPublicId())
                        .imgProductUrl(imageProductEntity.getImgProductUrl())
                        .build())
                .collect(Collectors.toList());

        optionValues = productItemEntity.getOptionValues()
                .stream()
                .map(optionValueEntity -> OptionValue.builder()
                        .opValueId(optionValueEntity.getOpValueId())
                        .valueName(optionValueEntity.getValueName())
                        .optionType(OptionType

                                .builder()
                                .opTypeId(optionValueEntity.getOptionType().getOpTypeId())
                                .optionName(optionValueEntity.getOptionType().getOptionName())
                                .build())
                        .build())
                .collect(Collectors.toList());

        List<OptionTypeDTO> optionTypeDTOS = optionValues
                .stream()
                .map(optionValue -> OptionTypeDTO
                        .builder()
                        .opTypeId(optionValue.getOptionType().getOpTypeId())
                        .optionName(optionValue.getOptionType().getOptionName())
                        .optionValue(OptionValueDTO
                                .builder()
                                .opValueId(optionValue.getOpValueId())
                                .valueName(optionValue.getValueName())
                                .build())
                        .build()).collect(Collectors.toList());

        ProductItemResponseDTO productItemResponseDTO = ProductItemResponseDTO
                .builder()
                .pItemId(productItemEntity.getPItemId())
                .price(productItemEntity.getPrice())
                .qtyInStock(productItemEntity.getQtyInStock())
                .status(productItemEntity.getStatus())
                .imageProductList(imageProducts)
                .optionTypes(optionTypeDTOS)
                .build();
        productItemResponseDTOList.add(productItemResponseDTO);
    }

    @Override
    public ResponseEntity<?> getProductById(Long productId) {
        try {
            ProductResponseDTO productResponseDTO = getProductByIdForService(productId);
            ProductResponseObject<ProductResponseDTO> productResponse = new ProductResponseObject<>();
                productResponse.setData(productResponseDTO);
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(200))
                        .body(
                                ResponseObject
                                        .builder()
                                        .status("SUCCESS")
                                        .message("Get Product Success")
                                        .results(productResponse)
                                        .build()
                        );
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message(e.getMessage())
                                    .results("")
                                    .build()
                    );
        }
    }

    @Override
    public ProductResponseDTO getProductByIdForService(Long productId) {
        try {
            ProductEntity productEntity = productRepository.findById(productId)
                    .orElseThrow(NoSuchElementException::new);
            if (productEntity.getStatus()) {
                List<ProductItemEntity> productItemEntities = productEntity.getProductItemList();
                ProductResponseDTO productResponseDTO = new ProductResponseDTO();
                List<ProductItemResponseDTO> productItemResponseDTOList = new ArrayList<>();

                for (ProductItemEntity productItemEntity : productItemEntities) {

                    mappingSpecialImg_OptionWithProductItem(productItemResponseDTOList, productItemEntity);

                    productResponseDTO = ProductResponseDTO
                            .builder()
                            .productName(productEntity.getProductName())
                            .description(productEntity.getDescription())
                            .status(productEntity.getStatus())
                            .productItemResponseList(productItemResponseDTOList)
                            .build();
                }
                return productResponseDTO;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<?> addNewProduct(ProductRequestCreate productRequest) {
        try {
            if(categoryRepository.existsById(productRequest.getCategoryId())){
                CategoryEntity categoryEntity = categoryRepository.findById(productRequest.getCategoryId())
                        .orElseThrow(NoSuchElementException::new);
                CategoryEntity category = CategoryEntity
                        .builder()
                        .id(categoryEntity.getId())
                        .content(categoryEntity.getContent())
                        .imagePublicId(categoryEntity.getImagePublicId())
                        .imageUrl(categoryEntity.getImageUrl())
                        .left(categoryEntity.getLeft())
                        .right(categoryEntity.getRight())
                        .build();
                Product product = Product
                        .builder()
                        .productName(productRequest.getProductName())
                        .description(productRequest.getDescription())
                        .status(true)
                        .category(category)
                        .build();
                Product productAfterSaved = modelMapper.map(
                        productRepository.save(modelMapper.map(product,ProductEntity.class)),Product.class);
                ProductResponseObject<Product> productResponse = new ProductResponseObject<>();
                productResponse.setData(productAfterSaved);
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(201))
                        .body(
                                ResponseObject
                                        .builder()
                                        .status("SUCCESS")
                                        .message("Add Product Success Pls Do Next-Step: Add Item and list image")
                                        .results(productResponse)
                                        .build()
                        );
            }

        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message(e.getMessage())
                                    .results("")
                                    .build()
                    );
        }
        return null;
    }

    @Override
    public ResponseEntity<?> searchProductByName(String productName) {
        try {
            if(productName.isBlank()){
                return ResponseEntity
                        .badRequest()
                        .body(
                                ResponseObject
                                        .builder()
                                        .status("FAIL")
                                        .message("Name Search Not Null")
                                        .build()
                        );
            }
            List<ProductEntity> productEntities = productRepository.searchByProductName(productName);
            if(productEntities.isEmpty()){
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(204))
                        .body(
                                ResponseObject
                                        .builder()
                                        .status("FAIL")
                                        .message("NOT FOUND")
                                        .build()
                        );
            }
            List<ProductResponseDTO> productResponseDTOs = mappingProductEntityListToProductDTOs(productEntities);

            ProductResponseObject<List<ProductResponseDTO>> productsResponse = new ProductResponseObject<>();
            productsResponse.setData(productResponseDTOs);

            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("SUCCESS")
                                    .message("Search Products Success")
                                    .results(productsResponse)
                                    .build()
                    );


        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message(e.getMessage())
                                    .results("")
                                    .build()
                    );
        }
    }

    @Override
    public ResponseEntity<?> editProductById(Long productId, ProductRequestEdit pRequestEdit) {
        try {
            if(productRepository.existsById(productId)){
                ProductEntity product = productRepository.findById(productId)
                        .orElseThrow(NoSuchElementException::new);
                product.setProductName(pRequestEdit.getProductName());
                product.setDescription(pRequestEdit.getDescription());
                productRepository.save(product);
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(200))
                        .body(
                                ResponseObject
                                        .builder()
                                        .status("SUCCESS")
                                        .message("Product was Updated")
                                        .build()
                        );
            }

        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("FAIL")
                                    .message("Product Not Exist!")
                                    .build()
                    );
        }
        return null;
    }

    @Override
    public ResponseEntity<?> editProductDetailsById(Long productId) {
        return null;
    }
    @Transactional
    @Override
    public ResponseEntity<?> removeProductById(Long productId) {
        if(productRepository.existsById(productId)){
            ProductEntity productEntity = productRepository.findById(productId)
                    .orElseThrow(NoSuchElementException::new);
            productEntity.setStatus(false);
            productEntity.getProductItemList().stream()
                            .forEach(productItemEntity -> {
                                productItemEntity.setStatus(false);
                                itemRepository.save(productItemEntity);
                            });
            productRepository.save(productEntity);
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(
                            ResponseObject
                                    .builder()
                                    .status("SUCCESS")
                                    .message("Product is Removed")
                                    .build()
                    );
        }
        return ResponseEntity
                .status(HttpStatusCode.valueOf(404))
                .body(
                        ResponseObject
                                .builder()
                                .status("FAIL")
                                .message("Product Not Exist!")
                                .build()
                );
    }

    private List<ProductResponseDTO> mappingProductEntityListToProductDTOs(Page<ProductEntity> productEntities){
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for(ProductEntity productEntity: productEntities){
            List<ProductItemEntity> productItemEntities = productEntity.getProductItemList();

            List<ProductItemResponseDTO> productItemResponseDTOList = new ArrayList<>();
            for(ProductItemEntity productItemEntity : productItemEntities){

                mappingSpecialImg_OptionWithProductItem(productItemResponseDTOList, productItemEntity);
            }
            ProductResponseDTO productResponseDTO = ProductResponseDTO
                    .builder()
                    .productId(productEntity.getProductId())
                    .productName(productEntity.getProductName())
                    .description(productEntity.getDescription())
                    .status(productEntity.getStatus())
                    .productItemResponseList(productItemResponseDTOList)
                    .build();
            productResponseDTOList.add(productResponseDTO);
        }
        return productResponseDTOList;
    }
    private List<ProductResponseDTO> mappingProductEntityListToProductDTOs(List<ProductEntity> productEntities){
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for(ProductEntity productEntity: productEntities){
            List<ProductItemEntity> productItemEntities = productEntity.getProductItemList();

            List<ProductItemResponseDTO> productItemResponseDTOList = new ArrayList<>();
            for(ProductItemEntity productItemEntity : productItemEntities){

                mappingSpecialImg_OptionWithProductItem(productItemResponseDTOList, productItemEntity);
            }
            ProductResponseDTO productResponseDTO = ProductResponseDTO
                    .builder()
                    .productId(productEntity.getProductId())
                    .productName(productEntity.getProductName())
                    .description(productEntity.getDescription())
                    .status(productEntity.getStatus())
                    .productItemResponseList(productItemResponseDTOList)
                    .build();
            productResponseDTOList.add(productResponseDTO);
        }
        return productResponseDTOList;
    }
}