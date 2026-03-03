package com.sportsant.saas.globalization.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "internationalization_profile")
public class InternationalizationProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String locale; // e.g., zh-CN

    private String countryName;
    
    private String timezone;

    // JSON stored as String
    @Column(columnDefinition = "TEXT")
    private String currencyConfig;

    @Column(columnDefinition = "TEXT")
    private String measurementConfig;

    @Column(columnDefinition = "TEXT")
    private String formattingConfig;

    @Column(columnDefinition = "TEXT")
    private String contactConfig;

    @Column(columnDefinition = "TEXT")
    private String networkConfig;

    @Column(columnDefinition = "TEXT")
    private String servicesConfig;

    @Column(columnDefinition = "TEXT")
    private String complianceConfig;

    @Column(columnDefinition = "TEXT")
    private String aiGuidanceConfig;

    @Column(columnDefinition = "TEXT")
    private String holidaysConfig;

    @Column(columnDefinition = "TEXT")
    private String socialPlatformsConfig;

    @Column(columnDefinition = "TEXT")
    private String paymentMethodsConfig;

    @Column(columnDefinition = "TEXT")
    private String kycRequirementsConfig;

    @Column(columnDefinition = "TEXT")
    private String validationConfig;

    @Column(columnDefinition = "TEXT")
    private String supportConfig;

    @Column(columnDefinition = "TEXT")
    private String featureFlagsConfig;

    @Column(columnDefinition = "TEXT")
    private String integrationsConfig;

    @Column(columnDefinition = "TEXT")
    private String sportsContextConfig;

    @Column(columnDefinition = "TEXT")
    private String dataGovernanceConfig;

    public InternationalizationProfile() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getCurrencyConfig() { return currencyConfig; }
    public void setCurrencyConfig(String currencyConfig) { this.currencyConfig = currencyConfig; }
    public String getMeasurementConfig() { return measurementConfig; }
    public void setMeasurementConfig(String measurementConfig) { this.measurementConfig = measurementConfig; }
    public String getFormattingConfig() { return formattingConfig; }
    public void setFormattingConfig(String formattingConfig) { this.formattingConfig = formattingConfig; }
    public String getContactConfig() { return contactConfig; }
    public void setContactConfig(String contactConfig) { this.contactConfig = contactConfig; }
    public String getNetworkConfig() { return networkConfig; }
    public void setNetworkConfig(String networkConfig) { this.networkConfig = networkConfig; }
    public String getServicesConfig() { return servicesConfig; }
    public void setServicesConfig(String servicesConfig) { this.servicesConfig = servicesConfig; }
    public String getComplianceConfig() { return complianceConfig; }
    public void setComplianceConfig(String complianceConfig) { this.complianceConfig = complianceConfig; }
    public String getAiGuidanceConfig() { return aiGuidanceConfig; }
    public void setAiGuidanceConfig(String aiGuidanceConfig) { this.aiGuidanceConfig = aiGuidanceConfig; }
    public String getHolidaysConfig() { return holidaysConfig; }
    public void setHolidaysConfig(String holidaysConfig) { this.holidaysConfig = holidaysConfig; }
    public String getSocialPlatformsConfig() { return socialPlatformsConfig; }
    public void setSocialPlatformsConfig(String socialPlatformsConfig) { this.socialPlatformsConfig = socialPlatformsConfig; }
    public String getPaymentMethodsConfig() { return paymentMethodsConfig; }
    public void setPaymentMethodsConfig(String paymentMethodsConfig) { this.paymentMethodsConfig = paymentMethodsConfig; }
    public String getKycRequirementsConfig() { return kycRequirementsConfig; }
    public void setKycRequirementsConfig(String kycRequirementsConfig) { this.kycRequirementsConfig = kycRequirementsConfig; }
    public String getValidationConfig() { return validationConfig; }
    public void setValidationConfig(String validationConfig) { this.validationConfig = validationConfig; }
    public String getSupportConfig() { return supportConfig; }
    public void setSupportConfig(String supportConfig) { this.supportConfig = supportConfig; }
    public String getFeatureFlagsConfig() { return featureFlagsConfig; }
    public void setFeatureFlagsConfig(String featureFlagsConfig) { this.featureFlagsConfig = featureFlagsConfig; }
    public String getIntegrationsConfig() { return integrationsConfig; }
    public void setIntegrationsConfig(String integrationsConfig) { this.integrationsConfig = integrationsConfig; }
    public String getSportsContextConfig() { return sportsContextConfig; }
    public void setSportsContextConfig(String sportsContextConfig) { this.sportsContextConfig = sportsContextConfig; }
    public String getDataGovernanceConfig() { return dataGovernanceConfig; }
    public void setDataGovernanceConfig(String dataGovernanceConfig) { this.dataGovernanceConfig = dataGovernanceConfig; }
}
