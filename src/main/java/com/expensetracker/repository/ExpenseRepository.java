package com.expensetracker.repository;

import com.expensetracker.config.DatabaseConfig;
import com.expensetracker.model.Expense;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {

    public void save(Expense expense) {

        String sql = "INSERT INTO expenses(date, category, amount, description) VALUES(?,?,?,?)";

        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, expense.getDate().toString());
            stmt.setString(2, expense.getCategory());
            stmt.setDouble(3, expense.getAmount());
            stmt.setString(4, expense.getDescription());

            stmt.executeUpdate();

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<Expense> findAll(){

        List<Expense> list = new ArrayList<>();

        String sql = "SELECT * FROM expenses";

        try(Connection conn = DatabaseConfig.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){

                Expense e = new Expense(
                        rs.getInt("id"),
                        java.time.LocalDate.parse(rs.getString("date")),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getString("description")
                );

                list.add(e);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }
}

