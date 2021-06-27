package com.finalproject.cashflow.controller;

import com.finalproject.cashflow.exceptions.ResourceNotFoundException;
import com.finalproject.cashflow.model.User;
import com.finalproject.cashflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
   @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public User getUser(@PathVariable(value = "id") Long id){

        return userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User not found")
        );
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public User saveUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public User updateUser(@RequestBody User newUser, @PathVariable(value = "id") Long id){
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(newUser.getUsername());
                    user.setEmail(newUser.getEmail());
                    user.setPassword(newUser.getPassword());
                    return userRepository.save(user);
                })
                .orElseGet(()->{
                    newUser.setId(id);
                    return userRepository.save(newUser);
                });
    }

    @DeleteMapping("users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeUser(@PathVariable(value = "id") Long id){
        User user = userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User not found")
        );
        userRepository.delete(user);
    }
}
