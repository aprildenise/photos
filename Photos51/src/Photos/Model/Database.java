package Photos.Model;

import java.io.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Database that stores information about the users in the application.
 * Handles all requests from the application, including searches, adds, and deletes.
 * Serializes and deserializes the data as needed.
 * @author Krysti Leong
 * @author April Dizon
 */
public class Database implements Serializable{

    private static final long serialVersionUID = 2L;
    private static File storeDir;
    private static final String storeFile = "database.dat";
    private static final String adminUsername = "admin";
    private static final String adminPassword = "admin";
    private static final User admin = new User(adminUsername, adminPassword);

    private final User stock = new User("stock", "");
    private final String stockImage = "stock";
    private final String stockFormat = ".jpg";
    private final int numStock = 6;

    private ArrayList<User> users;


    /**
     * Constructor. Initializes the data structures and attempts to load a file that
     * holds the data.
     */
    public Database() throws FileNotFoundException {
        users = new ArrayList<User>();
        File fileDir = FileHandler.findPath(storeFile);
        if (fileDir == null){
            // Should never happen, but this is here in case.
        }
        else{
            storeDir = fileDir.getAbsoluteFile();
            loadDatabase();
        }
    }

    /**
     * Used when loading an entirely new database from the system. Adds the
     * Admin to the database and the stock user, with their photos.
     */
    private void initStartingData() throws FileNotFoundException {
        if (!exists(admin)){
            users.add(admin);
        }
        if (!exists(stock)){
            //deleteUser(stock);
            users.add(stock);
        }
        Album stockAlbum = new Album(stockImage);
        // Load the stock photos for the stock user.
        for (int i = 1; i <= numStock; i++){
            String fileName = stockImage + i + stockFormat;
            File f = FileHandler.findPath(fileName);
            Photo stockPhoto = new Photo(f.getAbsolutePath());
            stockAlbum.addPhoto(stockPhoto);
        }
        addAlbum(stock, stockAlbum);

    }

    /**
     * Get the users from this database.
     * @return List of Users.
     */
    public List<User> getUsers(){
        return users;
    }

    /**
     * Set the users for this database.
     * @param users List of users to set.
     */
    public void setUsers(ArrayList<User> users){
        this.users = users;
    }

    /**
     * Add a User to the database as long as it does not already exist in the database.
     * @param user User to add.
     * @return True if it was added, false if it already existed and was not added.
     */
    public boolean addUser(User user){
        // Make sure this user doesn't exist.
        if (exists(user)){
            return false;
        }
        else{
            // Add the user, along with a new list of albums.
            users.add(user);
            return true;
        }
    }

    /**
     * Delete a User from the database as long as it's not the admin User.
     * @param user User to delete.
     * @return True if it was deleted, false if it was the admin user and was not deleted.
     */
    public DBStatus deleteUser(User user){
        // Make sure the user was not the admin.
        if (isAdmin(user)){
            return DBStatus.ADMIN;
        }
        // Delete the user, as well as any albums they had.
        else{
            User toDelete = getUser(user.username);
            if (toDelete == null){
                return DBStatus.NO_USER;
            }
            users.remove(toDelete);
            return DBStatus.SUCCESS;
        }
    }

