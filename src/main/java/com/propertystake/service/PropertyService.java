package com.propertystake.service;

import com.propertystake.model.Property;
import com.propertystake.model.Investment;
import com.propertystake.model.Wallet;
import com.propertystake.repository.PropertyRepository;
import com.propertystake.repository.InvestmentRepository;
import com.propertystake.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private EmailService emailService;

    // 🔹 Récupère toutes les propriétés
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // 🔹 Récupère une propriété par ID
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
    }

    // 🔹 Crée une nouvelle propriété
    public Property createProperty(Property property) {
        property.setFundingDeadline(LocalDate.now().plusMonths(2)); // Fixe une date limite à 2 mois
        property.setCreatedAt(LocalDate.now()); // Assure que createdAt est bien initialisé
        property.setUpdatedAt(LocalDate.now()); // Initialise updatedAt
        return propertyRepository.save(property);
    }

    // 🔹 Supprime une propriété par ID
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    // 🔹 Vérification automatique chaque jour pour voir si les propriétés atteignent leur objectif
    @Scheduled(cron = "0 0 0 * * *") // Exécution tous les jours à minuit
    public void checkFundingDeadlines() {
        System.out.println("📢 Vérification des délais de financement...");

        List<Property> properties = propertyRepository.findAll();
        for (Property property : properties) {
            if (property.getFundingDeadline().isBefore(LocalDate.now()) && !"FUNDED".equals(property.getStatus())) {
                BigDecimal totalInvestment = investmentRepository.sumInvestmentsByProperty(property.getId());

                if (totalInvestment.compareTo(property.getPrice()) < 0) { // Objectif non atteint
                    refundInvestors(property);
                } else {
                    property.setStatus("FUNDED");
                    propertyRepository.save(property);
                }
            }
        }

        System.out.println("✅ Fin de la vérification des délais de financement.");
    }

    // 🔹 Remboursement des investisseurs si la propriété n'est pas financée dans le délai imparti
    private void refundInvestors(Property property) {
        System.out.println("🚨 Annulation du financement pour la propriété : " + property.getName());

        List<Investment> investments = investmentRepository.findByPropertyId(property.getId());
        for (Investment investment : investments) {
            Wallet wallet = investment.getUser().getWallet();
            if (wallet != null) {
                wallet.setBalance(wallet.getBalance().add(investment.getAmount())); // Remboursement de l'investissement
                walletRepository.save(wallet);
            }

            // Envoyer un email aux investisseurs
            String subject = "⚠️ Investissement annulé";
            String message = "Bonjour " + investment.getUser().getFirstName() + ",\n\n"
                    + "Votre investissement de " + investment.getAmount() + "€ dans la propriété " + property.getName()
                    + " a été annulé car l'objectif de financement n'a pas été atteint dans le délai imparti.\n"
                    + "Votre argent a été remboursé sur votre portefeuille interne.\n\n"
                    + "Merci de votre confiance.";

            emailService.sendEmail(investment.getUser().getEmail(), subject, message);
        }

        // Supprimer tous les investissements liés à cette propriété
        investmentRepository.deleteAll(investments);

        // Marquer la propriété comme "EXPIRED"
        property.setStatus("EXPIRED");
        propertyRepository.save(property);

        System.out.println("✅ Tous les investisseurs ont été remboursés et la propriété est marquée comme 'EXPIRED'.");
    }
}
