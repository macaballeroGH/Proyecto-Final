package com.example.beautymanager.Configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String issuer;
    private String audience;
    private long expirationHours;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public long getExpirationHours() {
        return expirationHours;
    }

    public void setExpirationHours(long expirationHours) {
        this.expirationHours = expirationHours;
    }
}
