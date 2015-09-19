package sukretbox;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by sukret on 9/8/15.
 * For storing files on DropBox account of user
 */
public class DropboxStore implements DataStore {

    private DbxClient dbxClient;

    public DropboxStore(String accessToken)  {
        DbxRequestConfig config = new DbxRequestConfig("SukretBox", Locale.getDefault().toString());
        dbxClient = new DbxClient(config, accessToken);
    }

    public List<File> listFilesWithHashes(String userName) {
        DbxEntry.WithChildren listing;
        try {
            listing = dbxClient.getMetadataWithChildren("/");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        return listing.children.stream().filter(c -> c.isFile()).map(c -> {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                dbxClient.getFile(c.path, null, os);
            } catch(Exception e) {
                e.printStackTrace();
                return null;
            }
            byte[] data = os.toByteArray();
            return new File(userName,
                    c.asFile().name,
                    c.asFile().numBytes,
                    JenkinsHash.hash64(data),
                    File.Storage.DROPBOX);
        }).collect(Collectors.toList());
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
    public boolean deleteData(String userName, String fileName) {
        return false;
    }

    @Override
    public boolean copyFile(String userName, String fileName, String newUserName, String newFileName) {
        return false;
    }
}
