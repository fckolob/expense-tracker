package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.*;

public class DashboardController {

    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> barChart;
    @FXML private LineChart<String, Number> lineChart;

    @FXML private TextField categoryField;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker datePicker;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> colDate;
    @FXML private TableColumn<Expense, String> colCategory;
    @FXML private TableColumn<Expense, Double> colAmount;
    @FXML private TableColumn<Expense, String> colDescription;
    @FXML private TableColumn<Expense, Void> colActions;

    @FXML private TextField filterCategoryField;
    @FXML private ComboBox<String> filterMonthBox;

    private final ExpenseService service = new ExpenseService();

    @FXML
    public void initialize() {

        setupTable();
        loadTableData();
        loadChartData();

        // ENTER to add
        categoryField.setOnAction(e -> addExpense());
        amountField.setOnAction(e -> addExpense());
        descriptionField.setOnAction(e -> addExpense());

        // Month filter
        filterMonthBox.setItems(FXCollections.observableArrayList(
                "All",
                "01 - January", "02 - February", "03 - March",
                "04 - April", "05 - May", "06 - June",
                "07 - July", "08 - August", "09 - September",
                "10 - October", "11 - November", "12 - December"
        ));
        filterMonthBox.setValue("All");
    }

    @FXML
    public void addExpense() {
        try {
            String category = categoryField.getText();
            String amountText = amountField.getText();
            String description = descriptionField.getText();

            if (category.isEmpty() || amountText.isEmpty()) return;

            double amount = Double.parseDouble(amountText);
            LocalDate date = (datePicker.getValue() != null) ? datePicker.getValue() : LocalDate.now();

            Expense expense = new Expense(date, category, amount, description);
            service.addExpense(expense);

            loadTableData();
            loadChartData();

            categoryField.clear();
            amountField.clear();
            descriptionField.clear();
            datePicker.setValue(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTable() {

        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDate().toString()));

        colCategory.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory()));

        colAmount.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getAmount()));

        colDescription.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));

        colActions.setCellFactory(param -> new TableCell<>() {

            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    Expense exp = getTableView().getItems().get(getIndex());
                    if (exp != null) editExpense(exp);
                });

                deleteBtn.setOnAction(e -> {
                    Expense exp = getTableView().getItems().get(getIndex());
                    if (exp == null) return;

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete");
                    alert.setHeaderText("Delete expense?");
                    alert.setContentText(exp.getCategory() + " - $" + exp.getAmount());

                    alert.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK) {
                            service.deleteExpense(exp.getId());
                            loadTableData();
                            loadChartData();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void editExpense(Expense expense) {

        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Edit Expense");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        DatePicker dp = new DatePicker(expense.getDate());
        TextField cat = new TextField(expense.getCategory());
        TextField amt = new TextField(String.valueOf(expense.getAmount()));
        TextField desc = new TextField(expense.getDescription());

        VBox box = new VBox(10,
                new Label("Date"), dp,
                new Label("Category"), cat,
                new Label("Amount"), amt,
                new Label("Description"), desc
        );

        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Expense(
                        expense.getId(),
                        dp.getValue(),
                        cat.getText(),
                        Double.parseDouble(amt.getText()),
                        desc.getText()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            service.updateExpense(updated);
            loadTableData();
            loadChartData();
        });
    }

    private void loadTableData() {
        expenseTable.setItems(FXCollections.observableArrayList(service.getExpenses()));
    }

    private void loadChartData() {
        updateCharts(service.getExpenses());
    }

   private void updateCharts(List<Expense> expenses) {

    // =======================
    //  PIE CHART (CATEGORY)
    // =======================
    Map<String, Double> catMap = new HashMap<>();

    for (Expense e : expenses) {
        catMap.put(
                e.getCategory(),
                catMap.getOrDefault(e.getCategory(), 0.0) + e.getAmount()
        );
    }

    categoryChart.getData().clear();
    catMap.forEach((k, v) ->
            categoryChart.getData().add(new PieChart.Data(k, v))
    );


    // =======================
    //  BAR CHART (MONTHS ORDERED)
    // =======================
    Map<Integer, Double> monthMap = new HashMap<>();

    for (Expense e : expenses) {
        int month = e.getDate().getMonthValue(); // 1–12
        monthMap.put(
                month,
                monthMap.getOrDefault(month, 0.0) + e.getAmount()
        );
    }

    barChart.getData().clear();
    XYChart.Series<String, Number> barSeries = new XYChart.Series<>();

    // SORT MONTHS
    monthMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String monthName = java.time.Month.of(entry.getKey()).toString();
                barSeries.getData().add(
                        new XYChart.Data<>(monthName, entry.getValue())
                );
            });

    barChart.getData().add(barSeries);


    // =======================
    //  LINE CHART (SORTED + GROUPED BY DATE)
    // =======================
    Map<String, Double> dateMap = new HashMap<>();

    for (Expense e : expenses) {
        String date = e.getDate().toString();

        dateMap.put(
                date,
                dateMap.getOrDefault(date, 0.0) + e.getAmount()
        );
    }

    lineChart.getData().clear();
    XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();

    // ✅ SORT DATES
    dateMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                lineSeries.getData().add(
                        new XYChart.Data<>(entry.getKey(), entry.getValue())
                );
            });

    lineChart.getData().add(lineSeries);
}

    @FXML
    public void applyFilters() {

        String category = filterCategoryField.getText().toLowerCase();
        String month = filterMonthBox.getValue();

        List<Expense> filtered = service.getExpenses().stream()
                .filter(e -> category.isEmpty() ||
                        e.getCategory().toLowerCase().contains(category))
                .filter(e -> {
                    if (month.equals("All")) return true;
                    String m = String.format("%02d", e.getDate().getMonthValue());
                    return m.equals(month.substring(0, 2));
                })
                .toList();

        expenseTable.setItems(FXCollections.observableArrayList(filtered));
        updateCharts(filtered);
    }

    @FXML
    public void clearFilters() {
        filterCategoryField.clear();
        filterMonthBox.setValue("All");
        loadTableData();
        loadChartData();
    }

    @FXML
    public void exportToExcel() {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save Excel");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        fc.setInitialFileName("expenses.xlsx");

        File file = fc.showSaveDialog(null);
        if (file == null) return;

        try (var wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            var sheet = wb.createSheet("Expenses");

            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Date");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Description");

            int i = 1;
            for (Expense e : service.getExpenses()) {
                var row = sheet.createRow(i++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getDate().toString());
                row.createCell(2).setCellValue(e.getCategory());
                row.createCell(3).setCellValue(e.getAmount());
                row.createCell(4).setCellValue(e.getDescription());
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}