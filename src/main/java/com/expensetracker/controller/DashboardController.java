package com.expensetracker.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;

public class DashboardController {

    @FXML
    private PieChart categoryChart;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField amountField;

    @FXML
    public void addExpense(){

        String category = categoryField.getText();
        double amount = Double.parseDouble(amountField.getText());

        categoryChart.getData().add(
                new PieChart.Data(category, amount)
        );
    }
}