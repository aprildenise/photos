package Photos.Controller;

import Photos.Model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles the photo view that shows when opening an album. Displays photos
 * in a list on the left, and a slideshow of the photos on the right.
 * Users can edit the photo details on the right.
 * @author Krysti Leong
 * @author April Dizon
 */
public class PhotoController extends Controller {


    // Main function buttons
    @FXML protected Button addButton;
    @FXML protected Button backButton;
    @FXML protected Button logoutButton;

    // Slideshow view
    @FXML protected Button copyButton;
    @FXML protected Button moveButton;
    @FXML protected Button deleteButton;
    @FXML protected Button leftButton;
    @FXML protected Button rightButton;
    @FXML protected Button saveCaptionButton;
    @FXML protected Button addTagsButton;

    @FXML protected ImageView imageView;
    @FXML protected TableView tableView;
    @FXML protected TableView tagTableView;
    @FXML protected TableColumn<Photo, ImageView> thumbColumn;
    @FXML protected TableColumn<Photo, String> captionColumn;
    @FXML protected TableColumn<Photo, String> dateColumn;
    @FXML protected TableColumn<Tag, String> tagTypeColumn;
    @FXML protected TableColumn<Tag, String> tagValueColumn;
    @FXML protected TableColumn<Tag, Tag> tagOptionColumn;

    @FXML protected TextField captionTextField;
    @FXML protected TextField tagType;
    @FXML protected TextField tagValue;

    @FXML protected ComboBox presetTags;
    protected ObservableList<String> tagsList;

    /**
     * Local list of photos that will be used to update the UI.
     */
    protected List<Photo> photos;
    protected List<Tag> tags;

    /**
     * Init the scene by setting up the table view
     */
    @FXML private void initialize(){

        // Set up the tableView.
        thumbColumn.setCellValueFactory(new PropertyValueFactory("imageView"));
        captionColumn.setCellValueFactory(new PropertyValueFactory("caption"));
        dateColumn.setCellValueFactory(new PropertyValueFactory("dateModified"));
        tagTypeColumn.setCellValueFactory(currTag -> new SimpleStringProperty(currTag.getValue().getName()));
        tagValueColumn.setCellValueFactory(currTag -> new SimpleStringProperty(currTag.getValue().getValue()));
        tagOptionColumn.setCellValueFactory(currTag -> new ReadOnlyObjectWrapper<>(currTag.getValue()));
        Callback<TableColumn<Tag, Tag>, TableCell<Tag, Tag>> cellFactory = handleOptionCellFactory();
        tagOptionColumn.setCellFactory(cellFactory);

        // Add listener to selection model
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> selectItem());
        // test
        //photos = new ArrayList<Photo>();

