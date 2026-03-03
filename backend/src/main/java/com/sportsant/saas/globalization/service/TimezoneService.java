package com.sportsant.saas.globalization.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportsant.saas.globalization.entity.InternationalizationProfile;
import com.sportsant.saas.globalization.repository.InternationalizationProfileRepository;
import com.sportsant.saas.membership.entity.Member;
import com.sportsant.saas.membership.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class TimezoneService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InternationalizationProfileRepository profileRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public ZoneId getUserTimezone(Long userId) {
        if (userId == null) return ZoneId.of("UTC");
        
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        if (memberOpt.isPresent() && memberOpt.get().getTimezone() != null) {
            try {
                return ZoneId.of(memberOpt.get().getTimezone());
            } catch (Exception e) {
                // Invalid timezone string
            }
        }
        return ZoneId.of("UTC");
    }

    public ZoneId getLocaleTimezone(String locale) {
        Optional<InternationalizationProfile> profileOpt = profileRepository.findByLocale(locale);
        if (profileOpt.isPresent() && profileOpt.get().getTimezone() != null) {
            try {
                return ZoneId.of(profileOpt.get().getTimezone());
            } catch (Exception e) {
                // Invalid timezone string
            }
        }
        return ZoneId.of("UTC");
    }

    public ZonedDateTime convertToUserTime(LocalDateTime serverTime, Long userId) {
        ZoneId userZone = getUserTimezone(userId);
        return serverTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(userZone);
    }
    
    public String formatUserTime(LocalDateTime serverTime, Long userId, String pattern) {
        ZonedDateTime userTime = convertToUserTime(serverTime, userId);
        return userTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public String formatTimeForLocale(LocalDateTime serverTime, String locale, String formatKey) {
        ZoneId zone = getLocaleTimezone(locale);
        ZonedDateTime zonedTime = serverTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(zone);
        
        String pattern = "yyyy-MM-dd HH:mm:ss"; // Default fallback
        Optional<InternationalizationProfile> profileOpt = profileRepository.findByLocale(locale);
        
        if (profileOpt.isPresent()) {
             try {
                 JsonNode config = objectMapper.readTree(profileOpt.get().getFormattingConfig());
                 // formatKey e.g. "date", "time", "dateTime"
                 if (config.has(formatKey)) {
                     pattern = config.get(formatKey).asText();
                 }
             } catch (Exception e) {
                 // ignore, use default
             }
        }
        
        // Normalize pattern (e.g., YYYY -> yyyy for Java)
        pattern = normalizePattern(pattern);
        
        try {
            return zonedTime.format(DateTimeFormatter.ofPattern(pattern));
        } catch (IllegalArgumentException e) {
            return zonedTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
    
    private String normalizePattern(String pattern) {
        // Simple normalization for common format differences
        return pattern.replace("YYYY", "yyyy").replace("DD", "dd");
    }
}
