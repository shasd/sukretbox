package sukretbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sukret on 8/14/15.
 */
public class FileDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<File> getByUserName(String userName) {
         return jdbcTemplate.query(
                "SELECT * FROM files WHERE user_name = ?", new Object[] { userName },
                (rs, rowNum) -> new File(rs.getString("user_name"),
                                         rs.getString("file_name"),
                                         rs.getLong("size"),
                                         rs.getLong("hash"),
                                         rs.getByte("stored")));
    }

    public boolean add(File file) {
        int numAffected = jdbcTemplate.update("INSERT INTO files VALUES(?,?,?,?,?)" ,
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

    // return the same userName and fileName if file is stored under these names, otherwise the userName and fileName
    // which have the file stored
    public File getStoredFile(String userName, String fileName) {
        List<File> file = jdbcTemplate.query(
                          "SELECT * FROM files WHERE user_name=? and file_name=?", new Object[] { userName, fileName },
                          (rs, rowNum) -> new File(rs.getString("user_name"),
                                                   rs.getString("file_name"),
                                                   rs.getLong("size"),
                                                   rs.getLong("hash"),
                                                   rs.getByte("stored")));
        if(file.size() != 1)
            return null;
        if(file.get(0).getStored() == (byte)1)
            return file.get(0);
        List<File> files = jdbcTemplate.query(
                "SELECT * FROM files WHERE hash=? and stored=1", new Object[]{Long.toString(file.get(0).getHash())},
                (rs, rowNum) -> new File(rs.getString("user_name"),
                        rs.getString("file_name"),
                        rs.getLong("size"),
                        rs.getLong("hash"),
                        rs.getByte("stored")));
        if(files.size() == 0)
            return null;
        return files.get(0);
    }

    public boolean fileStored(long hash) {
        List<File> files = jdbcTemplate.query(
                "SELECT * FROM files WHERE hash=? and stored=1", new Object[] { Long.toString(hash) },
                (rs, rowNum) -> new File(rs.getString("user_name"),
                        rs.getString("file_name"),
                        rs.getLong("size"),
                        rs.getLong("hash"),
                        rs.getByte("stored")));
        if(files.size() > 0)
            return true;
        return false;
    }
}
