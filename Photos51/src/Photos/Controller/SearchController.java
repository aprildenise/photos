package Photos.Controller;

import Photos.Model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Handles the Search action of the user.
 */
// TODO: How to use the same tableview from photo.fxml?
public class SearchController extends Controller{


    @FXML private Button logoutButton;

    @FXML private Button dateButton;
    @FXML private Button tagButton;

    @FXML private TableView tableView;
    @FXML private TextField startDate;
    @FXML private TextField endDate;
    @FXML private ComboBox<String> operation;
    private final String noOp = "NA";
    private final String conjunction = "and";
    private final String disjunction = "or";
    @FXML private TextField tag1;
    @FXML private TextField tag2;

    @FXML private Button backButton;
    @FXML private Button leftButton;
    @FXML private Button rightButton;

    @FXML private ImageView imageView;
    @FXML private TableColumn<Photo, ImageView> thumbColumn;
    @FXML private TableColumn<Photo, String> captionColumn;

    private List<Photo> photos;

    @FXML private void initialize(){

        // Set up the scene keeping the searchButton disabled.
        // The button can be enabled once the user inputs fields for the date or tag.
        BooleanBinding dateFields = startDate.textProperty().isEmpty().or(endDate.textProperty().isEmpty());
        dateButton.disableProperty().bind(dateFields);

        // Specifically for the tag button, the rightmost field can only be enabled when
        // the combo box has a valid value.
        BooleanBinding noOperation = (operation.valueProperty().isEqualTo("NA"));
        tag2.disableProperty().bind(noOperation);
        BooleanBinding invalidLeftTag = tag1.textProperty().isEmpty();
        BooleanBinding invalidRightTag = Bindings.and(tag2.textProperty().isEmpty(), Bindings.not(noOperation));
        tagButton.disableProperty().bind(invalidLeftTag.or(invalidRightTag));

        // Set up the tableView.
        thumbColumn.setCellValueFactory(new PropertyValueFactory("imageView"));
        captionColumn.setCellValueFactory(new PropertyValueFactory("caption"));
        // Add listener to selection model
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> selectItem());
        // test
        //photos = new ArrayList<Photo>();
    }

//    @Override
//    protected void initData(User currentUser, Album currentAlbum, Database db) {
//        this.currentUser = currentUser;
//        this.currentAlbum = currentAlbum;
//        this.db = db;
//    }


    /**
     * Handles when the user wants to search for photos by a date range.
     * Both date textFields must be inputted for a valid search.
     * @param event
     */
    public void handleDateSearch(ActionEvent event) {

        // Get the input from the user.
        String date1 = removeWhitespaces(startDate.getText());
        String date2 = removeWhitespaces(endDate.getText());

        Calendar start;
        Calendar end;
        try {
            start = Photo.stringToCalendar(date1);
            end = Photo.stringToCalendar(date2);
        } catch (ParseException e) {
            showError("Invalid input. Please write dates in the following format: DD/MM/YYYY");
            return;
        }

        // Then just get the photos!
        List<Photo> found = db.searchByDate(currentUser, start, end);
        photos = found;
        if (photos.isEmpty()){
            showError("No photos found.");
        }
        else{
            tableView.getItems().setAll(photos);
            showSuccess("Found photos!");
        }
    }

    /**
     * Handles when the user wants to search for photos by tags.
     * Conjunctions and Disjunctions will only be used if the operations combo
     * box is appropriately used.
     * @param event
     */
    public void handleTagSearch(ActionEvent event){

        // Get input from the user and check if both (or one) are valid.
        String input1 = removeWhitespaces(tag1.getText());
        String input2 = removeWhitespaces(tag2.getText());
        Tag t1 = Tag.stringToTag(input1);
        Tag t2 = Tag.stringToTag(input2);
        String op = operation.getValue();
        if (t1 == null) {
            showError("Invalid input. Please write tags in the following format: tag=value");
            return;
        }
        List<Photo> found = null;
        // Good to start the search!
        if (op.equals(noOp)){
            found = db.searchByTag(currentUser, t1);
        }
        else{
            // Last minute check.
            if (t2 == null){
                showError("Invalid input. Please write tags in the following format: tag=value. Spaces are allowed in tags.");
                return;
            }
            if (op.equals(conjunction)){
                found = db.searchByTagConjunctive(currentUser, t1, t2);
            }
            else if (op.equals(disjunction)){
                found = db.searchByTagDisjunctive(currentUser, t1, t2);
            }
            else{
                //????
            }
        }
        photos = found;
        if (photos.isEmpty()){
            showError("No photos found.");
        }
        else{
            tableView.getItems().setAll(photos);
            showSuccess("Found photos!");
        }

    }

    protected void selectItem(){
        if(!tableView.getItems().isEmpty()){
            //Populate photo detailed view
            Photo photo = (Photo) tableView.getSelectionModel().getSelectedItem();
            imageView.setImage(new Image(photo.getPath()));
            leftButton.setDisable(false);
            rightButton.setDisable(false);
        }else{
            imageView.setImage(null);
            leftButton.setDisable(true);
            rightButton.setDisable(true);
        }

        //handle navigational buttons clickable logic
        int photoIndex = tableView.getSelectionModel().getSelectedIndex();
        if(photoIndex == 0) leftButton.setDisable(true);
        else leftButton.setDisable(false);

        if(photoIndex == photos.size()-1) rightButton.setDisable(true);
        else rightButton.setDisable(false);

    }

    protected void initData(User currentUser, Album currentAlbum, Database db) {
        this.currentUser = currentUser;
        this.currentAlbum = currentAlbum;
        this.db = db;
        photos = new ArrayList<>();
        if (currentUser == null){
            showError("User could not be properly loaded.");
        }
        // Display the photos.
        tableView.getItems().setAll(photos);
        tableView.getSelectionModel().select(0);         // Handle auto select first image
        selectItem();
    }


    /**
     *
     * @param actionEvent
     */
    public void handleAdd(ActionEvent actionEvent) {
        if (photos == null){
            showError("You haven't found any photos!");
            return;
        }
        if (photos.isEmpty()){
            showError("You haven't found any photos!");
            return;
        }
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
                newAlbum.setPhotos(photos);
                DBStatus attempt = db.addAlbum(currentUser, newAlbum);
                if (attempt == DBStatus.SUCCESS){
                    // Show this on the UI.
                    tableView.getItems().add(newAlbum);
                    notAdded = false;
                    showSuccess("See your new album in the album page!");
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
     * Handle when the user wants to go back to the login page.
     * @param actionEvent Action event for the back button.
     */
    public void handleBack(ActionEvent actionEvent) {
        Stage stage = (Stage)backButton.getScene().getWindow();
        currentAlbum = null;
        changeScene(stage, albumScene);
    }

    public void handleLeft(ActionEvent actionEvent) {
        int photoIndex = tableView.getSelectionModel().getSelectedIndex();
        tableView.getSelectionModel().select(photoIndex-1);
    }

    public void handleRight(ActionEvent actionEvent) {
        int photoIndex = tableView.getSelectionModel().getSelectedIndex();
        tableView.getSelectionModel().select(photoIndex+1);
    }

    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage)backButton.getScene().getWindow();
        currentAlbum = null;
        currentUser = null;
        changeScene(stage, loginScene);
    }
}
