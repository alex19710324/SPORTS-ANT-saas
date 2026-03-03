package com.sportsant.saas.points.controller;

import com.sportsant.saas.points.entity.PointsRule;
import com.sportsant.saas.points.entity.PointsTransaction;
import com.sportsant.saas.points.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Autowired
    private PointsService pointsService;

    @GetMapping("/rules")
    public List<PointsRule> getRules() {
        return pointsService.getAllRules();
    }

    @PostMapping("/rules")
    public PointsRule createRule(@RequestBody PointsRule rule) {
        return pointsService.createRule(rule);
    }

    @GetMapping("/transactions/{userId}")
    public List<PointsTransaction> getHistory(@PathVariable String userId) {
        return pointsService.getHistory(userId);
    }

    // Manual adjustment or testing endpoint
    @PostMapping("/earn")
    public void earnPoints(@RequestParam String userId, @RequestParam String ruleCode, @RequestParam(required = false) BigDecimal amount) {
        pointsService.earnPoints(userId, ruleCode, amount, "MANUAL_ADJUST");
    }
}
