package sukretbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Created by sukret on 8/14/15.
 */
public class FileDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<File> getByUserName(String userName) {
         return jdbcTemplate.query(
                "SELECT * FROM files WHERE user_name = ?", new Object[] { userName },
                (rs, rowNum) -> new File(rs.getString("user_name"), rs.getString("file_name"), rs.getLong("size")));
    }

    public boolean add(File file) {
        int numAffected = jdbcTemplate.update("INSERT INTO files VALUES(?,?,?)" ,
                            new Object[]{file.getFileName(), file.getUserName(), Long.toString(file.getSize())});
        if(numAffected == 1)
            return true;
        else
            return false;
    }

    public boolean remove(File file) {
        int numAffected = jdbcTemplate.update("DELETE FROM files WHERE user_name=? and file_name=?" ,
                                              new Object[]{file.getUserName(), file.getFileName()});
        if(numAffected == 1)
            return true;
        else
            return false;
    }
}
