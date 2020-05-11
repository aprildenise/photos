package Photos.Model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Defines a Photo for the library, which has a caption, date, directory, and
 * a hashmap of tags.
 * This class is serializable.
 * @author  Krysti Leong
 * @author April Dizon
 */
public class Photo implements Serializable, Cloneable {

    private static final long serialVersionUID = 2L;
    public String caption;
    public GregorianCalendar dateModified;
    private transient ImageView imageView;
    private transient Image image;
    private String directory;
    private File file;
    //private HashMap<String, ArrayList<String>> tagHashMap;
    private List<Tag> tags;

    /**
     * Constructor for this photo.
     */
    public Photo(String directory) throws FileNotFoundException {
        caption = "";
        //dateModified = new GregorianCalendar();
        this.directory = directory;
        this.file = new File(directory);

        // Set the date in a somewhat convoluted way.
        Date date = new Date(file.lastModified());
        GregorianCalendar calender = new GregorianCalendar();
        calender.setTime(date);
        dateModified = calender;

        //tagHashMap = new HashMap<String, ArrayList<String>>();
        tags = new ArrayList<Tag>();
        getImageView();
    }

    /**
     * Get the path for this photo.
     * @return Path
     */
    public String getPath(){
        return this.file.toURI().toString();
    }

    /**
     * Set the caption for this photo.
     * @param caption Caption to set.
     */
    public void setCaption(String caption){
        this.caption = caption;
    }

    /**
     * Get the Caption
     * @return Caption
     */
    public String getCaption(){
        return this.caption;
    }

    /**
     * Format the date correctly in the mm/dd/yyyy format.
     * @return Formated date.
     */
    public String getDateModified(){
        return Album.format(dateModified);
    }

    /**
     * Get the imageView of this photo.
     * @return ImageView found
     * @throws FileNotFoundException
     */
    public ImageView getImageView() throws FileNotFoundException, ConcurrentModificationException {

        this.image = new Image(new FileInputStream(directory));

        this.imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Set the tags for this photo.
     * @param tags List of tags.
     */
    public void setTags(List<Tag> tags){
        this.tags = tags;
    }

    /**
     * Get the tags for this photo.
     * @return List of tags.
     */
    public List<Tag> getTagsList(){
        return tags;
    }
    /**
     * Add a tag to this photo, as long as there isn't a duplicate tag.
     * Note that this does not check if a tag is allowed to have more than
     * one value.
     * @param tag tag
     * @return True if it was successfully added, false if this was a duplicate and
     * cannot be added.
     */
    protected boolean addTag(Tag tag){

        // Find any dups
        if (hasTag(tag)){
            return false;
        }

        // Find any dups, that have DIFFERENT VALUES.
        Tag dup = getTag(tag);
        if (dup != null){
            // Can we have multiple tags of the same name?
            if (dup.canHaveMultiple()){
                tag.setMultiple(true);
                tags.add(tag);
                return true;
            }
            else{
                return false;
            }
        }

        tags.add(tag);
        return true;
    }

    /**
     * Get a specified tag from this photo.
     * Will only check for tags of the same name.
     * @param tag Tag in question.
     * @return Tag if found, or null.
     */
    protected Tag getTag(Tag tag){
        // Find this tag.
        if (!tags.isEmpty()){
            for (Tag t : tags){
                if (t.sameName(tag)){
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * Check if this photo already has a tag that is EQUAL to the given tag.
     * @param tag Tag in question.
     * @return True if it has this tag, false elsewise.
     */
    protected boolean hasTag(Tag tag){
        // Check if this tag even exists.
        if (!tags.isEmpty()){
            // Loop through the values to see if there's any dups
            for (Tag t : tags){
                // Check if this is a dup
                if (t.equals(tag)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if this Photo has been modified within the given dates.
     * @param start Start date (Calendar)
     * @param end End date (Calendar)
     * @return True if it's within the date, false elsewise.
     */
    public boolean withinDate(Calendar start, Calendar end){
        if (dateModified.before(end) && dateModified.after(start)){
            return true;
        }
        return false;
    }


    /**
     * Convert string to a Calendar object.
     * @param s String to parse.
     * @return Calendar object.
     * @throws ParseException
     */
    public static Calendar stringToCalendar(String s) throws ParseException {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        cal.setTime(format.parse(s));
        return cal;
    }


    /**
     * Convert the data of the Photo into a String.
     * !!Used for serialization!!
     * @return String of this object.
     */
    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", dateModified=" + dateModified +
                '}';
    }

    /**
     * Clone method for cloning a photo
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        //TODO: check if we need to clone tags individually (to decouple references) - tags not copied
        Photo clone = (Photo)super.clone();
        ArrayList<Tag> clonedTags = new ArrayList<>();
        clonedTags.addAll(tags);
        clone.tags = clonedTags;
        clone.file = new File(file.getPath());
        clone.dateModified = (GregorianCalendar) dateModified.clone();
        return clone;
    }
}
