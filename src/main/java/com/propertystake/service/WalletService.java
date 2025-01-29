package com.propertystake.service;

import com.propertystake.model.User;
import com.propertystake.model.Wallet;
import com.propertystake.repository.UserRepository;
import com.propertystake.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService; // 🔹 Ajout du service email

    // Création d’un portefeuille pour un utilisateur donné
    public Wallet createWalletForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setUser(user); // 🔹 Associer le wallet à l’utilisateur

        return walletRepository.save(wallet);
    }

    // Mise à jour du solde du portefeuille avec notification par email
    public Wallet updateBalance(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet introuvable"));

        wallet.setBalance(wallet.getBalance().add(amount));
        Wallet updatedWallet = walletRepository.save(wallet);

        // 🔹 Envoyer un email de notification à l'utilisateur
        User user = wallet.getUser();
        if (user != null) {
            String subject = "Mise à jour de votre portefeuille";
            String message = "Bonjour " + user.getFirstName() + ",\n\n"
                    + "Votre portefeuille a été mis à jour. Nouveau solde : " + updatedWallet.getBalance() + "€.\n"
                    + "Merci de vérifier votre compte.";

            emailService.sendEmail(user.getEmail(), subject, message);
            System.out.println("Email de mise à jour du portefeuille envoyé à " + user.getEmail());
        }

        return updatedWallet;
    }
}
