package com.finalproject.cashflow.controller;

import com.finalproject.cashflow.exceptions.ResourceNotFoundException;
import com.finalproject.cashflow.model.Income;
import com.finalproject.cashflow.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RestController
@CrossOrigin(origins = "https://cashflow-app-bcc.herokuapp.com")//Allows data from the client side
@RequestMapping("/api")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    //Method to get all incomes by the use of the search
    @GetMapping("/incomes/search/{searchText}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public ResponseEntity<Page<Income>> findAll(Pageable pageable, @PathVariable String searchText) {
        return new ResponseEntity<>(incomeRepository.findAll(pageable, searchText), HttpStatus.OK);
    }
    //Method to get all incomes
    @GetMapping("/incomes/default")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public List<Income> getAllIncome(){
        return incomeRepository.findAll();
    }

    //Method to get all incomes by sort
    @GetMapping("/incomes")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public ResponseEntity<Page<Income>> getAllIncome(int pageNumber, int pageSize, String sortBy, String sortDir){
        return new ResponseEntity<>(incomeRepository.findAll(
                PageRequest.of(
                        pageNumber, pageSize,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
                )
        ), HttpStatus.OK);
    }

    //Method to get incomes by id
    @GetMapping("/incomes/{id}")
    public Income getIncome(@PathVariable(value = "id") Long id){

        return incomeRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Income not found")
        );
    }

    //Method to add an incomes
    @PostMapping("/incomes")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public Income saveIncome(@RequestBody Income income){
        return incomeRepository.save(income);
    }

    //Method to update an income
    @PutMapping("/incomes/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public Income updateIncome(@RequestBody Income newIncome, @PathVariable(value = "id") Long id){
        return incomeRepository.findById(id)
                .map(income -> {
                    income.setDescription(newIncome.getDescription());
                    income.setDate(newIncome.getDate());
                    income.setValue(newIncome.getValue());
                    income.setCategory(newIncome.getCategory());
                    return incomeRepository.save(income);
                })
                .orElseGet(()->{
                    newIncome.setId(id);
                    return incomeRepository.save(newIncome);
                });
    }

    //Method to delete and expense
    @DeleteMapping("incomes/{id}")
    @PreAuthorize("hasRole('ADMIN')")//Set the roles can request this method
    public void removeIncome(@PathVariable(value = "id") Long id){
        Income income = incomeRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Income not found")
        );
        incomeRepository.delete(income);
    }

    //Method to get all incomes categories
    @GetMapping("/incomes/categories")
    public ResponseEntity<Set<String>> findAllCategories(){
        return new ResponseEntity<>(new TreeSet<>(Arrays.asList("Donation", "Mass Offer", "Selling Actions")), HttpStatus.OK);
    }

}
