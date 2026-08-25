package com.example.ordermanagementsystem.service;

import com.example.ordermanagementsystem.dto.request.OrderDetailRequest;
import com.example.ordermanagementsystem.dto.request.OrderRequest;
import com.example.ordermanagementsystem.dto.request.OrderStatusRequest;
import com.example.ordermanagementsystem.dto.response.OrderDetailResponse;
import com.example.ordermanagementsystem.dto.response.OrderResponse;
import com.example.ordermanagementsystem.entity.*;
import com.example.ordermanagementsystem.enums.OrderStatus;
import com.example.ordermanagementsystem.enums.PaymentStatus;
import com.example.ordermanagementsystem.exception.InvalidOrderStatusException;
import com.example.ordermanagementsystem.exception.ProductOutOfStockException;
import com.example.ordermanagementsystem.exception.ResourceNotFoundException;
import com.example.ordermanagementsystem.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Chuyển OrderDetail thành response để trả về API
    private OrderDetailResponse mapDetailToResponse(OrderDetail detail) {
        OrderDetailResponse response = new OrderDetailResponse();

        response.setProductId(detail.getProduct().getId());
        response.setProductName(detail.getProduct().getName());
        response.setQuantity(detail.getQuantity());
        response.setUnitPrice(detail.getUnitPrice());
        response.setSubtotal(detail.getSubtotal());

        return response;
    }

    // Chuyển Order thành response để trả về API
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setOrderDate(order.getOrderDate());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setPaymentStatus(order.getPaymentStatus());

        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getName());

        response.setUserId(order.getUser().getId());
        response.setUserName(order.getUser().getFullName());

        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());

        List<OrderDetailResponse> detailResponses = new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {
            detailResponses.add(mapDetailToResponse(detail));
        }

        response.setOrderDetails(detailResponses);

        return response;
    }

    // Tạo đơn hàng mới và trừ số lượng sản phẩm trong kho
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        log.info("Creating new order for customerId={}, userId={}",
                request.getCustomerId(),
                request.getUserId());

        // Lấy customer từ DB
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", request.getCustomerId()));

        // Lấy user từ DB
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User", request.getUserId()));

        // Tạo Order
        Order order = new Order();

        order.setCustomer(customer);
        order.setUser(user);
        order.setOrderCode("ORD-" + System.currentTimeMillis());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.UNPAID);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderDetail> orderDetails = new ArrayList<>();

        // Xử lý từng sản phẩm trong đơn hàng
        for (OrderDetailRequest item : request.getOrderDetails()) {

            // Tìm sản phẩm
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product", item.getProductId()));

            // Kiểm tra tồn kho
            if (item.getQuantity() > product.getStock()) {

                log.warn("Product out of stock: productId={}, requestedQuantity={}, stock={}",
                        product.getId(),
                        item.getQuantity(),
                        product.getStock());

                throw new ProductOutOfStockException(product.getName());
            }

            // Tính thành tiền của sản phẩm
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            // Cộng vào tổng tiền đơn hàng
            totalAmount = totalAmount.add(subtotal);

            // Tạo OrderDetail
            OrderDetail detail = new OrderDetail();

            detail.setQuantity(item.getQuantity());
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setUnitPrice(product.getPrice());
            detail.setSubtotal(subtotal);

            orderDetails.add(detail);

            // Trừ số lượng sản phẩm trong kho
            product.setStock(product.getStock() - detail.getQuantity());
            productRepository.save(product);
        }

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully: orderId={}, orderCode={}, totalAmount={}",
                savedOrder.getId(),
                savedOrder.getOrderCode(),
                savedOrder.getTotalAmount());

        return mapToResponse(savedOrder);
    }

    // Lấy danh sách tất cả đơn hàng
    public List<OrderResponse> getAllOrders() {

        log.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(mapToResponse(order));
        }

        log.info("Fetched {} orders", responses.size());

        return responses;
    }

    // Lấy đơn hàng có phân trang
    public Page<OrderResponse> getAllOrders(Pageable pageable) {

        log.info("Fetching orders, page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Order> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(this::mapToResponse);
    }

    // Lấy một đơn hàng theo ID
    public OrderResponse getOrderById(Long id) {

        log.info("Fetching order with id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order", id));

        return mapToResponse(order);
    }

    // Hoàn lại số lượng sản phẩm vào kho khi hủy đơn
    private void restoreStock(Order order) {

        log.info("Restoring stock for cancelled orderId={}", order.getId());

        for (OrderDetail detail : order.getOrderDetails()) {

            Product product = detail.getProduct();

            product.setStock(
                    product.getStock() + detail.getQuantity()
            );

            productRepository.save(product);
        }
    }

    // Cập nhật trạng thái đơn hàng theo quy tắc cho phép
    @Transactional
    public OrderResponse updateOrderStatus(
            Long id,
            OrderStatusRequest request) {

        log.info("Updating order status: orderId={}, newStatus={}",
                id,
                request.getStatus());

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order", id));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Không cho phép cập nhật nếu trạng thái không thay đổi
        if (currentStatus == newStatus) {

            log.warn("Order status is already {} for orderId={}",
                    currentStatus,
                    id);

            throw new InvalidOrderStatusException(
                    "Đơn hàng đã ở trạng thái " + currentStatus
            );
        }

        // Kiểm tra trạng thái mới có hợp lệ không
        switch (currentStatus) {

            case PENDING:

                if (newStatus != OrderStatus.PROCESSING
                        && newStatus != OrderStatus.CANCELLED) {

                    throw new InvalidOrderStatusException(
                            "Đơn hàng PENDING chỉ được chuyển sang PROCESSING hoặc CANCELLED"
                    );
                }

                break;

            case PROCESSING:

                if (newStatus != OrderStatus.COMPLETED
                        && newStatus != OrderStatus.CANCELLED) {

                    throw new InvalidOrderStatusException(
                            "Đơn hàng PROCESSING chỉ được chuyển sang COMPLETED hoặc CANCELLED."
                    );
                }

                break;

            case COMPLETED:

                throw new InvalidOrderStatusException(
                        "Đơn hàng đã hoàn thành, không thể thay đổi trạng thái."
                );

            case CANCELLED:

                throw new InvalidOrderStatusException(
                        "Đơn hàng đã hủy, không thể thay đổi trạng thái."
                );
        }

        // Nếu hủy đơn thì hoàn lại hàng vào kho
        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);

        orderRepository.save(order);

        log.info("Order status updated successfully: orderId={}, {} -> {}",
                id,
                currentStatus,
                newStatus);

        return mapToResponse(order);
    }
}