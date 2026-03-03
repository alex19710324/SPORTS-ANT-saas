package com.sportsant.saas.mall.controller;

import com.sportsant.saas.mall.entity.MallOrder;
import com.sportsant.saas.mall.entity.MallProduct;
import com.sportsant.saas.mall.service.MallService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mall")
@Tag(name = "商城中心", description = "商品展示、订单管理")
public class MallController {

    @Autowired
    private MallService mallService;

    @GetMapping("/products")
    public List<MallProduct> getProducts() {
        return mallService.getAvailableProducts();
    }

    @PostMapping("/products")
    public MallProduct createProduct(@RequestBody MallProduct product) {
        return mallService.createProduct(product);
    }

    @PostMapping("/redeem")
    public MallOrder redeem(
            @RequestParam Long productId, 
            @RequestParam Long memberId, 
            @RequestParam(required = false) String address,
            @RequestParam(required = false, defaultValue = "en-US") String locale) {
        return mallService.redeemProduct(productId, memberId, address, locale);
    }

    @GetMapping("/orders")
    public List<MallOrder> getOrders() {
        return mallService.getAllOrders();
    }
}
