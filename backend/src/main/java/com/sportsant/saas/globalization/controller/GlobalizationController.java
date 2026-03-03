package com.sportsant.saas.globalization.controller;

import com.sportsant.saas.common.response.Result;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.service.GlobalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/globalization")
public class GlobalizationController {

    @Autowired
    private GlobalizationService globalizationService;

    @GetMapping("/profile/{locale}")
    public Result<InternationalizationProfile> getProfile(@PathVariable String locale) {
        return globalizationService.getProfileByLocale(locale)
                .map(Result::success)
                .orElse(Result.error(404, "Profile not found for locale: " + locale));
    }
}
