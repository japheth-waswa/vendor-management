package com.vendor.management.system.stock.service.domain;

import com.vendor.management.system.domain.exception.DomainException;
import com.vendor.management.system.domain.valueobject.*;
import com.vendor.management.system.outbox.OutboxStatus;
import com.vendor.management.system.outbox.model.OrderOutboxMessage;
import com.vendor.management.system.saga.SagaStatus;
import com.vendor.management.system.stock.service.domain.dto.order.*;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderListResponse;
import com.vendor.management.system.stock.service.domain.dto.order.response.OrderResponse;
import com.vendor.management.system.stock.service.domain.dto.product.*;
import com.vendor.management.system.stock.service.domain.dto.productcategory.*;
import com.vendor.management.system.stock.service.domain.entity.Order;
import com.vendor.management.system.stock.service.domain.entity.Product;
import com.vendor.management.system.stock.service.domain.entity.ProductCategory;
import com.vendor.management.system.stock.service.domain.exception.OrderNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.ProductCategoryNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.ProductNotFoundException;
import com.vendor.management.system.stock.service.domain.exception.StockDomainException;
import com.vendor.management.system.stock.service.domain.mapper.OrderDataMapper;
import com.vendor.management.system.stock.service.domain.mapper.ProductCategoryDataMapper;
import com.vendor.management.system.stock.service.domain.mapper.ProductDataMapper;
import com.vendor.management.system.stock.service.domain.ports.input.service.StockApplicationService;
import com.vendor.management.system.stock.service.domain.ports.output.repository.*;
import com.vendor.management.system.stock.service.domain.valueobject.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.vendor.management.system.domain.util.DomainConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = StockTestConfiguration.class)
public class StockApplicationServiceTest {

    @Autowired
    private StockApplicationService stockApplicationService;
    @Autowired
    private ProductCategoryDataMapper productCategoryDataMapper;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductDataMapper productDataMapper;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderDataMapper orderDataMapper;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderFileOutboxRepository orderFileOutboxRepository;
    @Autowired
    OrderFinanceOutboxRepository orderFinanceOutboxRepository;

    //product category
    private CreateProductCategoryCommand createProductCategoryCommand;
    private CreateProductCategoryCommand createProductCategoryCommandWrongNameNull;
    private CreateProductCategoryCommand createProductCategoryCommandWrongNameBlank;

    private UpdateProductCategoryCommand updateProductCategoryCommand;
    private UpdateProductCategoryCommand updateProductCategoryCommandWrongProductCategoryId;
    private UpdateProductCategoryCommand updateProductCategoryCommandWrongNameNull;
    private UpdateProductCategoryCommand updateProductCategoryCommandWrongNameBlank;

    private DeleteProductCategoryCommand deleteProductCategoryCommand;
    private DeleteProductCategoryCommand deleteProductCategoryCommandWrongProductCategoryId;

    //product
    private CreateProductCommand createProductCommand;
    private CreateProductCommand createProductCommandInvalidCategoryId;
    private CreateProductCommand createProductCommandWrongCategoryId;
    private CreateProductCommand createProductCommandWrongName;
    private CreateProductCommand createProductCommandWrongUnitPrice;
    private CreateProductCommand createProductCommandWrongStatus;

    private UpdateProductCommand updateProductCommand;
    private UpdateProductCommand updateProductCommandProductNotFound;
    private UpdateProductCommand updateProductCommandWrongProductId;
    private UpdateProductCommand updateProductCommandWrongName;
    private UpdateProductCommand updateProductCommandWrongVendorId;
    private UpdateProductCommand updateProductCommandWrongCategoryId;
    private UpdateProductCommand updateProductCommandInvalidCategoryId;

    private DeleteProductCommand deleteProductCommand;
    private DeleteProductCommand deleteProductCommandWrongProductId;
    private DeleteProductCommand deleteProductCommandWrongVendorId;

    //order
    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandSingle;
    private CreateOrderCommand createOrderCommandMultipleSameProduct;
    private CreateOrderCommand createOrderCommandNullItems;
    private CreateOrderCommand createOrderCommandEmptyItems;
    private CreateOrderCommand createOrderCommandWrongProduct;
    private CreateOrderCommand createOrderCommandInvalidQuantity;
    private ModifyOrderCommand modifyOrderCommandNewProduct;
    private ModifyOrderCommand modifyOrderCommandNewInvalidOrder;
    private ModifyOrderCommand modifyOrderCommandNewInvalidProduct;
    private ModifyOrderCommand modifyOrderCommandNewProductWrongQuantity;
    private ModifyOrderCommand modifyOrderCommandSameProductNewQuantity;
    private ModifyOrderCommand modifyOrderCommandSameProductWrongQuantity;
    private ModifyOrderCommand modifyOrderCommandInvalidOrderState;
    private ModifyOrderCommand modifyOrderCommandInvalidProductState;

    private DeleteOrderProductCommand deleteOrderProductCommand;
    private DeleteOrderProductCommand deleteOrderProductCommandWrongOrder;
    private DeleteOrderProductCommand deleteOrderProductNotFoundProduct;
    private DeleteOrderProductCommand deleteOrderProductWrongProduct;
    private DeleteOrderItemCommand deleteOrderItemCommand;
    private DeleteOrderItemCommand deleteOrderItemCommandWrongOrder;
    private DeleteOrderItemCommand deleteOrderItemCommandWrongOrderItem;

    private SettleOrderCommand settleOrderCommand;
    private SettleOrderCommand settleOrderCommandInvalidOrder;
    private SettleOrderCommand settleOrderCommandInvalidStatus;

    private CancelOrderCommand cancelOrderCommand;
    private CancelOrderCommand cancelOrderCommandInvalidOrder;
    private CancelOrderCommand cancelOrderCommandInvalidStatus;
    private CancelOrderCommand cancelOrderCommandNullMessages;
    private CancelOrderCommand cancelOrderCommandEmptyMessages;

    private DeleteOrderCommand deleteOrderCommand;
    private DeleteOrderCommand deleteOrderCommandInvalidOrder;
    private DeleteOrderCommand deleteOrderCommandInvalidStatus;
    private DeleteOrderCommand deleteOrderCommandNullMessages;
    private DeleteOrderCommand deleteOrderCommandEmptyMessages;

