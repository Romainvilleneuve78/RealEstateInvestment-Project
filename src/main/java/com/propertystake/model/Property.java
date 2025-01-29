package com.propertystake.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, precision = 38, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private LocalDate fundingDeadline;

    @Column(nullable = false)
    private Double rentalIncomePercentage;

    @Column(nullable = false, length = 255)
    private String status; // "AVAILABLE", "FUNDED", "EXPIRED"

    @Column(nullable = false, length = 255)
    private String location;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Investment> investments;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(nullable = false)
    private LocalDate updatedAt;


    public Property() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }


    public Property(String name, BigDecimal price, LocalDate fundingDeadline, Double rentalIncomePercentage, String status, String location) {
        this.name = name;
        this.price = price;
        this.fundingDeadline = fundingDeadline;
        this.rentalIncomePercentage = rentalIncomePercentage;
        this.status = status;
        this.location = location;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }


    public boolean isFundingExpired() {
        return LocalDate.now().isAfter(this.fundingDeadline);
    }


    public void updatePropertyStatus(BigDecimal totalInvested) {
        if (totalInvested.compareTo(this.price) >= 0) {
            this.status = "FUNDED"; // Propriété entièrement financée
        } else if (isFundingExpired()) {
            this.status = "EXPIRED"; // Financement expiré
        } else {
            this.status = "AVAILABLE"; // Toujours disponible pour investissement
        }
    }


    public BigDecimal getRemainingAmount() {
        if (this.investments == null || this.investments.isEmpty()) {
            return this.price;
        }
        BigDecimal totalInvested = this.investments.stream()
                .map(Investment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.price.subtract(totalInvested).max(BigDecimal.ZERO);
    }


    public BigDecimal calculateRentalIncome(BigDecimal investmentAmount) {
        return investmentAmount.multiply(BigDecimal.valueOf(rentalIncomePercentage)).divide(BigDecimal.valueOf(100));
    }


    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }


    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDate.now();
        }
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getFundingDeadline() { return fundingDeadline; }
    public void setFundingDeadline(LocalDate fundingDeadline) { this.fundingDeadline = fundingDeadline; }
    public Double getRentalIncomePercentage() { return rentalIncomePercentage; }
    public void setRentalIncomePercentage(Double rentalIncomePercentage) { this.rentalIncomePercentage = rentalIncomePercentage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<Investment> getInvestments() { return investments; }
    public void setInvestments(List<Investment> investments) { this.investments = investments; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}
