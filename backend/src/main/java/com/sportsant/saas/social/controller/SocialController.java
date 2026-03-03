package com.sportsant.saas.social.controller;

import com.sportsant.saas.social.entity.SocialAccount;
import com.sportsant.saas.social.entity.SocialContent;
import com.sportsant.saas.social.service.SocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    @Autowired
    private SocialService socialService;

    @GetMapping("/accounts")
    public List<SocialAccount> getAccounts() {
        return socialService.getAllAccounts();
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public SocialAccount addAccount(@RequestBody SocialAccount account) {
        return socialService.addAccount(account);
    }

    @PostMapping("/content")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MARKETING')")
    public SocialContent createContent(@RequestBody SocialContent content) {
        return socialService.createContent(content);
    }
}
