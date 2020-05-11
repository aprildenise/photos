package Photos.Model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Defines a Tags for this application, which is a name-value pair.
 * A photo can have multiple tags.
 * This is serializable.
 * @author Krysti Leong
 * @author April Dizon
 */

public class Tag implements Serializable, Cloneable {

    public String name;
    public String value;
    private boolean allowMultiple;
    public static String custom = "Custom tag";

    /**
     * Constructor for this tag.
     * @param tagName Name of the tag.
     * @param value Value of the tag.
     */
    public Tag(String tagName, String value, boolean allowMultiple){
        this.name = tagName;
        this.value = value;
        this.allowMultiple = allowMultiple;
    }

    public Tag(String tagName, String value){
        this(tagName, value, false);
    }


    public String getName(){
        return this.name;
    }

    public String getValue(){
        return this.value;
    }

    public boolean canHaveMultiple(){
        return allowMultiple;
    }

    public void setMultiple(Boolean allowMultiple){
        this.allowMultiple = allowMultiple;
    }

    public boolean sameName(Tag tag){
        return this.name.equals(tag.name);
    }

    public boolean equals(Tag tag){
        if (this.name.equals(tag.name) && this.value.equals(tag.value)){
            return true;
        }
        else{
            return false;
        }
    }

    public static Tag stringToTag(String s){
        int delimiter = s.indexOf("=");
        if (delimiter == -1) {
            return null;
        }
        String t = s.substring(0, delimiter);
        String v = s.substring(delimiter + 1);
        return new Tag(t,v);
    }

    @Override
    public String toString() {
        return this.name;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        Tag clone = (Tag)super.clone();
        return clone;
    }
}
