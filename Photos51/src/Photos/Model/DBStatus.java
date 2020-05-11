package Photos.Model;


/**
 * Used by the database and controllers to handle certain errors.
 * @author April Dizon
 * @author Krysti Leong
 */
public enum DBStatus {
    SUCCESS,
    FAILURE,
    ADMIN,
    NO_USER,
    NO_ALBUM,
    DUPLICATE_ALBUM,
    DUPLICATE_TAG,
    NOT_MULTIPLE_TAG,
    DB_NOT_FOUND,
    NEW_DB
}