    /**
     * Authorize the user by checking if this user's username and password is in the
     * database.
     * @param username Username of the user.
     * @param password Password of the user.
     * @return True if their username and password is in the database, false elsewise.
     */
    public boolean authorize(String username, String password){
        for (User u : users){
            if (u.compareUsername(username) == 0 && u.comparePassword(password) == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * Get the reference of the user from this database.
     * @param username Username of this user.
     * @return The user to get.
     */
    public User getUser(String username){
        for (User user: users){
            if (user.compareUsername(username) == 0){
                return user;
            }
        }
        return null;
    }

    /**
     * Check if a user is already in the database by comparing usernames.
     * @param user User in question.
     * @return True if they are in the database, false elsewise.
     */
    public boolean exists(User user){
        for (User u : users){
            if (user.compareUsername(u) == 0){
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the given user is the admin user.
     * @param user User in question.
     * @return True if it's the admin, false if it's not.
     */
    public static boolean isAdmin(User user){
        if (user.sameUser(admin)){
            return true;
        }
        else{
            return false;
        }
    }

//    /**
//     * Check if the given user is the admin user.
//     * @param username Username of the User in question.
//     * @return True if it's the admin, false if it's not.
//     */
//    public static boolean isAdmin(String username){
//        if (username.compareTo(adminUsername) == 0){
//            return true;
//        }
//        else{
//            return false;
//        }
//    }

    /**
     * Add an album to a user's album list.
     * @param user User to add the album.
     * @param album Album to add.
     * @return DBStatus of the add.
     */
    public DBStatus addAlbum(User user, Album album){
        // Check if the user exists.
        User thisUser = getUser(user.username);
        if(thisUser == null){
            return DBStatus.NO_USER;
        }
        // Make sure there's not duplicates of this album
        Album dupAlbum = getAlbum(thisUser, album);
        if (dupAlbum != null){
            return DBStatus.DUPLICATE_ALBUM;
        }

        // Everything is good!
        thisUser.getAlbums().add(album);
        return DBStatus.SUCCESS;
    }

    /**
     * Delete a user's album from the database.
     * @param user User that possesses the album.
     * @param album The album to delete.
     * @return DBStatus of the delete.
     */
    public DBStatus deleteAlbum(User user, Album album){
        // Check if this user exists.
        User thisUser = getUser(user.username);
        if (thisUser == null){
            return DBStatus.NO_USER;
        }
        // Get the user's albums, find the album they want to delete, and delete it!
        List<Album> userAlbums = thisUser.getAlbums();
        Album toDelete = getAlbum(thisUser, album);
        if (toDelete == null){
            return DBStatus.NO_ALBUM;
        }
        else{
            thisUser.getAlbums().remove(toDelete);
        }
        return DBStatus.SUCCESS;

    }

    /**
     * Add a photo to a user's album.
     * @param user User to have the photo.
     * @param album Album to have the photo.
     * @param photo Photo to add.
     * @return DBStatus of the add.
     */
    public DBStatus addPhoto(User user, Album album, Photo photo){

        // Get the db's reference to the user.
        User thisUser = getUser(user.username);
        if (thisUser == null){
            return DBStatus.NO_USER;
        }

        // Get the album in question.
        Album addTo = getAlbum(thisUser, album);
        if (addTo == null){
            return DBStatus.NO_ALBUM;
        }
        else{
            // Add the photo!
            addTo.addPhoto(photo);
        }
        return DBStatus.SUCCESS;
    }

    /**
     * Get the reference to an album from this database.
     * @param user User that has this album.
     * @param album Album to get.
     * @return Album found, if any, or null.
     */
    public Album getAlbum(User user, Album album){
        User thisUser = getUser(user.username);
        if (thisUser == null){
            return null;
        }
        List<Album> albums = thisUser.getAlbums();
        if (albums.size() == 0){
            return null;
        }
        else{
            for (Album a : albums){
                if (a.compareName(album) == 0){
                    return a;
                }
            }
        }
        return null;
    }

//    /**
//     * Check if a user's album exists.
//     * @param user User with the album.
//     * @param album Album in question.
//     * @return
//     */
//    public DBStatus exists(User user, Album album){
//        // Check if this user exists.
//        if (!exists(user)){
//            return DBStatus.NO_USER;
//        }
//        // Attempt to find the album in question.
//        List<Album> userAlbums = user.getAlbums();
//        for (Album a : userAlbums){
//            if (a.compareName(album) == 0){
//                return DBStatus.DUPLICATE_ALBUM;
//            }
//        }
//
//        // This album does not exist!
//        return DBStatus.NO_ALBUM;
//    }




    /**
     * Search a user's photos by their tags.
     * @param user User that has the photos to be searched.
     * @param tag Tag to find.
     * @return List of photos found that has the tag.
     */
    public List<Photo> searchByTag(User user, Tag tag){

        // Get all the albums of the current user to look at all their photos.
        String tagName = tag.name;
        String value = tag.value;
        List<Photo> found = new ArrayList<>();
        User thisUser = getUser(user.username);
        List<Album> albums = thisUser.getAlbums();

        // Error checking.
        if (albums == null){
            return found;
        }
        else if (albums.isEmpty()){
            return found;
        }
        else{
            for (Album a : albums){
                List<Photo> photos = a.getPhotos();
                // More error checking!
                if (photos == null){
                    continue;
                }
                else if (photos.isEmpty()){
                    continue;
                }
                else{
                    // Loop through the photos.
                    for (Photo p : photos){
                        if (p.hasTag(tag)){
                            found.add(p);
                        }
                    }
                }
            }
        }
        return found;
    }

    /**
     * Find photos that have the given 2 tags. Done by taking the intersection of photos that have
     * the first tag, and photos that have the second tag.
     * @param user User that has the photos to be searched.
     * @param tag1 First tag.
     * @param tag2 Second tag.
     * @return List of photos that have both tags.
     */
    public List<Photo> searchByTagConjunctive(User user, Tag tag1, Tag tag2){

        // Get the photos that have the given tags.
        String tagName1 = tag1.name;
        String tagName2 = tag2.name;
        String value1 = tag1.value;
        String value2 = tag2.value;
        List<Photo> found1 = searchByTag(user, tag1);
        List<Photo> found2 = searchByTag(user, tag2);
        List<Photo> found = new ArrayList<Photo>();

        // If one whole list is empty, just return.
        if (found1.size() == 0 || found2.size() == 0){
            return found;
        }

        // Find the intersection between the photos found.
        List<Photo> iterator;
        List<Photo> compareTo;
        if (found1.size() > found2.size()){
            iterator = found1;
            compareTo = found2;
        }
        else{
            iterator = found2;
            compareTo = found1;
        }
        for (Photo p : iterator){
            if (compareTo.contains(p)){
                found.add(p);
            }
        }
        return found;
    }


    /**
     * Find photos that have the either the 2 tags. Done by taking the union of photos that have
     * the first tag, and photos that have the second tag.
     * @param user User that has the photos to be searched.
     * @param tag1 First tag.
     * @param tag2 Second tag.
     * @return List of photos that have either tags.
     */
    public List<Photo> searchByTagDisjunctive(User user, Tag tag1, Tag tag2){

        // Get the photos that have the given tag.
        String tagName1 = tag1.name;
        String tagName2 = tag2.name;
        String value1 = tag1.value;
        String value2 = tag2.value;
        List<Photo> found1 = searchByTag(user, tag1);
        List<Photo> found2 = searchByTag(user, tag2);
        List<Photo> found = new ArrayList<Photo>();

        // If either list is empty, return the other one.
        if (found1.size() == 0 && found2.size() == 0){
            return found;
        }
        else if (found1.size() == 0){
            return found2;
        }
        else if (found2.size() == 0){
            return found1;
        }
        else{
            // Take the union of the 2 sets, MANUALLY to leave out any duplicates.
            found1.addAll(found2);
            found = found1;
//            List<Photo> iterator;
//            List<Photo> compareTo;
//            if (found1.size() > found2.size()){
//                iterator = found1;
//                compareTo = found2;
//            }
//            else{
//                iterator = found2;
//                compareTo = found1;
//            }
//            for (Photo p : iterator){
//                if (!compareTo.contains(p)){
//                    found.add(p);
//                }
//            }
        }
        //System.out.print(found);
        return found;
    }


    /**
     * Search for photos by date range.
     * @param user User that has the photos.
     * @param start Starting date.
     * @param finish Ending date.
     * @return List of photos found, if any, or null.
     */
    public List<Photo> searchByDate(User user, Calendar start, Calendar finish){
        List<Photo> found = new ArrayList<Photo>();
        User thisUser = getUser(user.username);
        List<Album> albums = thisUser.getAlbums();
        // Error checking.
        if (albums == null){
            return found;
        }
        else if (albums.isEmpty()){
            return found;
        }
        else{
            for (Album a : albums){
                List<Photo> photos = a.getPhotos();
                // More error checking!
                if (photos == null){
                    continue;
                }
                else if (photos.isEmpty()){
                    continue;
                }
                else{
                    // Loop through the photos.
                    for (Photo p : photos){
                        if (p.withinDate(start, finish)){
                            // This photo is within the date!
                            found.add(p);
                        }
                    }
                }
            }
        }

        return found;
    }

    /**
     * Add the tag to a photo. We're assuming this already exists in the database,
     * since there is no way to get this photo witout a proper reference to the album.
     * @param photo Photo that will get the tag.
     * @param toAdd Tag to add.
     * @return
     */
    public DBStatus addTag(Photo photo, Tag toAdd){

        // Attempt to add this tag.
        if (photo.hasTag(toAdd)){
            return DBStatus.DUPLICATE_TAG;
        }
        boolean attempt = photo.addTag(toAdd);
        if (!attempt){
            return DBStatus.NOT_MULTIPLE_TAG;
        }

        return DBStatus.SUCCESS;
    }

    /**
     * Attempt to "fake" load a user. This checks to make sure that their photos
     * are on the machine. If they're not, they will be deleted.
     * @param user
     */
    public DBStatus attemptPhotoLoad(User user){
        User thisUser = getUser(user.username);
        List<Album> albums = thisUser.getAlbums();
        int lostPhotos = 0;
        if (albums == null){
            return DBStatus.SUCCESS;
        }
        else if (albums.isEmpty()){
            return DBStatus.SUCCESS;
        }
        else{
            // Loop through the photos and "load" them.
            for (Album a: albums){
                List<Photo> photos = a.getPhotos();
                if (photos == null){
                    continue;
                }
                else if (photos.isEmpty()){
                    continue;
                }
                else{
                    int i = 0;
                    while (i < photos.size()){
                        Photo p = photos.get(i);
                        try {
                            p.getImageView();
                        } catch (FileNotFoundException e) {
                            // Remove this photo.
                            photos.remove(p);
                            a.setPhotos(photos);
                            lostPhotos++;
                            continue;
                        }
                        catch (ConcurrentModificationException e){
                            photos.remove(p);
                            a.setPhotos(photos);
                            lostPhotos++;
                            continue;
                        }
                        i++;
                    }
                }
            }
        }
        if (lostPhotos > 0){
            return DBStatus.FAILURE;
        }
        return DBStatus.SUCCESS;

    }

//    /**
//     *
//     * @param photo
//     * @param toDelete
//     * @return
//     */
//    public DBStatus removeTag(Photo photo, Tag toDelete){
//        // No checks needed. Just delete.
//        photo.getTagsList().remove(toDelete);
//
//        return DBStatus.SUCCESS;
//    }

    /**
     * Save this database to a file.
     * @return DBStatus of the function.
     */
    public DBStatus saveCurrentDatabase(){
        //System.out.println("Saving database...");
        try {
            writeFile(this);
        } catch (IOException e) {
            e.printStackTrace();
            return DBStatus.FAILURE;
        }
        return DBStatus.SUCCESS;
    }

    /**
     * Load this database from a file.
     * @return DBStatus of the function.
     */
    public DBStatus loadDatabase() throws FileNotFoundException {
        Database db = null;
        try {
            // Attempt the read the database file.
            db = readFile();

            // If there are any exceptions, simply save create a new database file
            // and populate it.
        } catch (IOException e) {
            //System.out.println("Caught exception:");
            //e.printStackTrace();
            initStartingData();
            saveCurrentDatabase();
            return DBStatus.NEW_DB;
        } catch (ClassNotFoundException e) {
            //System.out.println("Caught exception:");
            //e.printStackTrace();
            initStartingData();
            saveCurrentDatabase();
            return DBStatus.NEW_DB;
        }
        // If this was successfully read, load the users from the database file.
        // Make sure that the starting data is there as well.
        setUsers(db.users);
        initStartingData();
        return DBStatus.SUCCESS;
    }

    /**
     * Serializes the contents of the Database into the file in storeFile
     * @param db Database to save.
     * @throws IOException
     */
    private static void writeFile(Database db) throws IOException {
        //System.out.println(db);
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(storeDir));
            oos.writeObject(db);
    }

    /**
     * Read and reconstruct the file in storeFile to get its Database contents.
     * @return Database found.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Database readFile() throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir));
        Database db = (Database)ois.readObject();
        //System.out.println(db);
        return db;
    }



    /**
     * Convert the data of the database into a String.
     * !!Used for serialization!!
     * @return String of this object.
     */
    @Override
    public String toString() {
        return "Database{" +
                "users=" + users +
                '}';
    }
}
