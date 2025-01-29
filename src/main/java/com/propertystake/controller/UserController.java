package com.propertystake.controller;

import com.propertystake.model.User;
import com.propertystake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping(
            consumes = {"application/json", "application/json;charset=UTF-8"},
            produces = "application/json"
    )


    public User createUser(@RequestBody User user) {

        System.out.println("Headers: application/json");
        System.out.println("Received user: " + user);


        if (user.getWallet() != null) {
            user.getWallet().setUser(user); // Configure la relation bidirectionnelle
        }


        return userService.createUser(user);
    }



    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
