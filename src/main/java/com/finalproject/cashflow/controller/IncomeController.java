package com.finalproject.cashflow.controller;

import com.finalproject.cashflow.exceptions.ResourceNotFoundException;
import com.finalproject.cashflow.model.Income;
import com.finalproject.cashflow.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @GetMapping("/incomes/search/{searchText}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<Income>> findAll(Pageable pageable, @PathVariable String searchText) {
        return new ResponseEntity<>(incomeRepository.findAll(pageable, searchText), HttpStatus.OK);
    }

    @GetMapping("/incomes")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public List<Income> getAllIncome(){
        return incomeRepository.findAll();
    }

    @GetMapping("/incomes/{id}")
//    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Income getIncome(@PathVariable(value = "id") Long id){

        return incomeRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Income not found")
        );
    }

    @PostMapping("/incomes")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public Income saveIncome(@RequestBody Income income){
        return incomeRepository.save(income);
    }

    @PutMapping("/incomes/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
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

    @DeleteMapping("incomes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeIncome(@PathVariable(value = "id") Long id){
        Income income = incomeRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Income not found")
        );
        incomeRepository.delete(income);
    }

    @GetMapping("/incomes/categories")
    public ResponseEntity<Set<String>> findAllCategories(){
        return new ResponseEntity<>(new TreeSet<>(Arrays.asList("Donation", "Mass Offer", "Selling Actions")), HttpStatus.OK);
    }

}
