package Photos.Controller;

import Photos.Model.Album;
import Photos.Model.DBStatus;
import Photos.Model.Database;
import Photos.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

/**
 * Defines the functions of the admin subsystem, with this controller and admin.fxml.
 * Handles adding new users and deleting new users.
 * The admin user can never be deleted.
 * The stock user can be deleted, but will appear again on restart.
 * @author Krysti Leong
 * @author April Dizon
 */
public class AdminController extends Controller{


    @FXML private TableView tableView;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> passwordColumn;
    @FXML private TableColumn<User, Integer> albumsColumn;

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    /**
     * Initialize this scene by loading displaying the list of users that are
     * currently in the database.
     */
    @FXML private void initialize(){

        // Init the tableView.
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> selectItem());
        usernameColumn.setCellValueFactory(new PropertyValueFactory("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory("password"));
        albumsColumn.setCellValueFactory(new PropertyValueFactory("numAlbums"));

    }

    /**
     * Inits the data for this controller, and shows it on the screen.
     * @param currentUser Current user in this session, if any.
     * @param currentAlbum Current selected album in this session, if any.
     * @param db Current database of this session, if any.
     */
    protected void initData(User currentUser, Album currentAlbum, Database db){
        // Set the data and display it on the UI.
        setFields(currentUser, null, db);

        List<User> users = this.db.getUsers();
        if (users == null || users.size() == 0){
            // Should never happen, but here just in case.
        }
        else{
            tableView.getItems().setAll(users);
        }
    }

    /**
     * Handles whenever the user selects an item in the userListView.
     * Rarely used but here just in case.
     */
    @FXML private void selectItem(){
        return;
    }


    /**
     * Handle when the user wants to add a new user by getting input from the
     * usernameField and passwordField, checking if this user is valid by the Database db,
     * and then prompting the result for the user.
     * @param actionEvent ActionEvent for clicking the add Button.
     */
    @FXML private void handleAdd(ActionEvent actionEvent) {
        // Get input from the text fields.
        String username = removeWhitespaces(usernameField.getText());
        String password = removeWhitespaces(passwordField.getText());

        if (username.equals("")){
            showError("Input a user information to add.");
            return;
        }

        // Add these fields to a new user, and add it to the database.
        User newUser = new User(username, password);
        if (db.addUser(newUser)){
            // Everything is good! Update the observableList
            tableView.getItems().setAll(db.getUsers());

            // Clear the fields.
            usernameField.setText("");
            passwordField.setText("");
        }
        else{
            showError("User already exists.");
        }
    }


    /**
     * Handle when the user wants to delete the currently selected user.
     * The user cannot delete the admin.
     * @param actionEvent ActionEvent for this delete button.
     */
    @FXML private void handleDelete(ActionEvent actionEvent) {
        // Get the currently selected item.
        if (!tableView.getSelectionModel().isEmpty()) {
            //int index = tableView.getSelectionModel().getSelectedIndex();
            User user = (User) tableView.getSelectionModel().getSelectedItem();

            System.out.println("attempt to delete:" + user);

            // Attempt to delete the user.
            DBStatus attempt = db.deleteUser(user);
            if (attempt == DBStatus.SUCCESS) {
                if (showWarning("Are you sure you want to delete " + user.getUsername() + "?")) {
                    //userObservableList.remove(user);
                    tableView.getItems().remove(user);
                }
            } else {
                handleDBStatus(attempt);
            }
        }
    }

    /**
     * Handle when the user wants to go back to the login page.
     * @param actionEvent Action event for the back button.
     */
    @FXML private void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage)backButton.getScene().getWindow();
        currentUser = null;
        changeScene(stage, loginScene);
    }
}
