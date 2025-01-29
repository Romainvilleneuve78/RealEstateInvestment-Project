package com.propertystake.service;

import com.propertystake.model.Investment;
import com.propertystake.repository.InvestmentRepository;
import com.propertystake.service.EmailService;
import com.propertystake.service.WalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ScheduledRentalIncomeService {
    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 0 1 * ?") // Exécuté le 1er jour de chaque mois
    public void distributeRentalIncome() {
        List<Investment> investments = investmentRepository.findAll();

        for (Investment investment : investments) {
            BigDecimal rentalIncome = investment.getAmount()
                    .multiply(BigDecimal.valueOf(investment.getProperty().getRentalIncomePercentage()))
                    .divide(BigDecimal.valueOf(100));

            walletService.updateBalance(investment.getUser().getWallet().getId(),
                    investment.getUser().getWallet().getBalance().add(rentalIncome));

            // Envoyer un email
            String subject = "Revenus locatifs crédités";
            String message = "Bonjour " + investment.getUser().getFirstName() + ",\n\n"
                    + "Votre revenu locatif mensuel de " + rentalIncome + "€ a été crédité sur votre portefeuille.";

            emailService.sendEmail(investment.getUser().getEmail(), subject, message);
        }
    }
}
