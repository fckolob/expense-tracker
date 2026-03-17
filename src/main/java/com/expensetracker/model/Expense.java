package com.expensetracker.model;

import java.time.LocalDate;

public class Expense {

    private int id;
    private LocalDate date;
    private String category;
    private double amount;
    private String description;

    public Expense(int id, LocalDate date, String category, double amount, String description) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public Expense(LocalDate date, String category, double amount, String description) {
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
}

