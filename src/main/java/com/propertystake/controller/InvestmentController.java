package com.propertystake.controller;

import com.propertystake.dto.InvestmentRequest;
import com.propertystake.model.Investment;
import com.propertystake.model.Property;
import com.propertystake.model.User;
import com.propertystake.service.InvestmentService;
import com.propertystake.service.PropertyService;
import com.propertystake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @PostMapping
    public Investment createInvestment(@RequestBody InvestmentRequest investmentRequest) {
        // 🔹 Récupération de la propriété et de l'utilisateur
        Property property = propertyService.getPropertyById(investmentRequest.getPropertyId());
        User user = userService.getUserById(investmentRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'ID: " + investmentRequest.getUserId()));

        BigDecimal amount = investmentRequest.getAmount();

        // 🔹 Vérification du montant minimum (500€)
        if (amount.compareTo(BigDecimal.valueOf(500)) < 0) {
            throw new RuntimeException("Le montant d'investissement minimum est de 500€.");
        }

        // 🔹 Vérification que l'investissement ne dépasse pas le montant restant à financer
        BigDecimal totalInvested = investmentService.getTotalInvestedForProperty(property.getId());
        BigDecimal remainingAmount = property.getPrice().subtract(totalInvested);
        if (amount.compareTo(remainingAmount) > 0) {
            throw new RuntimeException("Impossible d'investir plus que le montant restant à financer pour cette propriété. Montant restant : " + remainingAmount + "€.");
        }

        // 🔹 Vérification des fonds disponibles dans le wallet
        if (user.getWallet() == null || user.getWallet().getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Fonds insuffisants dans le wallet.");
        }

        // 🔹 Création de l'investissement
        Investment investment = new Investment();
        investment.setAmount(amount);
        investment.setDate(investmentRequest.getDate() != null ? investmentRequest.getDate() : LocalDate.now()); // Définit la date si elle est absente
        investment.setProperty(property);
        investment.setUser(user);

        // 🔹 Appel du service pour gérer l'investissement (déduit aussi les fonds)
        return investmentService.createInvestment(investment);
    }
}
