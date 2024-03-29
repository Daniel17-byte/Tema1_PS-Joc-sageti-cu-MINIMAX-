package org.danielsa.proiect_ps.views;

import jakarta.inject.Inject;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;
import org.danielsa.proiect_ps.Main;
import org.danielsa.proiect_ps.models.RegisterModel;
import org.danielsa.proiect_ps.models.UserType;
import org.danielsa.proiect_ps.presenters.DatabaseService;
import org.danielsa.proiect_ps.presenters.RegisterPresenter;

import java.io.IOException;

@Getter
public class LoginView extends Scene {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label resultLabel;
    private Button registerButton;

    @Inject
    private final DatabaseService databaseService;

    public LoginView(DatabaseService databaseService) {
        super(new VBox(), 300, 200);
        this.databaseService = databaseService;
        initComponents();
    }

    private void initComponents() {
        VBox root = (VBox) getRoot();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        usernameField = new TextField();
        usernameField.setPromptText("Username");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        loginButton = new Button("Login");

        registerButton = new Button("Register");

        registerButton.setOnAction(event -> openRegisterWindow());

        resultLabel = new Label();

        root.getChildren().addAll(usernameField, passwordField, loginButton, resultLabel, registerButton);
    }

    public void setOnLoginAttempt(LoginAttemptHandler handler) {
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            handler.onLoginAttempt(username, password);
        });
    }

    public void showLoginResult(boolean success) {
        if (success) {
            Stage primaryStage = (Stage) this.getWindow();

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);

                primaryStage.setScene(scene);
                primaryStage.setTitle("Arrow Game");

                primaryStage.show();

                GameView gameView = fxmlLoader.getController();
                gameView.setDatabaseService(databaseService);
                if (gameView.getDatabaseService().getUser().getUserType().equals(UserType.ADMIN)){
                    gameView.loadUserList();
                }
                gameView.loadWonGames();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            resultLabel.setText("Login failed. Please try again.");
        }
    }

    public interface LoginAttemptHandler {
        void onLoginAttempt(String username, String password);
    }

    private void openRegisterWindow() {

        RegisterView registerView = new RegisterView(databaseService);
        RegisterModel registerModel = new RegisterModel(databaseService);

        @SuppressWarnings("unused")
        RegisterPresenter registerPresenter = new RegisterPresenter(registerView, registerModel);

        Stage registerStage = new Stage();
        registerStage.setScene(registerView);
        registerStage.setTitle("Register");
        registerStage.show();
    }
}
