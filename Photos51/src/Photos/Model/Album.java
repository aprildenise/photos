package Photos.Model;

import javafx.util.Pair;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Defines albums for the application. Handles collecting the dates of photos, adding photos
 * and deleting photos.
 * This is serializable.
 * @author Kristi Leong
 * @author April Dizon
 */
public class Album implements Serializable , Cloneable{

    private static final long serialVersionUID = 2L;
    public String name;
    private List<Photo> photos;
    private Calendar creationDate; // May not be needed
    public String dateModified;

    /**
     * Constructor for the Album.
     * @param name Name of the album.
     */
    public Album(String name){
        this.name = name;
        photos = new ArrayList<Photo>();
        creationDate = new GregorianCalendar();
        dateModified = "";
        updateDate();
    }

    /**
     * Get the list of photos
     * @return
     */
    public List<Photo> getPhotos(){
        return photos;
    }

    /**
     * Return the number of photos.
     * @return
     */
    public int getNumPhotos(){
        if (photos == null){
            return 0;
        }
        return photos.size();
    }

    /**
     * Return the name.
     * @return
     */
    public String getName(){
        return name;
    }

    /**
     * Return the date modified.
     * @return
     */
    public String getDateModified(){
        return dateModified;
    }

    /**
     * Set the name of this album
     * @param name Name to set.
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Utility method for converting Calendar to MM/dd/yyyy
     * @param calendar
     * @return
     */
    public static String format(Calendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }

    /**
     * Update the dateModified field based on the current photos
     * in the album.
     */
    public void updateDate(){
        if (photos.isEmpty()) {
            dateModified = format(creationDate);
        }
        // Go through the photos to find the earliest date and the latest date.
        else{
            Calendar earliest = photos.get(0).dateModified;
            Calendar latest = photos.get(0).dateModified;
            for (Photo photo : photos){
                if (photo.dateModified.compareTo(earliest) < 0){
                    earliest = photo.dateModified;
                }
                if (photo.dateModified.compareTo(latest) > 0){
                    latest = photo.dateModified;
                }
            }
            // If earliest and latest ended up being the same, then
            // convert only one of them to a string.
            if (earliest.compareTo(latest) == 0){
                dateModified = format(earliest);
            }
            else{
                // Use the concate of the two strings.
                dateModified = format(earliest) + " - " + format(latest);
            }
        }

    }

    /**
     * Add a photo to this album.
     * @param photo
     */
    public void addPhoto(Photo photo){
        photos.add(photo);
        updateDate();
    }

    /**
     * Set the photos of this album.
     * @param photos
     */
    public void setPhotos(List<Photo> photos){
        this.photos = photos;
        updateDate();
    }

    /**
     * Compare the name of this album.
     * @param compare
     * @return
     */
    public int compareName(Album compare){
        return this.name.compareTo(compare.name);
    }


    /**
     * Convert the data of the album into a String.
     * !!Used for serialization!!
     * @return String of this object.
     */
    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", numPhotos=" + getNumPhotos() +
                ", photos=" + photos +
                ", creationDate=" + format(creationDate) +
                ", dateModified='" + dateModified + '\'' +
                '}';
    }

    /**
     * Creates a clone of the current album
     * @return  a clone of Album
     * @throws CloneNotSupportedException   clone exception.
     */
    public Object clone() throws CloneNotSupportedException {
        Album clone = (Album)super.clone();
        return clone;
    }
}