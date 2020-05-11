package Photos.Controller;

import Photos.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileNotFoundException;


/**
 * Handles the login screen for the user. Takes username and password input, checks if its
 * in the database, and allows the user into the corresponding system, or rejects it.
 * !! The loginController is the first to get the database.
 * @author Krysti Leong
 * @author April Dizon
 */
public class LoginController extends Controller{

    // Buttons
    @FXML private Button loginButton;
    @FXML private Button quitButton;

    // Text Fields
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;

    /**
     * Initialize this scene. Because it's the first scene the user will see,
     * this will get the database.
     */
    @FXML private void initialize(){
        DBStatus status = null;
        try {
            status = getDatabase();
        } catch (FileNotFoundException e) {
            showError("Some photos were not found on this machine. :(");
        }
        handleDBStatus(status);
    }

    /**
     * Set the data given to local fields.
     * @param currentUser Current user in this session, if any.
     * @param currentAlbum Current selected album in this session, if any.
     * @param db Current database of this session, if any.
     */
    protected void initData(User currentUser, Album currentAlbum, Database db){
        setFields(null, null, db);
    }

    /**
     * When the user clicks the login button, attempt to log the user into the
     * application, iff the user is registered in the database.
     * @param event Action event of the button click.
     */
    @FXML
    protected void handleLoginButton(ActionEvent event){

        // Error checking.
        if (db == null){
            handleDBStatus(DBStatus.DB_NOT_FOUND);
            return;
        }

        // Get the inputs from the user.
        String username = usernameField.getText();
        username = removeWhitespaces(username);
        String password = passwordField.getText();
        password = removeWhitespaces(password);

        // See if this is a valid user.
        if (db.authorize(username, password)){
            this.currentUser = db.getUser(username);
            Stage stage = (Stage)loginButton.getScene().getWindow();
            if(Database.isAdmin(currentUser)){
                // Allow user into the admin subsystem.
                changeScene(stage, adminScene);
            }
            else{
                // Test if we can load any of these photos.
                DBStatus attempt = db.attemptPhotoLoad(currentUser);
                if (attempt == DBStatus.FAILURE){
                    showError("Some photos were not found on this machine. They were removed from this session.");
                }
                changeScene(stage, albumScene);
            }
        }
        else{
            showError("Username or password is incorrect.");
        }
    }


    /**
     * Handles the quit button. Simply closes the application.
     * @param event Action event of the button click.
     */
    @FXML protected void handleQuitButton(ActionEvent event){
        handleExit();
    }

}
