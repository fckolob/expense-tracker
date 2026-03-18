package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;

import java.util.List;

public class ExpenseService {

    private ExpenseRepository repository = new ExpenseRepository();

    public void addExpense(Expense expense){
        repository.save(expense);
    }

    public List<Expense> getExpenses(){
        return repository.findAll();
    }

    public void deleteExpense(int id){
    repository.delete(id);
}

public void updateExpense(Expense expense){
    repository.update(expense);
}
}
