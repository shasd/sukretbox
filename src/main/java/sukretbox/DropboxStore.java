package sukretbox;

import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Created by sukret on 9/8/15.
 * For storing files on DropBox account of user
 */
public class DropboxStore implements DataStore {

    private String appKey, appSecret;

    public DropboxStore() throws Exception {
        // read credentials from file, should be created for each deployment
        Scanner sc = new Scanner(new FileInputStream(new java.io.File("credentials")));
        appKey = sc.next();
        appSecret = sc.next();
    }

    @Override
    public byte[] getData(String userName, String fileName) {
        return new byte[0];
    }

    @Override
    public boolean storeData(String userName, String fileName, byte[] data) {
        return false;
    }

    @Override
    public List<File> listFiles(String userName) {
        return null;
    }

    @Override
    public boolean deleteData(String userName, String fileName) {
        return false;
    }

    @Override
    public boolean copyFile(String userName, String fileName, String newUserName, String newFileName) {
        return false;
    }
}
