package com.propertystake.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvestmentRequest {

    @NotNull(message = "propertyId is required")
    private Long propertyId;

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "amount is required")
    @Min(value = 500, message = "Investment amount must be greater than 500")
    private BigDecimal amount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    // 🔹 Constructeur par défaut
    public InvestmentRequest() {
        this.date = LocalDate.now(); // Défaut à la date du jour si non fournie
    }

    // 🔹 Constructeur avec paramètres
    public InvestmentRequest(Long propertyId, Long userId, BigDecimal amount, LocalDate date) {
        this.propertyId = propertyId;
        this.userId = userId;
        this.amount = amount;
        this.date = (date != null) ? date : LocalDate.now(); // Si `date` est null, prendre la date du jour
    }

    // 🔹 Getters et setters
    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = (date != null) ? date : LocalDate.now();
    }

    @Override
    public String toString() {
        return "InvestmentRequest{" +
                "propertyId=" + propertyId +
                ", userId=" + userId +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
