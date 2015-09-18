package sukretbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by sukret on 8/14/15.
 */
public class FileDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static File mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new File(rs.getString("user_name"),
                        rs.getString("file_name"),
                        rs.getLong("size"),
                        rs.getLong("hash"),
                        rs.getByte("stored"));
    }

    public List<File> getByUserName(String userName) {
         return jdbcTemplate.query(
                "SELECT * FROM files WHERE user_name = ?", new Object[] { userName }, FileDao::mapRow);
    }

    public File get(String userName, String fileName) {
        List<File> files = jdbcTemplate.query("SELECT * FROM files WHERE user_name=? and file_name=?",
                                              new Object[] { userName, fileName },
                                              FileDao::mapRow);
        if(files.size() == 0)
            return null;
        return files.get(0);
    }

    public boolean add(File file) {
        int numAffected = jdbcTemplate.update("INSERT INTO files VALUES(?,?,?,?,?)",
                new Object[]{file.getFileName(), file.getUserName(), Long.toString(file.getSize()),
                        Long.toString(file.getHash()), Byte.toString(file.getStored())});
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

    public boolean update(File file) {
        int numAffected = jdbcTemplate.update("UPDATE files SET size=?, hash=?, stored=? WHERE file_name=? and user_name=?",
                                              new Object[]{Long.toString(file.getSize()),
                                                           Long.toString(file.getHash()),
                                                           Byte.toString(file.getStored()),
                                                           file.getFileName(),
                                                           file.getUserName()});
        if(numAffected == 1)
            return true;
        else
            return false;
    }

    // return the same userName and fileName if file is stored under these names, otherwise the userName and fileName
    // which have the file stored
    public File getStoredFile(String userName, String fileName) {
        List<File> file = jdbcTemplate.query("SELECT * FROM files WHERE user_name=? and file_name=?",
                                             new Object[] { userName, fileName },
                                             FileDao::mapRow);
        if(file.size() != 1)
            return null;
        if(file.get(0).getStored() == (byte)1)
            return file.get(0);
        List<File> files = jdbcTemplate.query("SELECT * FROM files WHERE hash=? and stored=1",
                new Object[]{Long.toString(file.get(0).getHash())},
                FileDao::mapRow);
        if(files.size() == 0)
            return null;
        return files.get(0);
    }
    // return all users who have this file
    public List<File> getReferences(long hash) {
        return jdbcTemplate.query("SELECT * FROM files WHERE hash=?",
                                  new Object[] { Long.toString(hash) },
                                  FileDao::mapRow);
    }
    // return if this hash is already stored
    public boolean fileStored(long hash) {
        List<File> files = jdbcTemplate.query("SELECT * FROM files WHERE hash=? and stored=1",
                                              new Object[] { Long.toString(hash) },
                                              FileDao::mapRow);
        if(files.size() > 0)
            return true;
        return false;
    }
}
