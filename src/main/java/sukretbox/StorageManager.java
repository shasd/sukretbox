package sukretbox;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by sukret on 9/7/15.
 */
public class StorageManager {

    @Autowired
    DataStore dataStore;

    @Autowired
    FileDao fileDao;

    public boolean storeFile(String userName, String fileName, byte[] data) {
        // check same file name does not exist for same user
        List<File> files = fileDao.getByUserName(userName);
        if(files.stream().anyMatch(f -> f.getFileName().equals(fileName)))
            return false;
        long hash = JenkinsHash.hash64(data);
        boolean alreadyStored = fileDao.fileStored(hash); // check if same file exists for another user
        File file = new File(userName, fileName, data.length, hash, alreadyStored ? (byte) 0 : (byte) 1);
        if(!fileDao.add(file))
            return false;
        if(alreadyStored)
            return true;
        if(dataStore.storeData(userName, fileName, data))
            return true;
        // if file was not stored successfully, need to remove metadata from db
        fileDao.remove(file);
        return false;
    }

    public byte[] getData(String userName, String fileName) {
        File file = fileDao.getStoredFile(userName, fileName);
        if(file == null)
            return null;
        return dataStore.getData(file.getUserName(), file.getFileName());
    }
}
