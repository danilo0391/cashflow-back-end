package com.finalproject.cashflow.controller;

import com.finalproject.cashflow.exceptions.ResourceNotFoundException;
import com.finalproject.cashflow.model.Expense;
import com.finalproject.cashflow.repository.ExpenseRespository;
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
@CrossOrigin(origins = "https://cashflow-app-bcc.herokuapp.com") //Allows data from the client side
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseRespository expenseRespository;

    //Method to get all expenses by the use of the search
    @GetMapping("/expenses/search/{searchText}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public ResponseEntity<Page<Expense>> findAll(Pageable pageable, @PathVariable String searchText) {
        return new ResponseEntity<>(expenseRespository.findAll(pageable, searchText), HttpStatus.OK);
    }

    //Method to get all expenses
    @GetMapping("/expenses/default")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')") //Set the roles can request this method
    public List<Expense> getAllExpense(){
        return expenseRespository.findAll();
    }

    //Method to get all expenses by sort
    @GetMapping("/expenses")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public ResponseEntity<Page<Expense>> getAllExpense(int pageNumber, int pageSize, String sortBy, String sortDir){
        return new ResponseEntity<>(expenseRespository.findAll(
                PageRequest.of(
                        pageNumber, pageSize,
                        sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
                )
        ), HttpStatus.OK);
    }

    //Method to get expenses by id
    @GetMapping("/expenses/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public Expense getExpenseById(@PathVariable(value = "id") Long id){
        return expenseRespository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Expense not found")
        );
    }

    //Method to add an expense
    @PostMapping("/expenses")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public Expense addExpense(@RequestBody Expense expense){
        return expenseRespository.save(expense);
    }

    //Method to update an expense
    @PutMapping("/expenses/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")//Set the roles can request this method
    public Expense updateExpense(@RequestBody Expense newExpense, @PathVariable(value = "id") Long id){
        return expenseRespository.findById(id)
                .map(expense -> {
                    expense.setDescription(newExpense.getDescription());
                    expense.setDate(newExpense.getDate());
                    expense.setValue(newExpense.getValue());
                    expense.setCategory(newExpense.getCategory());
                    return expenseRespository.save(expense);
                })
                .orElseGet(()->{
                    newExpense.setId(id);
                    return expenseRespository.save(newExpense);
                });
    }

    //Method to delete and expense
    @DeleteMapping("expenses/{id}")
    @PreAuthorize("hasRole('ADMIN')")//Set the roles can request this method
    public void deleteExpense(@PathVariable(value = "id") Long id){
        Expense expense = expenseRespository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Expense not found")
        );
        expenseRespository.delete(expense);
    }

    //Method to get all expense categories
    @GetMapping("/expenses/categories")
    public ResponseEntity<Set<String>> findAllCategories(){
        return new ResponseEntity<>(new TreeSet<>(Arrays.asList("Liturgy Stuff", "Music Equipment", "Family Group", "Cleaning Materials", "Other")), HttpStatus.OK); //The categories are being set in the array as a list
    }
}
