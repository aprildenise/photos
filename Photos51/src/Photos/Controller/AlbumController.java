package Photos.Controller;

import Photos.Model.Album;
import Photos.Model.DBStatus;
import Photos.Model.Database;
import Photos.Model.User;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;


/**
 * Controller that handles the Album view, which displays the list of albums of the currently
 * logged in user. Allows the user to add, search, delete, and open albums, and logout.
 * @author Krysti Leong
 * @author April Dizon
 */
public class AlbumController extends Controller{

    @FXML private Label welcomeNote;
    @FXML private Button addButton;
    @FXML private Button searchButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    @FXML private TableView tableView;
    @FXML private TableColumn<Album, String> nameColumn;
    @FXML private TableColumn<Album, String> numColumn;
    @FXML private TableColumn<Album, String> dateColumn;
    @FXML private TableColumn<Album, Album> optionColumn;

//    /**
//     * Local list of Albums that will be used to update the UI.
//     */
//    private List<Album> currentAlbums;

    /**
     * Init the scene.
     */
    @FXML private void initialize(){
        // Init the table view.
        nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
        numColumn.setCellValueFactory(new PropertyValueFactory("numPhotos"));
        dateColumn.setCellValueFactory(new PropertyValueFactory("dateModified"));

        // Initialize options column
        optionColumn.setCellValueFactory(currAlbum -> new ReadOnlyObjectWrapper<>(currAlbum.getValue()));
        Callback<TableColumn<Album, Album>, TableCell<Album, Album>> cellFactory = handleCellFactory();
        optionColumn.setCellFactory( cellFactory );
        tableView.setFixedCellSize(40);
        tableView.setItems(tableView.getItems());
    }

    /**
     * Set the data and show it on the screen!
     * @param currentUser Current user in this session, if any.
     * @param currentAlbum Current selected album in this session, if any.
     * @param db Current database of this session, if any.
     */
    protected void initData(User currentUser, Album currentAlbum, Database db){

        // Init the data.
        setFields(currentUser,currentAlbum, db);
        if (currentUser == null){
            showError("User could not be properly loaded.");
        }
        welcomeNote.setText("Welcome, " + currentUser.getUsername() + "! These are your albums.");

        // Show the information.
        List<Album> userAlbums = currentUser.getAlbums();
        if (userAlbums != null){
            //currentAlbums = userAlbums;
            tableView.getItems().setAll(userAlbums);
        }
    }

    /**
     * Handles if the user wants to search for photos through through the search button.
     * Changes the scene
     * @param actionEvent Action event of the button click.
     */
    @FXML private void handleSearch(ActionEvent actionEvent) {
        // Change the scene.
        Stage stage = (Stage) searchButton.getScene().getWindow();
        changeScene(stage, searchScene);

    }


    /**
     * Used purely to set up the scene. Gives a cell a set of buttons that will
     * be used for the user.
     * @return Cell factory created.
     */
    private Callback<TableColumn<Album, Album>, TableCell<Album, Album>> handleCellFactory(){
        Callback<TableColumn<Album, Album>, TableCell<Album, Album>> cellFactory =
                new Callback<TableColumn<Album, Album>, TableCell<Album, Album>>() {
                    @Override
                    public TableCell<Album, Album> call( final TableColumn<Album, Album> param ) {
                        final TableCell<Album, Album> cell = new TableCell<Album, Album>() {
                            HBox hBox;
                            final Button viewBtn = new Button("open");
                            final Button editBtn = new Button("edit");
                            final Button deleteBtn = new Button("delete");
                            @Override
                            public void updateItem(Album album, boolean empty ) {
                                super.updateItem(album, empty);
                                if ( empty ) {
                                    setGraphic( null );
                                    setText( null );
                                } else {
                                    viewBtn.setOnAction(event -> handleView(event, album));
                                    editBtn.setOnAction(event -> handleEdit(event, album));
                                    deleteBtn.setOnAction(event -> handleDelete(event, album));
                                    hBox = new HBox();
                                    hBox.setSpacing(1);
                                    hBox.getChildren().addAll(editBtn, viewBtn, deleteBtn);
                                    setGraphic(hBox);
                                    setText( null );
                                }
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

    /**
     * Handle the add button an attempt to add a new album to this user's account.
     * Shows a dialog to get the album name, and makes sure that this name is not
     * a duplicate of another album name.
     * Any errors are displayed for the user in a dialog popup.
     * @param actionEvent Action event of the add button click.
     */
    public void handleAdd(ActionEvent actionEvent) {
        boolean notAdded = true;
        while (notAdded){
            // Get user input.
            TextInputDialog input = new TextInputDialog("Album name");
            input.setHeaderText("Name your new album.");
            Optional<String> result = input.showAndWait();
            if (!result.isPresent()){
                // User canceled.
                return;
            }
            String name = result.get();
            if (!name.isEmpty()){
                // Try to add this album.
                Album newAlbum = new Album(name);
                DBStatus attempt = db.addAlbum(currentUser, newAlbum);
                if (attempt == DBStatus.SUCCESS){
                    // Show this on the UI.
                    tableView.getItems().add(newAlbum);
                    notAdded = false;
                    return;
                }
                else{
                    handleDBStatus(attempt);
                }
            }
        }
        // Done!
    }

    /**
     * Edit the name of the album.
     * @param actionEvent Action event of the button click.
     * @param album Album to edit.
     */
    public void handleEdit(ActionEvent actionEvent, Album album) {
        // Get user input.
        boolean notAdded = true;
        while (notAdded){
            TextInputDialog input = new TextInputDialog(album.getName());
            input.setHeaderText("Name your new album.");
            Optional<String> result = input.showAndWait();
            if (!result.isPresent()){
                // User canceled.
                return;
            }
            String name = result.get();
            if (!name.isEmpty()){
                // Find any duplicates of this name.
                Album dup = db.getAlbum(currentUser, new Album(name));
                if (dup != null){
                    handleDBStatus(DBStatus.DUPLICATE_ALBUM);
                    continue;
                }

                // Get the database reference of this album.
                Album databaseAlbum = db.getAlbum(currentUser, album);
                if (databaseAlbum == null){
                    handleDBStatus(DBStatus.NO_ALBUM);
                }

                // Finally, we can edit it!
                tableView.getItems().remove(album);
                //album.setName(name);
                databaseAlbum.setName(name);
                tableView.getItems().add(databaseAlbum);
                tableView.refresh(); //Not the smartest, but works!
                notAdded = false;
                return;
            }
            else{
                showError("Please rename your album.");
            }
        }
    }

    /**
     * Handle the open/view action on an album.
     * Takes the user to the photo scene.
     * @param actionEvent ActionEvent of the button click.
     * @param album Album that was chosen.
     */
    public void handleView(ActionEvent actionEvent, Album album){
        this.currentAlbum = db.getAlbum(currentUser, album);
        Stage stage = (Stage)searchButton.getScene().getWindow();
        changeScene(stage, photoScene);
    }


    /**
     * Handles when the user wants to delete an album.
     * Any errors are shown in a dialog popup.
     * @param actionEvent ActionEvent of this button
     * @param album The Album to be deleted.
     */
    public void handleDelete(ActionEvent actionEvent, Album album) {
        // Show a confirmation for deletion.
        if (showWarning("Are you sure you want to delete " + album.getName() + "?")){
            // Remove this album from the list and the db.
            DBStatus attempt = db.deleteAlbum(currentUser, album);
            if (attempt == DBStatus.SUCCESS){
                tableView.getItems().remove(album);
            }
            else{
                // Something went wrong.
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
        currentAlbum = null;
        changeScene(stage, loginScene);
    }
}
