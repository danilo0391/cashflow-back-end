package com.finalproject.cashflow.controller;

import com.finalproject.cashflow.exceptions.ResourceNotFoundException;
import com.finalproject.cashflow.model.User;
import com.finalproject.cashflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://cashflow-app-bcc.herokuapp.com")
@RestController
@RequestMapping("/api/")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    //Method to get all users
    @GetMapping("/users")
   @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //Method to get all users by id
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public User getUser(@PathVariable(value = "id") Long id){

        return userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User not found")
        );
    }

    //Method to add a new user
    @PostMapping("/users")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public User saveUser(@RequestBody User user){
        return userRepository.save(user);
    }

    //Method to update a user
    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
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

    //Method to delete a user
    @DeleteMapping("users/{id}")
    @PreAuthorize("hasRole('ADMIN')")//Set the roles can request this method
    public void removeUser(@PathVariable(value = "id") Long id){
        User user = userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User not found")
        );
        userRepository.delete(user);
    }
}
