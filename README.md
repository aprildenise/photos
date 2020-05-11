## Photos
Krysti Leong, April Denise Dizon, Group 51
* Made with IntelliJ, Java 1.8
* Optional implementations:
    - Passwords are implemented. The admin's password is "admin," like stated in the prompt.
    - When adding a new album, the creation date of the album is first displayed. It will become the date range of photos when there are photos added.
    - Only 6 stock photos in the folder of 10 are used.


## Changelog

03/31 - April
* Accidentally deleted Storyboard, reverted to the commit to put it back.
* Set up the project in IntelliJ
* Started coding serialization in Database.
* Started coding Album and Photo object.
* Started coding login through LoginController and login.fxml

04/05 - April
* Added more functionality to the LoginController to check if a User is in the Database.
* Started coding the Admin Subsystem in AdminController and admin.fxml

04/06 - April
* Finished a basic version of the login screen and the admin subsystem.
* Completed serialization.
* Need to decide:
- [X] Where to connect all the UIs and windows together?
- [X] How to generalize the function of all the buttons into an abstract class or interface? (ie. All add buttons take information from textfields and create a new Object, all delete buttons delete the currently selected item, all UIs use the showAlert() function, etc.)

04/07 - April
* Fixed some methods in User
* Started generalizing the controllers by creating a parent class, Controller.

04/09 - April
* added a changeScene() method to load different fxml files.
* began coding the album view in album.fxml and AlbumController based on the storyboard.

04/10 - April
* Made Controller into an abstract class and expanded it. 
* Implemented the passing of variables between scenes with initData()
* Expanded Database to add, delete, and check albums.
* Added TableView to ablum.fxml
* Still adding more functionality to album.fxml and AlbumController.

04/11 - Krysti
* Pulled latest codebase and added comments on areas where code is not working when cloning for the first time.

04/12 - Krysti
* Fixed date formatting

04/13 - Krysti
* Added options for all albums
* Implemented delete album action
* Implemented edit album action

04/12 - April
* Added FileHandler.java to get paths for loading and saving.
* [X] Working on fixing serialization so that albums are also serialized. Perhaps it's because Lists are not necessarily serializable?

04/12 - April
* Fixed serialization above.

04/13 - April
* Actually pushed the serialization fix.
* Changed admin UI to a tableView so that it can be consistent with the other pages.
* Altered utility functions in User again because I honestly don't know which one is better to have.
* Minor additions to album view.
* Finished up .fxml of login and admin view

04/13 - Krysti
* Implemented the back button in PhotoScene

04/14- Krysti
* Updated handle add photo function
* Implemented handle delete function
* Updated photo FXML with photo preview
* Displayed photo preview on the right hand side of Photo Scene
* Added date column in photo scene
* Handled Auto Select in photo scene

04/14 - April
* Added Tag class
* Working on search function through SearchController and search.fxml
* Added tag functions to photos and Database

4/15 - Krysti
* Handled Adding Caption
* Handle adding tags
* Updated photo preview FXML to show Tag List View
* Fixed date at the album level to show photo's last modified
* Fixed glitch with deleting photos and showing the right date at album level
* Need to make sure all changes are propagated with the Database