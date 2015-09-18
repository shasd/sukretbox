package sukretbox;

import sukretbox.exceptions.StorageLimitExceededException;

/**
 * Created by sukret on 8/11/15.
 */
public class User {
    private String name;
    private long currentStorage;
    private long storageLimit;
    private String password;
    private String dbxToken;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public User(String name, long currentStorage, long storageLimit) {
        this.name = name;
        this.currentStorage = currentStorage;
        this.storageLimit = storageLimit;
    }
    public User(String name, long currentStorage, long storageLimit, String password, String dbxToken) {
        this.name = name;
        this.currentStorage = currentStorage;
        this.storageLimit = storageLimit;
        this.password = password;
        this.dbxToken = dbxToken;
    }

    public String getDbxToken() {
        return dbxToken;
    }

    public void setDbxToken(String dbxToken) {
        this.dbxToken = dbxToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCurrentStorage() {
        return currentStorage;
    }

    public void setCurrentStorage(long currentStorage) {
        this.currentStorage = currentStorage;
    }

    public long getStorageLimit() {
        return storageLimit;
    }

    public void setStorageLimit(long storageLimit) {
        this.storageLimit = storageLimit;
    }

    public void increaseCurrentStorage(long newFileSize) throws StorageLimitExceededException {
        if(currentStorage + newFileSize > storageLimit)
            throw new StorageLimitExceededException();
        currentStorage += newFileSize;
    }

    public void decreaseCurrentStorage(long newFileSize) {
        currentStorage -= newFileSize;
    }
}
