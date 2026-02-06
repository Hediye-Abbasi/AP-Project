package com.shoppingmall.controller;

import com.shoppingmall.config.ServiceFactory;
import com.shoppingmall.model.Product;
import com.shoppingmall.model.User;
import com.shoppingmall.service.AuthenticationService;
import com.shoppingmall.service.CartService;
import com.shoppingmall.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the customer product catalog screen.
 */
public class CatalogController implements UserAwareController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label balanceLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortCombo;

    @FXML
    private ListView<Product> productListView;

    private final ProductService productService = ServiceFactory.productService();
    private final CartService cartService = ServiceFactory.cartService();
    private final AuthenticationService authenticationService = ServiceFactory.authenticationService();

    private User currentUser;
    private ObservableList<Product> products = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        sortCombo.getItems().addAll(
                "Name ↑",
                "Name ↓",
                "Price ↑",
                "Price ↓"
        );
        sortCombo.getSelectionModel().selectFirst();

        productListView.setCellFactory(list -> new ProductCell());

        refreshProducts();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        List<Product> result = productService.searchProducts(query);
        applySortAndShow(result);
    }

    @FXML
    private void handleSortChange() {
        applySortAndShow(products);
    }

    @FXML
    private void handleViewCart() {
        try {
            FXMLLoader loader = new FXMLLoader(CatalogController.class.getResource("/fxml/CartView.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAwareController) {
                userAwareController.setCurrentUser(currentUser);
            }
            Stage stage = (Stage) productListView.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(CatalogController.class.getResource("/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Your Cart");
        } catch (IOException e) {
            showError("Navigation error", "Unable to open cart screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChargeBalance() {
        if (currentUser == null) {
            showError("Error", "User session not found.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Charge Balance");
        dialog.setHeaderText("Add funds to your account");
        dialog.setContentText("Enter amount to charge:");

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dark-theme.css").toExternalForm());

        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showError("Invalid Amount", "Please enter a positive amount.");
                    return;
                }
                if (amount > 10000) {
                    showError("Invalid Amount", "Maximum charge amount is $10,000.");
                    return;
                }

                BigDecimal chargeAmount = BigDecimal.valueOf(amount);
                User updatedUser = authenticationService.updateBalance(currentUser, chargeAmount);
                currentUser = updatedUser;
                updateBalanceLabel();
                showInfo("Balance Charged", 
                        String.format("Successfully charged $%.2f. New balance: $%.2f", 
                                amount, updatedUser.getBalance().doubleValue()));
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid number.");
            } catch (Exception e) {
                showError("Error", "Failed to charge balance: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleLogout() {
        cartService.clearCart();
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(CatalogController.class.getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) productListView.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(CatalogController.class.getResource("/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Shopping Mall");
        } catch (IOException e) {
            showError("Navigation error", "Unable to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshProducts() {
        List<Product> all = productService.getAllProducts();
        applySortAndShow(all);
    }

    private void applySortAndShow(List<Product> base) {
        String sort = sortCombo.getSelectionModel().getSelectedItem();
        List<Product> sorted = base;
        if ("Name ↑".equals(sort)) {
            sorted = productService.sortByName(base, true);
        } else if ("Name ↓".equals(sort)) {
            sorted = productService.sortByName(base, false);
        } else if ("Price ↑".equals(sort)) {
            sorted = productService.sortByPrice(base, true);
        } else if ("Price ↓".equals(sort)) {
            sorted = productService.sortByPrice(base, false);
        }
        products = FXCollections.observableArrayList(sorted);
        productListView.setItems(products);
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + user.getFullName());
        }
        updateBalanceLabel();
    }

    private void updateBalanceLabel() {
        if (balanceLabel != null && currentUser != null) {
            BigDecimal balance = currentUser.getBalance();
            balanceLabel.setText("Balance: $" + balance);
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Custom ListCell rendering products as "cards" with modern styling via CSS.
     */
    private class ProductCell extends ListCell<Product> {
        @Override
        protected void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            if (empty || product == null) {
                setGraphic(null);
            } else {
                Label name = new Label(product.getName());
                name.getStyleClass().add("product-name");

                Label price = new Label("$" + product.getPrice());
                price.getStyleClass().add("product-price");

                Label category = new Label(product.getCategory());
                category.getStyleClass().add("product-category");

                Label stock = new Label("In stock: " + product.getStockQuantity());
                stock.getStyleClass().add("product-stock");

                Button addButton = new Button("Add to Cart");
                addButton.getStyleClass().add("primary-button");
                addButton.setOnAction(e -> {
                    if (product.getStockQuantity() <= 0) {
                        showError("Out of Stock", "This product is currently out of stock.");
                    } else {
                        cartService.addToCart(product);
                    }
                });

                VBox details = new VBox(4, name, price, category, stock, addButton);
                details.getStyleClass().add("product-card");
                HBox container = new HBox(details);
                container.getStyleClass().add("product-card-container");
                setGraphic(container);
            }
        }
    }
}


