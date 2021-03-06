package sukretbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import sukretbox.exceptions.UserDoesNotExistException;
import sukretbox.exceptions.UserExistsException;

import java.util.List;

/**
 * Created by sukret on 8/11/15.
 */
public class UserDao {

    private static final long DEFAULT_STORAGE_LIMIT = 100000000; // 100 MB per new user by default

    private static final String usersTable = "users2"; // name of the table storing user information

    public static String getUsersTable() {
        return usersTable;
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    public User getByName(String name) throws UserDoesNotExistException {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM " + usersTable + " WHERE name = ?", new Object[] { name },
                (rs, rowNum) -> new User(rs.getString("name"),
                                         rs.getInt("current_storage"),
                                         rs.getInt("storage_limit"),
                                         rs.getString("password"),
                                         rs.getString("dbx_token"))
        );
        if(users.size() == 0)
            throw new UserDoesNotExistException();
        return users.get(0);
    }

    public boolean add(String name, String password) throws UserExistsException {
        if(jdbcTemplate.queryForList("SELECT * FROM " + usersTable + " WHERE name = ?", new Object[]{name}).size() != 0)
            throw new UserExistsException();
        int numAffected = jdbcTemplate.update("INSERT INTO " + usersTable + " VALUES(?,?,?,?,?)" ,
                                              new Object[]{name, "0", Long.toString(DEFAULT_STORAGE_LIMIT), password,
                                                           "NONE"});
        if(numAffected == 1)
            return true;
        else
            return false;
    }

    public boolean update(User user) {
        int numAffected = jdbcTemplate.update("UPDATE " + usersTable +
                                              " SET storage_limit=?, current_storage=?, dbx_token=? WHERE name=?",
                            new Object[]{Long.toString(user.getStorageLimit()),
                                         Long.toString(user.getCurrentStorage()),
                                         user.getDbxToken(),
                                         user.getName() });
        if(numAffected == 1)
            return true;
        else
            return false;
    }
}