    private final UUID VENDOR_ID_1 = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    private final UUID VENDOR_ID_2 = UUID.fromString("5371d458-b310-4bc6-a448-e44c589f56f3");
    private final UUID VENDOR_ID_3 = UUID.fromString("1769154d-4c99-41e0-aafd-9701c1d893e7");
    private final UUID VENDOR_ID_4 = UUID.fromString("0b314d2d-859e-4105-b895-c93e0e5a45ab");
    private final UUID VENDOR_ID_5 = UUID.fromString("ef5736e4-3a34-4968-8915-0622a842d799");
    private final UUID VENDOR_ID_6 = UUID.fromString("1a528569-607a-484c-8e07-996be031a125");

    private final UUID PRODUCT_CATEGORY_ID = UUID.fromString("6bddb84d-f273-4f34-b0e2-c680c63a98a1");
    private final UUID PRODUCT_CATEGORY_ID_1 = UUID.fromString("52d31578-0c0d-4d47-a0c4-aa96899eb0fe");
    private final UUID PRODUCT_CATEGORY_ID_2 = UUID.fromString("6b2ef9d0-1d3b-4445-867d-9be619392ce7");
    private final UUID PRODUCT_CATEGORY_ID_3 = UUID.fromString("50b2f65d-3b27-46d7-b57e-146c95c4938f");
    private final String PRODUCT_CATEGORY_NAME = "fashion";
    private final String PRODUCT_CATEGORY_NAME_1 = "electronics";
    private final String PRODUCT_CATEGORY_NAME_2 = "shoes";
    private final String PRODUCT_CATEGORY_NAME_3 = "furniture";

    private final UUID PRODUCT_ID_1 = UUID.fromString("81eccb9c-38ba-4b52-bbbc-03aabdf8f779");
    private final UUID PRODUCT_ID_2 = UUID.fromString("7c8e38d5-972e-4414-8aac-fac149599cfe");
    private final UUID PRODUCT_ID_3 = UUID.fromString("129e3edf-0371-459b-9dcb-ee1d2d6a36a4");
    private final UUID PRODUCT_ID_4 = UUID.fromString("4d727926-289b-44f2-8074-12584ad316ca");
    private final UUID PRODUCT_ID_5 = UUID.fromString("cec5ddab-b6a2-418c-aa77-ec6ac61c8b6f");
    private final UUID PRODUCT_ID_6 = UUID.fromString("3828c7d4-8d4d-4838-b94d-c7cc16398bca");

    private final String PRODUCT_NAME_1 = "prod 1";
    private final String PRODUCT_NAME_2 = "prod 2";
    private final String PRODUCT_NAME_3 = "prod 3";
    private final String PRODUCT_NAME_4 = "prod 4";

    private final UUID CUSTOMER_ID_1 = UUID.fromString("6ae1e36a-d698-4032-bb2c-db0fc99c33ec");
    private final UUID ORDER_ID_1 = UUID.fromString("8536e131-787c-4257-af81-8f10bf3b5045");
    private final UUID ORDER_ID_2 = UUID.fromString("a07a66f5-7be0-4bc3-aeb4-cb45f285dc62");
    private final UUID ORDER_ID_3 = UUID.fromString("74b6c87d-fea2-40c5-af2d-e15280911590");
    private final UUID ORDER_ID_4 = UUID.fromString("182ee825-b5c5-4a46-849a-37377add8c14");
    private final UUID ORDER_ID_5 = UUID.fromString("283b23b1-b76d-43f9-9ec1-980331814714");
    private final UUID ORDER_ID_6 = UUID.fromString("5ce985ae-d186-422b-9dea-34ad9a49bbb7");
    private final UUID ORDER_ID_7 = UUID.fromString("e834d40a-7bc3-4ab1-81d4-f7343ee3d1b1");
    private final UUID ORDER_ID_8 = UUID.fromString("b0fcd134-2e11-48f4-89c2-b3d8588790e0");
    BigDecimal orderPriceTotal;

    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 10;
    private List<AbstractSortPayload<SortDirection, OrderSortField>> orderSortList;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSx");
    private final ZonedDateTime UPDATED_AT = ZonedDateTime.parse("2024-05-19 07:32:48.490179+00", formatter);
    private final ZonedDateTime CREATED_AT = ZonedDateTime.parse("2024-05-19 07:02:24.445686+00", formatter);