        BooleanBinding customTag = Bindings.not(presetTags.valueProperty().isEqualTo(Tag.custom));
        tagType.disableProperty().bind(customTag);
    }

    protected void selectItem(){
        if(!tableView.getItems().isEmpty()){
            //Populate photo detailed view
            Photo photo = (Photo) tableView.getSelectionModel().getSelectedItem();
            if(photo == null){
                return;
            }
            tags = photo.getTagsList();
            if(tags != null){
                tagTableView.getItems().setAll(tags);
            }
            captionTextField.setText(photo.getCaption());
            imageView.setImage(new Image(photo.getPath()));
            copyButton.setDisable(false);
            moveButton.setDisable(false);
            deleteButton.setDisable(false);
            leftButton.setDisable(false);
            rightButton.setDisable(false);
            saveCaptionButton.setDisable(false);
            addTagsButton.setDisable(false);

        }else{
            imageView.setImage(null);
            tagTableView.getItems().setAll(new ArrayList());
            captionTextField.setText("");
            copyButton.setDisable(true);
            moveButton.setDisable(true);
            deleteButton.setDisable(true);
            leftButton.setDisable(true);
            rightButton.setDisable(true);
            saveCaptionButton.setDisable(true);
            addTagsButton.setDisable(true);
        }

        //handle navigational buttons clickable logic
        int photoIndex = tableView.getSelectionModel().getSelectedIndex();
        if(photoIndex == 0) leftButton.setDisable(true);
        else leftButton.setDisable(false);

        if(photoIndex == photos.size()-1) rightButton.setDisable(true);
        else rightButton.setDisable(false);

    }

    protected void initData(User currentUser, Album currentAlbum, Database db) {
        setFields(currentUser,currentAlbum, db);
        photos = currentAlbum.getPhotos();
        if (currentUser == null || currentAlbum == null){
            // Shouldn't happen, but just in case.
            showError("User or album could not be properly loaded.");
        }
        // Set up the preset tags.
        // Not the best way to do this but will work for now.
        List<String> tagsToStrings = new ArrayList<String>();
        for (Tag tag: currentUser.getCreatedTags()){
            tagsToStrings.add(tag.name);
        }
        tagsList = FXCollections.observableList(tagsToStrings);
        presetTags.setItems(tagsList);

        // Display the photos.
        tableView.getItems().setAll(photos);
        tableView.getSelectionModel().select(0); // Handle auto select first image
        selectItem();

    }

    /**
     *
     * @param actionEvent
     */
    public void handleAdd(ActionEvent actionEvent) {

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = chooser.showOpenDialog(addButton.getScene().getWindow());
        if (file == null){
            showError("File could not be found.");
            return;
        }
        // Create a new photo object for this photo and add it to the necessary structures.
        Photo photo = null;
        try {
            photo = new Photo(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            showError("This photo could not be loaded.");
            return;
            //e.printStackTrace();
        }
        db.addPhoto(currentUser, currentAlbum, photo);
        tableView.getItems().setAll(photos);
        tableView.getSelectionModel().select(photos.size()-1);
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


    public boolean cloneLogic(String cloneType) {
        Dialog<Album> dialog = new Dialog<>();
        dialog.setTitle(cloneType + " photo");
        dialog.setHeaderText("Select an album!");
        Label moveLabel = new Label("Destination: ");
        List<String> albumNames = new ArrayList<>();
        for(Album album : currentUser.getAlbums())
        {
            String name = album.getName();
            if(!currentAlbum.getName().equals(name)) {
                albumNames.add(name);
            }
        }
        if(albumNames.isEmpty()){
            showError("No albums to move to");
            return false;
        }
        ObservableList<String> options = FXCollections.observableArrayList(albumNames);
        ComboBox<String> moveComboBox = new ComboBox<>(options);
        GridPane grid = new GridPane();
        grid.add(moveLabel, 1, 1);
        grid.add(moveComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType(cloneType, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        Optional<Album> result = dialog.showAndWait();
        if (result.isPresent()) {
            String destAlbumName = moveComboBox.getSelectionModel().getSelectedItem();
            Album destAlbum = currentUser.getAlbumByName(destAlbumName);
            if(destAlbum == null){
                System.out.println("Something went wrong with getting the album reference");
                return false;
            }
            try{
                Photo currPhoto = (Photo) tableView.getSelectionModel().getSelectedItem();
                Photo currPhotoClone = (Photo) currPhoto.clone();
                destAlbum.addPhoto(currPhotoClone);
            }catch(CloneNotSupportedException e){
                showSuccess("Clone failed");
                e.printStackTrace();
            }
            showSuccess(cloneType + ": success!");
            return true;
        }
        return false;
    }

    /**
     * Handle when the user wants to copy a photo from one album to another
     * @param actionEvent
     */
    public void handleCopy(ActionEvent actionEvent) {
        cloneLogic("Copy");
    }

    /**
     * Handle when the user wants to move a photo from one album to another
     * @param actionEvent
     */
    public void handleMove(ActionEvent actionEvent) {
        if(cloneLogic("Move")){
            handleDelete(actionEvent);
        }else{
            System.out.println("Error: something went wrong while trying to clone this photo");
        }

    }

    /**
     * Handle when the user wants to copy a photo from one album to another
     * @param actionEvent
     */
    public void handleDelete(ActionEvent actionEvent) {
        currentAlbum.getPhotos().remove(tableView.getSelectionModel().getSelectedItem());
        tableView.getItems().setAll(photos);
        currentAlbum.updateDate();
    }

    /**
     * Handle photo preview navigation to the left
     * @param actionEvent
     */
    public void handleLeft(ActionEvent actionEvent) {
        int photoIndex = tableView.getSelectionModel().getSelectedIndex();
        tableView.getSelectionModel().select(photoIndex-1);
    }

    /**
     * Handle photo preview navigation to the right
     * @param actionEvent
     */
    public void handleRight(ActionEvent actionEvent) {
        int photoIndex = tableView.getSelectionModel().getSelectedIndex();
        tableView.getSelectionModel().select(photoIndex+1);
    }


    @FXML
    protected void handleSaveCaption(ActionEvent actionEvent){
        Photo photo = (Photo) tableView.getSelectionModel().getSelectedItem();
        String caption = captionTextField.getText();
        if(!caption.isEmpty()){
            photo.setCaption(caption);
        }
        else{
            showError("You cannot rename a photo to an empty caption.");
            return;
        }
        showSuccess("Successfully saved caption!");
        tableView.getItems().setAll(photos);
    }

    @FXML
    protected void handleAddTags(ActionEvent actionEvent){
        String tagTypeText = tagType.getText();
        String tagValueText = tagValue.getText();
        // Check if we're NOT using a custom tag.
        boolean multiple = false;
        if (presetTags.getValue() == null){
            showError("You're missing a tag or a value. Please input them. Spaces are allowed in tags.");
            return;
        }
        Tag preset = currentUser.getCreatedTag(tagType.getText());
        if (!presetTags.getValue().toString().equals(Tag.custom)){
            // If we are using a preset, use the text from the combo box
            tagTypeText = presetTags.getValue().toString();
            preset = currentUser.getCreatedTag(tagTypeText);
            multiple = preset.canHaveMultiple();
        }
        else{
            if (preset != null){
                showError("This tag type is already in the database. Why not use your preset tag?");
                return;
            }
        }


        // Error checking.
        if(tagTypeText.isEmpty() || tagValueText.isEmpty()){
            showError("You're missing a tag or a value. Please input them. Spaces are allowed in tags.");
            return;
        }
        if (tagTypeText.indexOf("=") != -1 || tagValueText.indexOf("=") != -1){
            showError("= is not a valid character in a tag.");
            return;
        }
        Tag newTag = new Tag(tagTypeText, tagValueText, multiple);

        // Attempt to add the tag.
        Photo photo = (Photo) tableView.getSelectionModel().getSelectedItem();
        DBStatus attempt = db.addTag(photo, newTag);
        if (attempt == DBStatus.SUCCESS){
            tableView.getItems().setAll(photos);
            //System.out.println(photo.hasTag(new Tag(tagTypeText, tagValueText)));
            //Clean up and message
            tagType.setText("");
            tagValue.setText("");
            showSuccess("Successfully Added a new tag!");

            // Ask the user if they want to save the new tag.
            if (presetTags.getValue().toString().equals(Tag.custom)) {
                if (showWarning("Do you want to save your new custom tag?")){
                    currentUser.getCreatedTags().add(newTag);
                    presetTags.getItems().add(newTag.name);
                }
                if (showWarning("Does this tag allow for multiple values?")){
                    newTag.setMultiple(true);
                }
                else{
                    newTag.setMultiple(false);
                }
            }
        }
        else{
            handleDBStatus(attempt);
        }
    }

    public void handleLogout(ActionEvent event) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        currentUser = null;
        currentAlbum = null;
        changeScene(stage, loginScene);
    }

    @FXML
    protected void handleDeleteTags(ActionEvent actionEvent, Tag tag){
        //TODO: validate that this is in synced with DB
        tags.remove(tag);
        tagTableView.getItems().setAll(tags);
    }

    /**
     * Creates a Callback with buttons related to options for each Tag added.
     * @return CallBack of Table Column.
     */
    protected Callback<TableColumn<Tag, Tag>, TableCell<Tag, Tag>> handleOptionCellFactory(){
        Callback<TableColumn<Tag, Tag>, TableCell<Tag, Tag>> cellFactory =
                new Callback<TableColumn<Tag, Tag>, TableCell<Tag, Tag>>() {
                    @Override
                    public TableCell call( final TableColumn<Tag, Tag> param ) {
                        final TableCell<Tag, Tag> cell = new TableCell<Tag, Tag>() {
                            HBox hBox;
                            final Button deleteBtn = new Button("delete");
                            @Override
                            public void updateItem(Tag tag, boolean empty ) {
                                super.updateItem(tag, empty);
                                if( empty ) {
                                    setGraphic( null );
                                } else {
                                    deleteBtn.setOnAction(event -> handleDeleteTags(event, tag));
                                    hBox = new HBox();
                                    hBox.getChildren().addAll(deleteBtn);
                                    setGraphic(hBox);
                                }
                                setText( null );
                            }
                        };
                        return cell;
                    }
                };
        return cellFactory;
    }

}
