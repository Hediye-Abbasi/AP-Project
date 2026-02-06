package com.shoppingmall.controller;

import com.shoppingmall.config.ServiceFactory;
import com.shoppingmall.model.Product;
import com.shoppingmall.model.User;
import com.shoppingmall.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller for the administrator product management screen.
 */
public class AdminProductController implements UserAwareController {

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> categoryColumn;

    @FXML
    private TableColumn<Product, BigDecimal> priceColumn;

    @FXML
    private TableColumn<Product, Integer> stockColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField stockField;

    private final ProductService productService = ServiceFactory.productService();
    private ObservableList<Product> products;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        nameColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        categoryColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCategory()));
        priceColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPrice()));
        stockColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStockQuantity()));

        refreshTable();

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                categoryField.setText(selected.getCategory());
                priceField.setText(selected.getPrice().toString());
                stockField.setText(String.valueOf(selected.getStockQuantity()));
            }
        });
    }

    @FXML
    private void handleAdd() {
        try {
            Product product = new Product();
            product.setName(nameField.getText());
            product.setCategory(categoryField.getText());
            product.setPrice(new BigDecimal(priceField.getText()));
            product.setStockQuantity(Integer.parseInt(stockField.getText()));
            product.setActive(true);
            productService.createProduct(product);
            refreshTable();
            clearForm();
        } catch (Exception e) {
            showError("Invalid input", "Please check the fields and try again.");
        }
    }

    @FXML
    private void handleUpdate() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Select a product to update.");
            return;
        }
        try {
            selected.setName(nameField.getText());
            selected.setCategory(categoryField.getText());
            selected.setPrice(new BigDecimal(priceField.getText()));
            selected.setStockQuantity(Integer.parseInt(stockField.getText()));
            productService.updateProduct(selected);
            refreshTable();
        } catch (Exception e) {
            showError("Invalid input", "Please check the fields and try again.");
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Select a product to delete.");
            return;
        }
        productService.deleteProduct(selected.getId());
        refreshTable();
        clearForm();
    }

    @FXML
    private void handleLogout() {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(AdminProductController.class.getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) productTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(AdminProductController.class.getResource("/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Shopping Mall");
        } catch (IOException e) {
            showError("Navigation error", "Unable to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        products = FXCollections.observableArrayList(productService.getAllProducts());
        productTable.setItems(products);
    }

    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        priceField.clear();
        stockField.clear();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void setCurrentUser(User user) {
        // not used currently but kept for consistency
    }
}