    @BeforeAll
    public void init() {
        //product category
        createProductCategoryCommand = CreateProductCategoryCommand.builder()
                .name(PRODUCT_CATEGORY_NAME)
                .build();
        createProductCategoryCommandWrongNameNull = CreateProductCategoryCommand.builder()
                .build();
        createProductCategoryCommandWrongNameBlank = CreateProductCategoryCommand.builder()
                .name(" ")
                .build();
        updateProductCategoryCommand = UpdateProductCategoryCommand.builder()
                .productCategoryId(PRODUCT_CATEGORY_ID)
                .name(PRODUCT_CATEGORY_NAME)
                .build();
        updateProductCategoryCommandWrongProductCategoryId = UpdateProductCategoryCommand.builder()
                .name(PRODUCT_CATEGORY_NAME)
                .build();
        updateProductCategoryCommandWrongNameNull = UpdateProductCategoryCommand.builder()
                .productCategoryId(PRODUCT_CATEGORY_ID)
                .build();
        updateProductCategoryCommandWrongNameBlank = UpdateProductCategoryCommand.builder()
                .productCategoryId(PRODUCT_CATEGORY_ID)
                .name(" ")
                .build();
        deleteProductCategoryCommand = DeleteProductCategoryCommand.builder()
                .productCategoryId(PRODUCT_CATEGORY_ID)
                .build();
        deleteProductCategoryCommandWrongProductCategoryId = DeleteProductCategoryCommand.builder()
                .build();


        ProductCategory productCategorySave = productCategoryDataMapper.transformCreateProductCategoryCommandToProductCategory(new VendorId(VENDOR_ID_1), createProductCategoryCommand);
        productCategorySave.setId(new ProductCategoryId(PRODUCT_CATEGORY_ID));

        ProductCategory productCategoryDb = ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID))
                .productCategoryName(new ProductCategoryName(PRODUCT_CATEGORY_NAME))
                .vendorId(new VendorId(VENDOR_ID_1))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        ProductCategory productCategoryDb_1 = ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_1))
                .productCategoryName(new ProductCategoryName(PRODUCT_CATEGORY_NAME_1))
                .vendorId(new VendorId(VENDOR_ID_1))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        ProductCategory productCategoryDb_2 = ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_2))
                .productCategoryName(new ProductCategoryName(PRODUCT_CATEGORY_NAME_2))
                .vendorId(new VendorId(VENDOR_ID_1))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        ProductCategory productCategoryDb_3 = ProductCategory.builder()
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_3))
                .productCategoryName(new ProductCategoryName(PRODUCT_CATEGORY_NAME_3))
                .vendorId(new VendorId(VENDOR_ID_2))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        when(productCategoryRepository.save(any(ProductCategory.class))).thenReturn(productCategorySave);
        when(productCategoryRepository.update(any(ProductCategory.class))).thenReturn(productCategoryDb);
        when(productCategoryRepository.findByIdAndVendorId(new ProductCategoryId(PRODUCT_CATEGORY_ID), new VendorId(VENDOR_ID_1))).thenReturn(Optional.of(productCategoryDb));
        when(productCategoryRepository.findByIdAndVendorId(new ProductCategoryId(PRODUCT_CATEGORY_ID_1), new VendorId(VENDOR_ID_1))).thenReturn(Optional.of(productCategoryDb_1));
        when(productCategoryRepository.findByIdAndVendorId(new ProductCategoryId(PRODUCT_CATEGORY_ID_3), new VendorId(VENDOR_ID_2))).thenReturn(Optional.of(productCategoryDb_3));
        when(productCategoryRepository.findAll(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE,
                List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductCategorySortField.NAME))))
                .thenReturn(Optional.of(List.of(productCategoryDb, productCategoryDb_1, productCategoryDb_2)));
        when(productCategoryRepository.findAll(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(productCategoryDb, productCategoryDb_2)));

        //product
        createProductCommand = CreateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.ACTIVE)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        createProductCommandWrongCategoryId = CreateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_2)
                .status(ProductStatus.ACTIVE)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        createProductCommandInvalidCategoryId = CreateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_3)
                .status(ProductStatus.ACTIVE)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        createProductCommandWrongName = CreateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.ACTIVE)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        createProductCommandWrongUnitPrice = CreateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.ACTIVE)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(0.45))
                .quantity(200)
                .build();
        createProductCommandWrongStatus = CreateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        updateProductCommand = UpdateProductCommand.builder()
                .productId(PRODUCT_ID_2)
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.INACTIVE)
                .name(PRODUCT_NAME_2)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        updateProductCommandProductNotFound = UpdateProductCommand.builder()
                .productId(PRODUCT_ID_3)
                .categoryId(PRODUCT_CATEGORY_ID_2)
                .status(ProductStatus.INACTIVE)
                .name(PRODUCT_NAME_2)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        updateProductCommandInvalidCategoryId = UpdateProductCommand.builder()
                .productId(PRODUCT_ID_2)
                .categoryId(UUID.fromString("7455f65d-7e04-449e-a275-f3f1ed507170"))
                .status(ProductStatus.INACTIVE)
                .name(PRODUCT_NAME_2)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        updateProductCommandWrongProductId = UpdateProductCommand.builder()
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.ACTIVE)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        updateProductCommandWrongName = UpdateProductCommand.builder()
                .productId(PRODUCT_ID_1)
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.ACTIVE)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        updateProductCommandWrongVendorId = UpdateProductCommand.builder()
                .productId(PRODUCT_ID_3)
                .categoryId(PRODUCT_CATEGORY_ID_1)
                .status(ProductStatus.ACTIVE)
                .name(PRODUCT_NAME_1)
                .description("basic description 1")
                .price(BigDecimal.valueOf(23.67))
                .quantity(200)
                .build();
        deleteProductCommand = DeleteProductCommand.builder()
                .productId(PRODUCT_ID_1)
                .build();
        deleteProductCommandWrongProductId = DeleteProductCommand.builder()
                .build();
        deleteProductCommandWrongVendorId = DeleteProductCommand.builder()
                .productId(PRODUCT_ID_1)
                .build();

        Product product_vendor_1_db_1 = Product.builder()
                .vendorId(new VendorId(VENDOR_ID_1))
                .productId(new ProductId(PRODUCT_ID_1))
                .productName(new ProductName(PRODUCT_NAME_1))
                .productDescription(new ProductDescription("rand desc 1"))
                .productStatus(ProductStatus.ACTIVE)
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_1))
                .unitPrice(new Money(BigDecimal.valueOf(23.56)))
                .quantity(new Quantity(20))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        Product product_vendor_1_db_2 = Product.builder()
                .vendorId(new VendorId(VENDOR_ID_1))
                .productId(new ProductId(PRODUCT_ID_2))
                .productName(new ProductName(PRODUCT_NAME_2))
                .productDescription(new ProductDescription("rand desc 2"))
                .productStatus(ProductStatus.ACTIVE)
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_1))
                .unitPrice(new Money(BigDecimal.valueOf(34.78)))
                .quantity(new Quantity(30))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        Product product_vendor_1_db_3 = Product.builder()
                .vendorId(new VendorId(VENDOR_ID_1))
                .productId(new ProductId(PRODUCT_ID_3))
                .productName(new ProductName(PRODUCT_NAME_3))
                .productDescription(new ProductDescription("rand desc 3"))
                .productStatus(ProductStatus.ACTIVE)
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_2))
                .unitPrice(new Money(BigDecimal.valueOf(4.23)))
                .quantity(new Quantity(70))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        Product product_vendor_1_db_inactive = Product.builder()
                .vendorId(new VendorId(VENDOR_ID_1))
                .productId(new ProductId(PRODUCT_ID_4))
                .productName(new ProductName(PRODUCT_NAME_4))
                .productDescription(new ProductDescription("rand desc 2"))
                .productStatus(ProductStatus.INACTIVE)
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_3))
                .unitPrice(new Money(BigDecimal.valueOf(89.12)))
                .quantity(new Quantity(50))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();
        Product product_vendor_1_db_5 = Product.builder()
                .vendorId(new VendorId(VENDOR_ID_1))
                .productId(new ProductId(PRODUCT_ID_5))
                .productName(new ProductName("tcl tv"))
                .productDescription(new ProductDescription("rand desc 1"))
                .productStatus(ProductStatus.ACTIVE)
                .productCategoryId(new ProductCategoryId(PRODUCT_CATEGORY_ID_1))
                .unitPrice(new Money(BigDecimal.valueOf(23.56)))
                .quantity(new Quantity(20))
                .updatedAt(new UpdatedAt(UPDATED_AT))
                .createdAt(new CreatedAt(CREATED_AT))
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(product_vendor_1_db_1);
        when(productRepository.update(any(Product.class))).thenReturn(product_vendor_1_db_1);
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_1), new VendorId(VENDOR_ID_1))).thenReturn(Optional.of(product_vendor_1_db_1));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_1), new VendorId(VENDOR_ID_3))).thenReturn(Optional.of(product_vendor_1_db_1));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_1), new VendorId(VENDOR_ID_4))).thenReturn(Optional.of(product_vendor_1_db_1));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_1), new VendorId(VENDOR_ID_5))).thenReturn(Optional.of(product_vendor_1_db_1));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_2), new VendorId(VENDOR_ID_1))).thenReturn(Optional.of(product_vendor_1_db_2));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_2), new VendorId(VENDOR_ID_4))).thenReturn(Optional.of(product_vendor_1_db_2));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_2), new VendorId(VENDOR_ID_5))).thenReturn(Optional.of(product_vendor_1_db_2));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_3), new VendorId(VENDOR_ID_2))).thenReturn(Optional.of(product_vendor_1_db_3));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_4), new VendorId(VENDOR_ID_1))).thenReturn(Optional.of(product_vendor_1_db_inactive));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_5), new VendorId(VENDOR_ID_4))).thenReturn(Optional.of(product_vendor_1_db_5));
        when(productRepository.findByIdAndVendorId(new ProductId(PRODUCT_ID_5), new VendorId(VENDOR_ID_5))).thenReturn(Optional.of(product_vendor_1_db_5));
        when(productRepository.findAll(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(product_vendor_1_db_1, product_vendor_1_db_2)));
        when(productRepository.findAll(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE,
                List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductSortField.NAME))))
                .thenReturn(Optional.of(List.of(product_vendor_1_db_1, product_vendor_1_db_2, product_vendor_1_db_3)));
      when(productRepository.findAllByName(new VendorId(VENDOR_ID_1),new ProductName(PRODUCT_NAME_1), ProductStatus.ACTIVE,PAGE_NUMBER, PAGE_SIZE,
                List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductSortField.NAME))))
                .thenReturn(Optional.of(List.of(product_vendor_1_db_1, product_vendor_1_db_2, product_vendor_1_db_3)));

        //order

        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(2)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_2)
                                .quantity(3)
                                .price(BigDecimal.valueOf(50.23))
                                .subTotal(BigDecimal.valueOf(150.69))
                                .build()
                )).build();
        createOrderCommandSingle = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(2)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build()
                )).build();
        createOrderCommandNullItems = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(null).build();
        createOrderCommandEmptyItems = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(Collections.emptyList()).build();
        createOrderCommandMultipleSameProduct = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(2)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_2)
                                .quantity(3)
                                .price(BigDecimal.valueOf(50.23))
                                .subTotal(BigDecimal.valueOf(150.69))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(12)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build()
                )).build();
        createOrderCommandWrongProduct = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(2)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_2)
                                .quantity(3)
                                .price(BigDecimal.valueOf(50.23))
                                .subTotal(BigDecimal.valueOf(150.69))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_3)
                                .quantity(12)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build()
                )).build();
        createOrderCommandInvalidQuantity = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID_1)
                .items(List.of(
                        OrderItem.builder()
                                .productId(PRODUCT_ID_1)
                                .quantity(1)
                                .price(BigDecimal.valueOf(100.32))
                                .subTotal(BigDecimal.valueOf(200.64))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID_2)
                                .quantity(3000)
                                .price(BigDecimal.valueOf(50.23))
                                .subTotal(BigDecimal.valueOf(150.69))
                                .build()
                )).build();

        Order order = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_1), createOrderCommand);
        order.initializeOrder();
        order.setId(new OrderId(ORDER_ID_1));
        orderPriceTotal = order.getPrice().getAmount();

        Order orderSingle = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_1), createOrderCommandSingle);
        orderSingle.initializeOrder();
        orderSingle.setId(new OrderId(ORDER_ID_1));

        Order orderSingleInvalidState = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_1), createOrderCommandSingle);
        orderSingleInvalidState.initializeOrder();
        orderSingleInvalidState.setId(new OrderId(ORDER_ID_1));
        orderSingleInvalidState.settle();

        //vendor 4
        Order orderVendor4 = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_4), createOrderCommand);
        orderVendor4.initializeOrder();
        orderVendor4.setId(new OrderId(ORDER_ID_1));
        Order orderSingleVendor4 = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_4), createOrderCommandSingle);
        orderSingleVendor4.initializeOrder();
        orderSingleVendor4.setId(new OrderId(ORDER_ID_1));

        //vendor 5
        Order orderVendor5 = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_5), createOrderCommand);
        orderVendor5.initializeOrder();
        orderVendor5.setId(new OrderId(ORDER_ID_1));
        Order orderSingleVendor5 = orderDataMapper.transformCreateOrderCommandToOrder(new VendorId(VENDOR_ID_5), createOrderCommandSingle);
        orderSingleVendor5.initializeOrder();
        orderSingleVendor5.setId(new OrderId(ORDER_ID_1));

        //vendor 4 for order status
        Order orderSettleDelete = Order.builder()
                .orderId(new OrderId(ORDER_ID_3))
                .vendorId(new VendorId(VENDOR_ID_4))
                .customerId(new CustomerId(CUSTOMER_ID_1))
                .orderStatus(OrderStatus.PENDING)
                .price(new Money(BigDecimal.valueOf(23.89)))
                .build();
        Order orderSettleDeleteInvalidStatus = Order.builder()
                .orderId(new OrderId(ORDER_ID_4))
                .vendorId(new VendorId(VENDOR_ID_4))
                .customerId(new CustomerId(CUSTOMER_ID_1))
                .orderStatus(OrderStatus.SETTLED)
                .price(new Money(BigDecimal.valueOf(23.89)))
                .build();
        Order orderCancel = Order.builder()
                .orderId(new OrderId(ORDER_ID_5))
                .vendorId(new VendorId(VENDOR_ID_4))
                .customerId(new CustomerId(CUSTOMER_ID_1))
                .orderStatus(OrderStatus.SETTLED)
                .price(new Money(BigDecimal.valueOf(23.89)))
                .build();
        Order orderCancelInvalidStatus = Order.builder()
                .orderId(new OrderId(ORDER_ID_6))
                .vendorId(new VendorId(VENDOR_ID_4))
                .customerId(new CustomerId(CUSTOMER_ID_1))
                .orderStatus(OrderStatus.CANCELLED)
                .price(new Money(BigDecimal.valueOf(23.89)))
                .build();
        Order orderCancelEmptyMessages = Order.builder()
                .orderId(new OrderId(ORDER_ID_7))
                .vendorId(new VendorId(VENDOR_ID_4))
                .customerId(new CustomerId(CUSTOMER_ID_1))
                .orderStatus(OrderStatus.SETTLED)
                .price(new Money(BigDecimal.valueOf(23.89)))
                .build();
        Order orderDeleteEmptyMessages = Order.builder()
                .orderId(new OrderId(ORDER_ID_8))
                .vendorId(new VendorId(VENDOR_ID_4))
                .customerId(new CustomerId(CUSTOMER_ID_1))
                .orderStatus(OrderStatus.PENDING)
                .price(new Money(BigDecimal.valueOf(23.89)))
                .build();

        modifyOrderCommandNewProduct = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_2)
                .quantity(10)
                .build();
        modifyOrderCommandNewInvalidOrder = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_2)
                .productId(PRODUCT_ID_2)
                .quantity(5)
                .build();
        modifyOrderCommandNewInvalidProduct = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_6)
                .quantity(3)
                .build();
        modifyOrderCommandNewProductWrongQuantity = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_2)
                .quantity(150)
                .build();
        modifyOrderCommandSameProductNewQuantity = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_1)
                .quantity(7)
                .build();
        modifyOrderCommandSameProductWrongQuantity = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_1)
                .quantity(700)
                .build();
        modifyOrderCommandInvalidOrderState = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_1)
                .quantity(8)
                .build();
        modifyOrderCommandInvalidProductState = ModifyOrderCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_4)
                .quantity(6)
                .build();

        deleteOrderProductCommand = DeleteOrderProductCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_2)
                .build();
        deleteOrderProductCommandWrongOrder = DeleteOrderProductCommand.builder()
                .orderId(ORDER_ID_2)
                .productId(PRODUCT_ID_2)
                .build();
        deleteOrderProductNotFoundProduct = DeleteOrderProductCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_3)
                .build();
        deleteOrderProductWrongProduct = DeleteOrderProductCommand.builder()
                .orderId(ORDER_ID_1)
                .productId(PRODUCT_ID_5)
                .build();
        deleteOrderItemCommand = DeleteOrderItemCommand.builder()
                .orderId(ORDER_ID_1)
                .orderItemId(2L)
                .build();
        deleteOrderItemCommandWrongOrder = DeleteOrderItemCommand.builder()
                .orderId(ORDER_ID_2)
                .orderItemId(2L)
                .build();
        deleteOrderItemCommandWrongOrderItem = DeleteOrderItemCommand.builder()
                .orderId(ORDER_ID_1)
                .orderItemId(3L)
                .build();

        settleOrderCommand = SettleOrderCommand.builder()
                .orderId(ORDER_ID_3)
                .build();
        settleOrderCommandInvalidOrder = SettleOrderCommand.builder()
                .orderId(UUID.fromString("8002a546-1955-41f3-8194-6c28936937d6"))
                .build();
        settleOrderCommandInvalidStatus = SettleOrderCommand.builder()
                .orderId(ORDER_ID_4)
                .build();

        cancelOrderCommand = CancelOrderCommand.builder()
                .orderId(ORDER_ID_5)
                .messages(List.of("message 1", "message 2"))
                .build();
        cancelOrderCommandInvalidOrder = CancelOrderCommand.builder()
                .orderId(UUID.fromString("fd3e7823-5b0b-42d4-af68-817ee6816511"))
                .messages(List.of("message 1", "message 2"))
                .build();
        cancelOrderCommandInvalidStatus = CancelOrderCommand.builder()
                .orderId(ORDER_ID_6)
                .messages(List.of("message 1", "message 2"))
                .build();
        cancelOrderCommandNullMessages = CancelOrderCommand.builder()
                .orderId(ORDER_ID_5)
                .messages(null)
                .build();
        cancelOrderCommandEmptyMessages = CancelOrderCommand.builder()
                .orderId(ORDER_ID_7)
                .messages(Collections.emptyList())
                .build();

        deleteOrderCommand = DeleteOrderCommand.builder()
                .orderId(ORDER_ID_3)
                .messages(List.of("message 1", "message 2"))
                .build();
        deleteOrderCommandInvalidOrder = DeleteOrderCommand.builder()
                .orderId(UUID.fromString("59a16d02-1adc-44cc-9ceb-3ed75879318d"))
                .messages(List.of("message 1", "message 2"))
                .build();
        deleteOrderCommandInvalidStatus = DeleteOrderCommand.builder()
                .orderId(ORDER_ID_4)
                .messages(List.of("message 1", "message 2"))
                .build();
        deleteOrderCommandNullMessages = DeleteOrderCommand.builder()
                .orderId(ORDER_ID_8)
                .messages(null)
                .build();
        deleteOrderCommandEmptyMessages = DeleteOrderCommand.builder()
                .orderId(ORDER_ID_8)
                .messages(Collections.emptyList())
                .build();

        orderSortList = List.of(new AbstractSortPayload<>(SortDirection.DESC, OrderSortField.CREATED_AT));

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderRepository.update(any(Order.class))).thenReturn(order);

        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_1)))
                .thenReturn(Optional.of(orderSingle));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderVendor4));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_5)))
                .thenReturn(Optional.of(orderVendor5));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_3)))
                .thenReturn(Optional.of(orderSingleInvalidState));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_3), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderSettleDelete));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_4), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderSettleDeleteInvalidStatus));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_5), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderCancel));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_6), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderCancelInvalidStatus));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_7), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderCancelEmptyMessages));
        when(orderRepository.findByIdAndVendorId(new OrderId(ORDER_ID_8), new VendorId(VENDOR_ID_4)))
                .thenReturn(Optional.of(orderDeleteEmptyMessages));

        when(orderRepository.deleteOrderItem(orderSingleVendor4, new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_4), new ProductId(PRODUCT_ID_2)))
                .thenReturn(Optional.of(orderSingleVendor4));
        when(orderRepository.deleteOrderItem(orderSingleVendor4, new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_4), new OrderItemId(2L)))
                .thenReturn(Optional.of(orderSingleVendor4));

        when(orderRepository.deleteOrderItem(orderSingleVendor5, new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_5), new ProductId(PRODUCT_ID_2)))
                .thenReturn(Optional.of(orderSingleVendor5));
        when(orderRepository.deleteOrderItem(orderSingleVendor5, new OrderId(ORDER_ID_1), new VendorId(VENDOR_ID_5), new OrderItemId(2L)))
                .thenReturn(Optional.of(orderSingleVendor5));

        when(orderRepository.findAll(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE))
                .thenReturn(Optional.of(List.of(order, orderSingle, orderSingleInvalidState)));
        when(orderRepository.findAll(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE, orderSortList))
                .thenReturn(Optional.of(List.of(orderSingleInvalidState, orderVendor4)));

        OrderOutboxMessage outboxMessage = OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sagaId(UUID.randomUUID())
                .createdAt(ZonedDateTime.now(ZoneId.of(UTC)))
                .type("OrderProcessing")
                .payload("")
                .orderStatus(OrderStatus.SETTLED)
                .sagaStatus(SagaStatus.SUCCEEDED)
                .outboxStatus(OutboxStatus.FAILED)
                .version(3)
                .build();

        when(orderFileOutboxRepository.save(any(OrderOutboxMessage.class))).thenReturn(outboxMessage);
        when(orderFinanceOutboxRepository.save(any(OrderOutboxMessage.class))).thenReturn(outboxMessage);
    }

    @Test
    public void testCreateProductCategory() {
        ProductCategoryResponse productCategoryResponse = stockApplicationService.createProductCategory(new VendorId(VENDOR_ID_1), createProductCategoryCommand);
        assertEquals(PRODUCT_CATEGORY_NAME, productCategoryResponse.getName());
    }

    @Test
    public void testCreateProductCategoryWrongNameNull() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createProductCategory(new VendorId(VENDOR_ID_1), createProductCategoryCommandWrongNameNull));
    }

    @Test
    public void testCreateProductCategoryWrongNameBlank() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.createProductCategory(new VendorId(VENDOR_ID_1), createProductCategoryCommandWrongNameBlank));
        assertEquals("Product category name is required", stockDomainException.getMessage());
    }

    @Test
    public void testCreateProductCategoryNullVendorId() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createProductCategory(null, createProductCategoryCommand));
    }

    @Test
    public void testCreateProductCategoryVendorIdValueNull() {
        DomainException domainException = assertThrows(DomainException.class, () -> stockApplicationService.createProductCategory(new VendorId(null), createProductCategoryCommand));
        assertEquals("Vendor id is required!", domainException.getMessage());
    }

    @Test
    public void testUpdateProductCategory() {
        ProductCategoryResponse productCategoryResponse = stockApplicationService.updateProductCategory(new VendorId(VENDOR_ID_1), updateProductCategoryCommand);
        assertEquals(PRODUCT_CATEGORY_NAME, productCategoryResponse.getName());
        assertEquals(PRODUCT_CATEGORY_ID, productCategoryResponse.getProductCategoryId());
    }

    @Test
    public void testUpdateProductCategoryNotFound() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class, () -> stockApplicationService.updateProductCategory(new VendorId(VENDOR_ID_2), updateProductCategoryCommand));
        assertEquals("Product category not found!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void testUpdateProductCategoryCommandWrongProductCategoryId() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.updateProductCategory(new VendorId(VENDOR_ID_1), updateProductCategoryCommandWrongProductCategoryId));
    }

    @Test
    public void testUpdateProductCategoryCommandWrongNameNull() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.updateProductCategory(new VendorId(VENDOR_ID_1), updateProductCategoryCommandWrongNameNull));
    }

    @Test
    public void testUpdateProductCategoryCommandWrongNameBlank() {
        DomainException domainException = assertThrows(DomainException.class, () -> stockApplicationService.updateProductCategory(new VendorId(VENDOR_ID_1), updateProductCategoryCommandWrongNameBlank));
        assertEquals("Product category name is required", domainException.getMessage());
    }

    @Test
    public void testDeleteProductCategory() {
        ProductCategoryResponse productCategoryResponse = stockApplicationService.deleteProductCategory(new VendorId(VENDOR_ID_1), deleteProductCategoryCommand);
        assertEquals(PRODUCT_CATEGORY_ID, productCategoryResponse.getProductCategoryId());
    }

    @Test
    public void testDeleteProductCategoryNotFound() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class, () -> stockApplicationService.deleteProductCategory(new VendorId(VENDOR_ID_2), deleteProductCategoryCommand));
        assertEquals("Product category not found!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void testDeleteProductCategoryCommandWrongProductCategoryId() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.deleteProductCategory(new VendorId(VENDOR_ID_1), deleteProductCategoryCommandWrongProductCategoryId));
    }

    @Test
    public void testFetchProductCategoriesWithSort() {
        ProductCategoryListResponse productCategories = stockApplicationService.fetchCategories(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE,
                List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductCategorySortField.NAME)));
        assertEquals(3, productCategories.getList().size());
    }

    @Test
    public void testFetchProductCategoriesWithSortNotFound() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class,
                () -> stockApplicationService.fetchCategories(new VendorId(VENDOR_ID_2), PAGE_NUMBER, PAGE_SIZE, List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductCategorySortField.NAME))));
        assertEquals("Product categories not found!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void testFetchProductCategoriesWithSortInvalidPageSize() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.fetchCategories(new VendorId(VENDOR_ID_1), PAGE_NUMBER, 0, List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductCategorySortField.NAME))));
    }

    @Test
    public void testFetchProductCategoriesWithoutSort() {
        ProductCategoryListResponse productCategories = stockApplicationService.fetchCategories(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(2, productCategories.getList().size());
    }

    @Test
    public void testFetchProductCategoriesWithoutSortNotFound() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class, () -> stockApplicationService.fetchCategories(new VendorId(VENDOR_ID_2), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Product categories not found!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void testFetchProductCategoriesWithoutSortInvalidPageNumber() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.fetchCategories(new VendorId(VENDOR_ID_1), -1, PAGE_SIZE));
    }

    @Test
    public void test_Product_Create() {
        ProductResponse productResponse = stockApplicationService.createProduct(new VendorId(VENDOR_ID_1), createProductCommand);
        assertEquals(PRODUCT_NAME_1, productResponse.getName());
    }

    @Test
    public void test_Product_Create_CategoryNotFound() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class, () -> stockApplicationService.createProduct(new VendorId(VENDOR_ID_1), createProductCommandWrongCategoryId));
        assertEquals("Category does not belong to this vendor!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Create_InvalidCategory() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class, () -> stockApplicationService.createProduct(new VendorId(VENDOR_ID_1), createProductCommandInvalidCategoryId));
        assertEquals("Category does not belong to this vendor!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Create_WrongName() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createProduct(new VendorId(VENDOR_ID_1), createProductCommandWrongName));
    }

    @Test
    public void test_Product_Create_WrongUnitPrice() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createProduct(new VendorId(VENDOR_ID_1), createProductCommandWrongUnitPrice));
    }

    @Test
    public void test_Product_Create_WrongStatus() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createProduct(new VendorId(VENDOR_ID_1), createProductCommandWrongStatus));
    }

    @Test
    public void test_Product_Update() {
        ProductResponse productResponse = stockApplicationService.updateProduct(new VendorId(VENDOR_ID_1), updateProductCommand);
        assertEquals(PRODUCT_NAME_1, productResponse.getName());
    }

    @Test
    public void test_Product_Update_NotFound() {
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.updateProduct(new VendorId(VENDOR_ID_1), updateProductCommandProductNotFound));
        assertEquals("Product not found!", productNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Update_InvalidCategoryId() {
        ProductCategoryNotFoundException productCategoryNotFoundException = assertThrows(ProductCategoryNotFoundException.class,
                () -> stockApplicationService.updateProduct(new VendorId(VENDOR_ID_1), updateProductCommandInvalidCategoryId));
        assertEquals("Category does not belong to this vendor!", productCategoryNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Update_WrongProductId() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.updateProduct(new VendorId(VENDOR_ID_1), updateProductCommandWrongProductId));
    }

    @Test
    public void test_Product_Update_WrongName() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.updateProduct(new VendorId(VENDOR_ID_1), updateProductCommandWrongName));
    }

    @Test
    public void test_Product_Update_WrongVendorId() {
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.updateProduct(new VendorId(VENDOR_ID_1), updateProductCommandWrongVendorId));
        assertEquals("Product not found!", productNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Delete() {
        ProductResponse productResponse = stockApplicationService.deleteProduct(new VendorId(VENDOR_ID_1), deleteProductCommand);
        assertEquals(PRODUCT_NAME_1, productResponse.getName());
    }

    @Test
    public void test_Product_Delete_WrongProductId() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.deleteProduct(new VendorId(VENDOR_ID_1), deleteProductCommandWrongProductId));
    }

    @Test
    public void test_Product_Delete_WrongVendorId() {
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.deleteProduct(new VendorId(VENDOR_ID_2), deleteProductCommandWrongVendorId));
        assertEquals("Product not found!", productNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Fetch_Products() {
        ProductListResponse products = stockApplicationService.fetchProducts(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(2, products.getList().size());
        assertEquals(PRODUCT_NAME_1, products.getList().get(0).getName());
    }

    @Test
    public void test_Product_Fetch_Products_NotFound() {
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.fetchProducts(new VendorId(VENDOR_ID_2), PAGE_NUMBER, PAGE_SIZE));
        assertEquals("Products not found!", productNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Fetch_Products_WithSort() {
        ProductListResponse products = stockApplicationService.fetchProducts(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE,
                List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductSortField.NAME)));
        assertEquals(3, products.getList().size());
        assertEquals(PRODUCT_NAME_3, products.getList().get(2).getName());
    }

    @Test
    public void test_Product_Fetch_Products_NotFound_WithSort() {
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.fetchProducts(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE,
                        List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductSortField.CREATED_AT))));
        assertEquals("Products not found!", productNotFoundException.getMessage());
    }

    @Test
    public void test_Product_Fetch_Products_By_Name(){
        ProductListResponse products  = stockApplicationService.fetchProducts(new VendorId(VENDOR_ID_1),new ProductName(PRODUCT_NAME_1),ProductStatus.ACTIVE, PAGE_NUMBER, PAGE_SIZE,
                List.of(new AbstractSortPayload<>(SortDirection.DESC, ProductSortField.NAME)));
        assertEquals(3,products.getList().size());
        assertEquals(PRODUCT_NAME_3, products.getList().get(2).getName());
    }

    @Test
    public void test_Order_create() {
        OrderResponse orderResponse = stockApplicationService.createOrder(new VendorId(VENDOR_ID_1), createOrderCommand);
        assertEquals(orderPriceTotal, orderResponse.getPrice());
    }

    @Test
    public void test_Order_create_multiple_same_product() {
        OrderResponse orderResponse = stockApplicationService.createOrder(new VendorId(VENDOR_ID_1), createOrderCommandMultipleSameProduct);
        assertEquals(orderPriceTotal, orderResponse.getPrice());
    }

    @Test
    public void test_Order_create_Null_Items() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createOrder(new VendorId(VENDOR_ID_1), createOrderCommandNullItems));
    }

    @Test
    public void test_Order_create_Empty_Items() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.createOrder(new VendorId(VENDOR_ID_1), createOrderCommandEmptyItems));
    }

    @Test
    public void test_Order_create_wrong_product() {
        ProductNotFoundException productNotFoundException = assertThrows(ProductNotFoundException.class, () -> stockApplicationService.createOrder(new VendorId(VENDOR_ID_1), createOrderCommandWrongProduct));
        assertEquals("Product in order items not found!", productNotFoundException.getMessage());
    }

    @Test
    public void test_Order_create_Invalid_Quantity() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class, () -> stockApplicationService.createOrder(new VendorId(VENDOR_ID_1), createOrderCommandInvalidQuantity));
        assertEquals("Quantity must be less than the available stock", stockDomainException.getMessage());
    }

    @Test
    void test_Order_Modify_Item() {
        OrderResponse orderResponse = stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandNewProduct);
        assertEquals(orderPriceTotal, orderResponse.getPrice());
    }

    @Test
    void test_Order_Modify_Item_Invalid_Order() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandNewInvalidOrder));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Order_Modify_Item_Invalid_Product() {
        ProductNotFoundException notFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandNewInvalidProduct));
        assertEquals("Product not available!", notFoundException.getMessage());
    }

    @Test
    void test_Order_Modify_Item_Wrong_Product_Quantity() {
        StockDomainException domainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandNewProductWrongQuantity));
        assertEquals("Quantity must be less than the available stock", domainException.getMessage());
    }

    @Test
    void test_Order_Modify_Item_Same_Product_New_Quantity() {
        OrderResponse orderResponse = stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandSameProductNewQuantity);
        assertEquals(orderPriceTotal, orderResponse.getPrice());
    }

    @Test
    void test_Order_Modify_Item_Same_Product_Wrong_Quantity() {
        StockDomainException domainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandSameProductWrongQuantity));
        assertEquals("Quantity must be less than the available stock", domainException.getMessage());
    }

    @Test
    void test_Order_Modify_Item_Invalid_Order_State() {
        StockDomainException domainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_3), modifyOrderCommandInvalidOrderState));
        assertEquals("Order is not in correct state for order item modification!", domainException.getMessage());
    }

    @Test
    void test_Order_Modify_Item_Invalid_Product_State() {
        StockDomainException domainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.modifyOrder(new VendorId(VENDOR_ID_1), modifyOrderCommandInvalidProductState));
        assertEquals("Product: " + PRODUCT_NAME_4 + " is not active!", domainException.getMessage());
    }

    @Test
    void test_Order_Delete_Product() {
        OrderResponse orderResponse = stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_4), deleteOrderProductCommand);
        assertEquals(1, orderResponse.getItems().size());
        assertEquals(PRODUCT_ID_1, orderResponse.getItems().get(0).getProductId());
    }

    @Test
    void test_Order_Delete_Product_Wrong_Order() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_4), deleteOrderProductCommandWrongOrder));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Order_Delete_Product_Not_Found() {
        ProductNotFoundException notFoundException = assertThrows(ProductNotFoundException.class,
                () -> stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_4), deleteOrderProductNotFoundProduct));
        assertEquals("Product not available!", notFoundException.getMessage());
    }

    @Test
    void test_Order_Delete_Product_Wrong_Product() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_4), deleteOrderProductWrongProduct));
        assertEquals("Product does not exist in the current order!", stockDomainException.getMessage());
    }

    @Test
    void test_Order_Delete_Order_Item() {
        OrderResponse orderResponse = stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_5), deleteOrderItemCommand);
        assertEquals(1, orderResponse.getItems().size());
        assertEquals(PRODUCT_ID_1, orderResponse.getItems().get(0).getProductId());
    }

    @Test
    void test_Order_Delete_Order_Item_Wrong_Order() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_5), deleteOrderItemCommandWrongOrder));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Order_Delete_Order_Item_Wrong_Order_Item() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.deleteOrderItem(new VendorId(VENDOR_ID_5), deleteOrderItemCommandWrongOrderItem));
        assertEquals("Order item does not exist!", stockDomainException.getMessage());
    }

    @Test
    void test_Order_settle() {
        OrderResponse orderResponse = stockApplicationService.settleOrder(new VendorId(VENDOR_ID_4), settleOrderCommand);
        assertNotNull(orderResponse.getOrderId());
    }

    @Test
    void test_Order_settle_Invalid_Order() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.settleOrder(new VendorId(VENDOR_ID_4), settleOrderCommandInvalidOrder));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Order_settle_Invalid_Status() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.settleOrder(new VendorId(VENDOR_ID_4), settleOrderCommandInvalidStatus));
        assertEquals("Order is not in correct state for settle operation!", stockDomainException.getMessage());
    }

    @Test
    void test_Order_Cancel() {
        OrderResponse orderResponse = stockApplicationService.cancelOrder(new VendorId(VENDOR_ID_4), cancelOrderCommand);
        assertNotNull(orderResponse.getOrderId());
    }

    @Test
    void test_Order_Cancel_Invalid_Order() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.cancelOrder(new VendorId(VENDOR_ID_4), cancelOrderCommandInvalidOrder));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Order_Cancel_Invalid_Status() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.cancelOrder(new VendorId(VENDOR_ID_4), cancelOrderCommandInvalidStatus));
        assertEquals("Order is not in correct state for cancel operation!", stockDomainException.getMessage());
    }

    @Test
    void test_Order_Cancel_Null_Messages() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.cancelOrder(new VendorId(VENDOR_ID_4), cancelOrderCommandNullMessages));
    }

    @Test
    void test_Order_Cancel_Empty_Messages() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.cancelOrder(new VendorId(VENDOR_ID_4), cancelOrderCommandEmptyMessages));
    }

    @Test
    void test_Order_Delete() {
        stockApplicationService.deleteOrder(new VendorId(VENDOR_ID_4), deleteOrderCommand);
    }

    @Test
    void test_Order_Delete_Invalid_Order() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.deleteOrder(new VendorId(VENDOR_ID_4), deleteOrderCommandInvalidOrder));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Order_Delete_Invalid_Status() {
        StockDomainException stockDomainException = assertThrows(StockDomainException.class,
                () -> stockApplicationService.deleteOrder(new VendorId(VENDOR_ID_4), deleteOrderCommandInvalidStatus));
        assertEquals("Order is not in correct state for deletion!", stockDomainException.getMessage());
    }

    @Test
    void test_Order_Delete_Null_Messages() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.deleteOrder(new VendorId(VENDOR_ID_4), deleteOrderCommandNullMessages));
    }

    @Test
    void test_Order_Delete_Empty_Messages() {
        assertThrows(ConstraintViolationException.class,
                () -> stockApplicationService.deleteOrder(new VendorId(VENDOR_ID_4), deleteOrderCommandEmptyMessages));
    }

    @Test
    void test_Order_Fetch_Order() {
        OrderResponse orderResponse = stockApplicationService.fetchOrder(new VendorId(VENDOR_ID_1), new OrderId(ORDER_ID_1));
        assertEquals(ORDER_ID_1, orderResponse.getOrderId());
    }

    @Test
    void test_Order_Fetch_Order_Not_Found() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.fetchOrder(new VendorId(VENDOR_ID_2), new OrderId(ORDER_ID_1)));
        assertEquals(Order_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Orders_Without_Sort() {
        OrderListResponse orders = stockApplicationService.fetchOrders(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE);
        assertEquals(3, orders.getList().size());
    }

    @Test
    void test_Orders_With_Sort() {
        OrderListResponse orders = stockApplicationService.fetchOrders(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE, orderSortList);
        assertEquals(2, orders.getList().size());
    }

    @Test
    void test_Orders_Without_Sort_Not_Found() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.fetchOrders(new VendorId(VENDOR_ID_2), PAGE_NUMBER, PAGE_SIZE));
        assertEquals(Orders_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Orders_With_Sort_Not_Found() {
        OrderNotFoundException notFoundException = assertThrows(OrderNotFoundException.class,
                () -> stockApplicationService.fetchOrders(new VendorId(VENDOR_ID_2), PAGE_NUMBER, PAGE_SIZE, orderSortList));
        assertEquals(Orders_Not_Found_Exception_Message, notFoundException.getMessage());
    }

    @Test
    void test_Orders_With_Null_Sort() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.fetchOrders(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE, null));
    }

    @Test
    void test_Orders_With_Empty_Sort() {
        assertThrows(ConstraintViolationException.class, () -> stockApplicationService.fetchOrders(new VendorId(VENDOR_ID_1), PAGE_NUMBER, PAGE_SIZE, Collections.emptyList()));
    }
}
