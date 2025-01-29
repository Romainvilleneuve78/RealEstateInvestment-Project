package com.propertystake.service;

import com.propertystake.model.Investment;
import com.propertystake.model.User;
import com.propertystake.model.Wallet;
import com.propertystake.model.Property;
import com.propertystake.repository.InvestmentRepository;
import com.propertystake.repository.WalletRepository;
import com.propertystake.repository.UserRepository;
import com.propertystake.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private EmailService emailService;

    // 🔹 Récupère les investissements par utilisateur
    public List<Investment> getInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserId(userId);
    }

    // 🔹 Récupère le total investi pour une propriété
    public BigDecimal getTotalInvestedForProperty(Long propertyId) {
        return Optional.ofNullable(investmentRepository.sumInvestmentsByProperty(propertyId))
                .orElse(BigDecimal.ZERO);
    }

    // 🔹 Crée un nouvel investissement
    public Investment createInvestment(Investment investment) {
        User user = investment.getUser();
        Property property = investment.getProperty();
        BigDecimal amount = investment.getAmount();

        // ✅ Vérification si la propriété est déjà financée ou expirée
        if ("FUNDED".equals(property.getStatus())) {
            throw new IllegalStateException("Cette propriété est déjà financée, vous ne pouvez plus investir.");
        }
        if ("EXPIRED".equals(property.getStatus())) {
            throw new IllegalStateException("Cette propriété a expiré, vous ne pouvez plus investir.");
        }

        // ✅ Vérification du montant minimum d'investissement (500€)
        if (amount.compareTo(BigDecimal.valueOf(500)) < 0) {
            throw new IllegalArgumentException("Le montant minimum d'investissement est de 500€.");
        }

        // ✅ Vérification du solde disponible dans le portefeuille
        Wallet wallet = user.getWallet();
        if (wallet == null || wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Fonds insuffisants dans le portefeuille.");
        }

        // ✅ Vérification du montant restant à financer
        BigDecimal totalInvested = getTotalInvestedForProperty(property.getId());
        BigDecimal remainingAmount = property.getPrice().subtract(totalInvested);
        if (amount.compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException("Le montant dépasse le financement restant de la propriété.");
        }

        // ✅ Débiter le portefeuille de l'utilisateur
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        // ✅ Sauvegarde de l'investissement
        investment.setDate(LocalDate.now());
        Investment savedInvestment = investmentRepository.save(investment);

        // ✅ Mettre à jour le statut de la propriété après investissement
        updatePropertyFundingStatus(property);

        // ✅ Envoi d'email de confirmation
        String subject = "Confirmation d'Investissement";
        String message = "Bonjour " + user.getFirstName() + ",\n\n"
                + "Votre investissement de " + amount + "€ dans la propriété " + property.getName() + " a été enregistré avec succès.\n"
                + "Montant restant à financer : " + (remainingAmount.subtract(amount).compareTo(BigDecimal.ZERO) > 0 ? remainingAmount.subtract(amount) : "Complètement financée") + "€.\n\n"
                + "Merci pour votre confiance !";

        emailService.sendEmail(user.getEmail(), subject, message);

        return savedInvestment;
    }

    // 🔹 Met à jour le statut de la propriété en fonction du financement
    private void updatePropertyFundingStatus(Property property) {
        BigDecimal totalInvested = getTotalInvestedForProperty(property.getId());

        if (totalInvested.compareTo(property.getPrice()) >= 0) {
            property.setStatus("FUNDED");
        } else if (property.isFundingExpired()) {
            property.setStatus("EXPIRED");
        } else {
            property.setStatus("AVAILABLE");
        }

        propertyRepository.save(property);
    }

    // 🔹 Planificateur pour créditer les revenus locatifs chaque mois
    @Scheduled(cron = "0 0 0 1 * *") // Exécution le 1er jour de chaque mois à minuit
    public void creditMonthlyRentalIncome() {
        System.out.println("Début du processus de revenus locatifs mensuels...");

        List<Investment> investments = investmentRepository.findAll();

        for (Investment investment : investments) {
            BigDecimal rentalIncome = calculateRentalIncome(investment);

            Wallet wallet = investment.getUser().getWallet();
            if (wallet != null) {
                wallet.setBalance(wallet.getBalance().add(rentalIncome));
                walletRepository.save(wallet);

                String subject = "Revenus Locatifs Crédités";
                String message = "Bonjour " + investment.getUser().getFirstName() + ",\n\n"
                        + "Votre revenu locatif mensuel de " + rentalIncome + "€ a été crédité sur votre portefeuille.";

                emailService.sendEmail(investment.getUser().getEmail(), subject, message);
            }
        }

        System.out.println("Fin du processus de revenus locatifs mensuels.");
    }

    // 🔹 Vérification quotidienne des propriétés non financées et remboursement des investisseurs
    @Scheduled(cron = "0 0 0 * * *") // Exécution tous les jours à minuit
    public void checkExpiredFundingProperties() {
        System.out.println("Vérification des propriétés non financées après la deadline...");

        List<Property> properties = propertyRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Property property : properties) {
            if ("AVAILABLE".equals(property.getStatus()) && property.getFundingDeadline().isBefore(today)) {
                // Propriété non financée à la deadline
                System.out.println("Remboursement en cours pour la propriété : " + property.getName());

                List<Investment> investments = investmentRepository.findByPropertyId(property.getId());

                for (Investment investment : investments) {
                    Wallet wallet = investment.getUser().getWallet();
                    if (wallet != null) {
                        wallet.setBalance(wallet.getBalance().add(investment.getAmount())); // Remboursement
                        walletRepository.save(wallet);
                    }
                    investmentRepository.delete(investment); // Suppression des investissements annulés
                }

                // Mettre à jour le statut de la propriété
                property.setStatus("EXPIRED");
                propertyRepository.save(property);

                System.out.println("Remboursement terminé pour la propriété : " + property.getName());
            }
        }
    }

    // 🔹 Calcul des revenus locatifs
    private BigDecimal calculateRentalIncome(Investment investment) {
        BigDecimal rentalIncomePercentage = BigDecimal.valueOf(investment.getProperty().getRentalIncomePercentage());
        return investment.getAmount().multiply(rentalIncomePercentage).divide(BigDecimal.valueOf(100));
    }
}
