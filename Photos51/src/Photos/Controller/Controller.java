package Photos.Controller;

import Photos.Model.Album;
import Photos.Model.DBStatus;
import Photos.Model.Database;
import Photos.Model.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Statement;

/**
 * Abstract class that defines the functions for all the controllers in this application.
 * All controllers SHOULD derive from this class.
 * Handles scene changes, saving, loading, errors handling, etc.
 * @author Krysti Leong
 * @author April Dizon
 */
public abstract class Controller {

    // TODO: is there a better way to get the .fxmls without manually writing the directory here?
    protected static final String adminScene = "../View/admin.fxml";
    protected static final String loginScene = "../View/login.fxml";
    protected static final String albumScene = "../View/album.fxml";
    protected static final String photoScene = "../View/photo.fxml";
    protected static final String searchScene = "../View/search.fxml";


    protected User currentUser;
    protected Album currentAlbum;
    protected Database db;


    /**
     * Called before a scene is changed in changeScenes().
     * Pass and save data between the given scenes.
     * @param currentUser Current user in this session, if any.
     * @param currentAlbum Current selected album in this session, if any.
     * @param db Current database of this session, if any.
     */
    protected abstract void initData(User currentUser, Album currentAlbum, Database db);


    /**
     * Set the given fields. Used in initData.
     * @param currentUser User to pass.
     * @param currentAlbum Album to pass.
     * @param db Database to pass.
     */
    protected void setFields(User currentUser, Album currentAlbum, Database db){
        this.currentUser = currentUser;
        this.currentAlbum = currentAlbum;
        this.db = db;
    }

    /**
     * Get the database of the application. If the current database is null, create
     * a new one and load data into it. If a database already exists, then return it.
     * Should be called only once.
     * @return Database of the application.
     */
    protected DBStatus getDatabase() throws FileNotFoundException {
        if (db == null){
            db = new Database();
        }
        return DBStatus.SUCCESS;
    }

//    /**
//     * Save the current database stored in the db variable.
//     */
//    protected void saveDatabase(){
//        db.saveCurrentDatabase();
//    }

    /**
     * Handle the exit function of this application. First saves the database, and then
     * ends the application.
     */
    protected void handleExit(){
        //controller.Dispose();
        //System.out.println("handled exit.");
        db.saveCurrentDatabase();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Change the scene by loading another .fxml file.
     * @param stage Current stage?
     * @param fxmlFile .fxml file of the scene to load.
     */
    protected void changeScene(Stage stage, String fxmlFile){

        // Save the data from this session and load up a new scene.
        db.saveCurrentDatabase();
        Parent root = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            //root = loader.load(getClass().getResource(fxmlFile));
            //loader.setRoot(root);
            loader = new FXMLLoader(getClass().getResource(fxmlFile));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not connect. Please try again later.");
            return;
        }

        // Init the scene and show it.
        Controller controller = loader.getController();
        controller.initData(currentUser, currentAlbum, db);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> handleExit());
    }

//    /**
//     * Check if the database is present. Used to catch an errors and make sure
//     * the app doesn't crash.
//     * @return True if database != null, false elsewise.
//     */
//    protected boolean databaseIsWorking(){
//        if (db == null){
//            showError("Database cannot be loaded. Please try again later.");
//            return false;
//        }
//        else{
//            return true;
//        }
//    }
    /**
     *  Display a success alert on the screen for the user.
     * @param message Message to display on the alert.
     */

    protected void showSuccess(String message){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK){
            alert.close();
            return;
        }
    }
    /**
     * Display an error alert on the screen for the user.
     * @param message Message to display on the alert.
     */
    protected void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK){
            alert.close();
            return;
        }
    }

    /**
     * Display an warning alert on the screen for the user.
     * @param message Message to display on the alert.
     * @return True if the user clicked YES, false elsewise.
     */
    protected boolean showWarning(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            return true;
        }{
            return false;
        }
    }

    /**
     * Utility function to remove whitespaces for a string.
     * Used when collecting user input.
     * @param s String
     * @return Given string, with whitespaces removed.
     */
    public static String removeWhitespaces(String s){
        return s.replaceAll("\\s+","");
    }

    /**
     * Show an error dialog depending on what the given DBstatus is.
     * @param status Status to handle.
     */
    protected void handleDBStatus(DBStatus status){
        if (status == DBStatus.NO_USER){
            showError("User was not found in the database.");
        }
        else if (status == DBStatus.NO_ALBUM){
            showError("Album was not found in the database.");
        }
        else if (status == DBStatus.DUPLICATE_ALBUM){
            showError("This album already exists.");
        } else if (status == DBStatus.ADMIN) {
            showError("Cannot delete the admin.");
        }
        else if (status == DBStatus.DUPLICATE_TAG){
            showError("This tag already exists.");
        }
        else if (status == DBStatus.NOT_MULTIPLE_TAG){
            showError("This tag does not allow multiples.");
        }
    }
}
