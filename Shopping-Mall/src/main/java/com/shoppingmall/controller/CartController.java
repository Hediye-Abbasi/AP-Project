package com.shoppingmall.controller;

import com.shoppingmall.config.ServiceFactory;
import com.shoppingmall.model.CartItem;
import com.shoppingmall.model.Order;
import com.shoppingmall.model.User;
import com.shoppingmall.service.CartService;
import com.shoppingmall.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Controller for the shopping cart and checkout screen.
 */
public class CartController implements UserAwareController {

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, BigDecimal> totalColumn;

    @FXML
    private TableColumn<CartItem, Void> actionsColumn;

    @FXML
    private Label totalLabel;

    private final CartService cartService = ServiceFactory.cartService();
    private final OrderService orderService = ServiceFactory.orderService();

    private User currentUser;
    private ObservableList<CartItem> items;

    @FXML
    private void initialize() {
        productColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getProduct().getName()));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalColumn.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(
                c.getValue().getTotalPrice()));

        // Setup actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button increaseBtn = new Button("+");
            private final Button decreaseBtn = new Button("-");
            private final Button removeBtn = new Button("Remove");
            private final HBox buttonsBox = new HBox(5, increaseBtn, decreaseBtn, removeBtn);

            {
                increaseBtn.getStyleClass().add("small-button");
                decreaseBtn.getStyleClass().add("small-button");
                removeBtn.getStyleClass().add("danger-button");
                removeBtn.setStyle("-fx-font-size: 11px;");

                increaseBtn.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartService.increaseQuantity(item.getProduct());
                    refreshTable();
                });

                decreaseBtn.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartService.decreaseQuantity(item.getProduct());
                    refreshTable();
                });

                removeBtn.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartService.removeFromCart(item.getProduct());
                    refreshTable();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
                }
            }
        });

        refreshTable();
    }

    @FXML
    private void handleCheckout() {
        if (cartService.getItems().isEmpty()) {
            showError("Empty Cart", "Your cart is empty. Add items before checkout.");
            return;
        }
        try {
            Order order = orderService.checkout(currentUser, cartService.getItems());
            cartService.clearCart();
            refreshTable();
            showInfo("Purchase successful", "Order #" + order.getId() + " has been created.");
        } catch (RuntimeException e) {
            showError("Checkout failed", e.getMessage());
        }
    }

    @FXML
    private void handleClearCart() {
        if (cartService.getItems().isEmpty()) {
            showInfo("Cart Empty", "Your cart is already empty.");
            return;
        }
        cartService.clearCart();
        refreshTable();
        showInfo("Cart Cleared", "All items have been removed from your cart.");
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(CartController.class.getResource("/fxml/CatalogView.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserAwareController userAwareController) {
                userAwareController.setCurrentUser(currentUser);
            }
            Stage stage = (Stage) cartTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(CartController.class.getResource("/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Shopping Mall - Catalog");
        } catch (IOException e) {
            showError("Navigation error", "Unable to go back to catalog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        cartService.clearCart();
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(CartController.class.getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cartTable.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(CartController.class.getResource("/css/dark-theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Shopping Mall");
        } catch (IOException e) {
            showError("Navigation error", "Unable to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    private void refreshTable() {
        items = FXCollections.observableArrayList(cartService.getItems());
        cartTable.setItems(items);
        BigDecimal total = cartService.getTotalAmount();
        totalLabel.setText("Total: $" + total);
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
}


