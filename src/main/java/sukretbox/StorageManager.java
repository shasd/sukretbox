package sukretbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sukret on 9/7/15.
 * Manages metadata in fileDao and actual storage together
 */
public class StorageManager {

    private static final File.Storage storage = File.Storage.SUKRETBOX;

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
        File.Storage newStorage = alreadyStored ? File.Storage.NONE : storage;
        File file = new File(userName, fileName, data.length, hash, newStorage);
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

    public byte[] getFile(String userName, String fileName) {
        File file = fileDao.getStoredFile(userName, fileName);
        if(file == null)
            return null;
        return dataStore.getData(file.getUserName(), file.getFileName());
    }

    public boolean deleteFile(String userName, String fileName) {
        Logger logger = LoggerFactory.getLogger(StorageManager.class);
        File file = fileDao.get(userName, fileName);
        if(file == null)
            return false;
        // if this reference does not store the file we can delete the reference
        if(file.getStorage() == File.Storage.NONE)
            return fileDao.remove(file);
        // if this reference stores the file we need to check if there are other references for this file
        List<File> references = fileDao.getReferences(file.getHash());
        if(references.size() == 1) { // if this is the only reference
            if(dataStore.deleteData(userName, fileName))
                return fileDao.remove(file);
        }
        // move stored file to another reference
        File anotherReference = references.stream()
                                          .filter(f -> !f.getFileName().equals(fileName) ||
                                                       !f.getUserName().equals(userName))
                                          .collect(Collectors.toList())
                                          .get(0);
        if(!dataStore.copyFile(userName, fileName, anotherReference.getUserName(), anotherReference.getFileName()))
            return false;
        anotherReference.setStorage(storage);
        fileDao.update(anotherReference);
        fileDao.remove(file);
        return dataStore.deleteData(userName, fileName);

    }
}
