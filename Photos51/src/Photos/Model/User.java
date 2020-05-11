package Photos.Model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the Users of this application, which have a username, password, and albums.
 * This object is serializable.
 * @author Krysti Leong
 * @author April Dizon
 */
public class User implements Serializable {

    private static final long serialVersionUID = 2L;
    public String username;
    private String password;
    private List<Album> albums;
    private List<Tag> createdTags;

    /**
     * Constructor for this user.
     * @param username Username of this user.
     * @param password Password of this user.
     */
    public User(String username, String password){
        this.username = username;
        this.password = password;
        albums = new ArrayList<Album>();

        // Set the tags for users to have.
        createdTags = new ArrayList<Tag>();
        Tag tag1 = new Tag("location", "", false);
        Tag tag2 = new Tag("people", "", true);
        Tag tag3 = new Tag("mood", "", true);
        Tag tag4 = new Tag(Tag.custom, "", false);
        createdTags.add(tag4);
        createdTags.add(tag1);
        createdTags.add(tag2);
        createdTags.add(tag3);

    }

    /**
     * Get this user's albums, if any.
     * @return ArrayList of Albums, which can be empty, or have Albums
     * in it.
     */
    public List<Album> getAlbums(){
        return albums;
    }

    public Album getAlbumByName(String albumName){
        for(Album album : albums) {
            String name = album.getName();
            if(albumName.equals(name)) {
                return album;
            }
        }
        return null;
    }
    /**
     * Set this user's albums and update the numAlbums.
     * @param albums Albums to set.
     */
    public void setAlbums(ArrayList<Album> albums){
        this.albums = albums;
    }

    /**
     * Get this user's username.
     * Used by tableviews.
     * @return User's username.
     */
    public String getUsername(){
        return username;
    }

    /**
     * Get this user's password.
     * Used by tableviews
     * @return Password.
     */
    public String getPassword(){
        return password;
    }

    /**
     * Get the number of albums.
     * Used by tableviews.
     * @return Number of albums.
     */
    public int getNumAlbums(){
        if (albums == null){
            return 0;
        }
        return albums.size();
    }

    public List<Tag> getCreatedTags(){
        return createdTags;
    }

    public Tag getCreatedTag(String tagName){
        for (Tag t: createdTags){
            if (t.sameName(new Tag(tagName, ""))){
                return t;
            }
        }
        return null;
    }

    public void setCreatedTags(List<Tag> tags){
        this.createdTags = tags;
    }

    /**
     * Compare this user's username to another's.
     * @param toCompare User to compare to.
     * @return 0 if they are equal, < 1 if this user's username is less than toCompare's
     * > 1 if this user's username is greater than toCompare's.
     */
    public int compareUsername(User toCompare){
        return this.username.compareTo(toCompare.username);
    }

    /**
     * Compare this user's username to another's.
     * @param username username of the User to compare.
     * @return 0 if they are equal, < 1 if this user's username is less than toCompare's
     * > 1 if this user's username is greater than toCompare's.
     */
    public int compareUsername(String username){
        return this.username.compareTo(username);
    }

    /**
     * Compare this user's password to another's.
     * @param toCompare User to compare to.
     * @return 0 if they are equal, < 1 if this user's password is less than toCompare's
     * > 1 if this user's password is greater than toCompare's.
     */
    public int comparePassword(User toCompare){
        if (toCompare.password.equals("") && this.password.equals("")){
            return 0;
        }
        return this.password.compareTo(toCompare.password);
    }

    /**
     * Compare this user's password to another's.
     * @param password password to compare.
     * @return 0 if they are equal, < 1 if this user's password is less than toCompare's
     * > 1 if this user's password is greater than toCompare's.
     */
    public int comparePassword(String password){
        if (this.password.equals("") && password.equals("")){
            return 0;
        }
        return this.password.compareTo(password);
    }

    /**
     * Check if this user's username and password matches another's.
     * @param toCompare User to compare to.
     * @return True if this user and toCompare is the same, false elsewise.
     */
    public boolean sameUser(User toCompare){
        if (compareUsername(toCompare) == 0 && comparePassword(toCompare) == 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Convert the data of the user into a String.
     * !!Used for serialization!!
     * @return String of this object.
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", albums=" + albums +
                '}';
    }
}
