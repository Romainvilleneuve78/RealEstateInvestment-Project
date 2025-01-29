package com.propertystake.controller;

import com.propertystake.model.User;
import com.propertystake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Récupère tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Récupère un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Crée un nouvel utilisateur
    @PostMapping(
            consumes = {"application/json", "application/json;charset=UTF-8"},
            produces = "application/json"
    )


    public User createUser(@RequestBody User user) {
        // Affiche les headers et l'objet reçu pour déboguer
        System.out.println("Headers: application/json");
        System.out.println("Received user: " + user);

        // Vérifie et configure le portefeuille (Wallet) de l'utilisateur si présent
        if (user.getWallet() != null) {
            user.getWallet().setUser(user); // Configure la relation bidirectionnelle
        }

        // Appelle le service pour créer et sauvegarder l'utilisateur
        return userService.createUser(user);
    }


    // Supprime un utilisateur par son ID
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
