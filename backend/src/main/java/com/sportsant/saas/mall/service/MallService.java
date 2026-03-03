package com.sportsant.saas.mall.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import com.sportsant.saas.globalization.service.TaxEngine;
import com.sportsant.saas.mall.entity.MallOrder;
import com.sportsant.saas.payment.service.PaymentResult;
import com.sportsant.saas.payment.service.PaymentService;
import com.sportsant.saas.mall.entity.MallProduct;
import com.sportsant.saas.mall.repository.MallOrderRepository;
import com.sportsant.saas.mall.repository.MallProductRepository;
import com.sportsant.saas.points.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MallService {

    @Autowired
    private MallProductRepository productRepository;

    @Autowired
    private MallOrderRepository orderRepository;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private TaxEngine taxEngine;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private InternationalizationProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<MallProduct> getAvailableProducts() {
        return productRepository.findByEnabledTrue();
    }

    public MallProduct createProduct(MallProduct product) {
        return productRepository.save(product);
    }

    @Transactional
    public MallOrder redeemProduct(Long productId, Long memberId, String address, String locale) {
        // 1. Check Product
        MallProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        // Check if stock is sufficient
        if (product.getStock() == null || product.getStock() <= 0) {
            throw new RuntimeException("Out of stock");
        }

        // 2. Check Member Points & Burn
        // pointsService.burnPoints will check balance and throw exception if insufficient
        if (product.getPointsPrice() > 0) {
            pointsService.burnPoints(memberId.toString(), "MALL_REDEEM", product.getPointsPrice(), "MALL_ORDER");
        }

        // 3. Deduct Stock
        product.setStock(product.getStock() - 1);
        product.setSalesCount(product.getSalesCount() + 1);
        productRepository.save(product);

        // 4. Create Order
        MallOrder order = new MallOrder();
        order.setOrderNo("MALL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setProductType(product.getType());
        order.setMemberId(memberId.toString());
        order.setPointsPaid(product.getPointsPrice());
        
        // 5. Calculate Tax & Initiate Payment
        BigDecimal cashPrice = product.getCashPrice() != null ? product.getCashPrice() : BigDecimal.ZERO;
        String finalLocale = locale != null ? locale : "en-US";
        
        // Determine Currency
        String currency = "USD";
        try {
            InternationalizationProfile profile = profileRepository.findByLocale(finalLocale).orElse(null);
            if (profile != null && profile.getCurrencyConfig() != null) {
                JsonNode config = objectMapper.readTree(profile.getCurrencyConfig());
                if (config.has("code")) currency = config.get("code").asText();
            }
        } catch (Exception e) {
            // ignore
        }
        order.setCurrency(currency);
        
        if (cashPrice.compareTo(BigDecimal.ZERO) > 0) {
            // Calculate Tax
            TaxEngine.TaxResult taxResult = taxEngine.calculateTax(finalLocale, cashPrice);
            order.setNetAmount(taxResult.netAmount);
            order.setTaxAmount(taxResult.taxAmount);
            order.setTaxRate(taxResult.taxRate);
            order.setTaxType(taxResult.taxType);
            order.setRegionLocale(taxResult.locale);
            order.setCashPaid(taxResult.totalAmount);
            
            // Initiate Payment
            PaymentResult paymentResult = paymentService.initiatePayment(
                finalLocale, 
                order.getOrderNo(), 
                taxResult.totalAmount, 
                "Order " + order.getOrderNo()
            );
            
            if (paymentResult.isSuccess()) {
                order.setStatus("PENDING_PAYMENT");
                order.setPayUrl(paymentResult.getPayUrl());
            } else {
                throw new RuntimeException("Payment initiation failed: " + paymentResult.getMessage());
            }
            
        } else {
            order.setCashPaid(BigDecimal.ZERO);
            order.setNetAmount(BigDecimal.ZERO);
            order.setTaxAmount(BigDecimal.ZERO);
            order.setTaxRate(BigDecimal.ZERO);
            order.setTaxType("NONE");
            order.setRegionLocale(finalLocale);
            order.setStatus("PAID"); // Pure points
        }

        order.setShippingAddress(address);
        
        // Auto-complete for virtual goods ONLY if Paid (or pure points)
        if ("PAID".equals(order.getStatus()) && ("VIRTUAL".equals(product.getType()) || "COUPON".equals(product.getType()))) {
            order.setStatus("COMPLETED");
            order.setRedeemCode(UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            order.setCompletedAt(LocalDateTime.now());
        }

        return orderRepository.save(order);
    }

    public List<MallOrder> getMemberOrders(Long memberId) {
        return orderRepository.findByMemberId(memberId.toString());
    }

    public List<MallOrder> getAllOrders() {
        return orderRepository.findAll();
    }
}
