package com.sportsant.saas.dto;

public class ConsentDto {
    private String agreementType; // "terms", "privacy", "marketing"
    private String version;
    private boolean agreed;

    public String getAgreementType() { return agreementType; }
    public void setAgreementType(String agreementType) { this.agreementType = agreementType; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isAgreed() { return agreed; }
    public void setAgreed(boolean agreed) { this.agreed = agreed; }
}
