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
public class DropboxStore {

    // return list of files and their hashes
    public static List<File> listFilesWithHashes(String userName, String accessToken) {
        DbxRequestConfig config = new DbxRequestConfig("SukretBox", Locale.getDefault().toString());
        DbxClient dbxClient = new DbxClient(config, accessToken);
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

    public static byte[] getData(String userName, String fileName, String accessToken) {
        DbxRequestConfig config = new DbxRequestConfig("SukretBox", Locale.getDefault().toString());
        DbxClient dbxClient = new DbxClient(config, accessToken);
        DbxEntry.WithChildren listing;
        try {
            listing = dbxClient.getMetadataWithChildren("/");
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        List<DbxEntry.File> files = listing.children.stream()
                                                    .filter(c -> c.isFile())
                                                    .map(c -> c.asFile())
                                                    .filter(c -> c.name.equals(fileName))
                                                    .collect(Collectors.toList());
        if(files.size() == 0)
            return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            dbxClient.getFile(files.get(0).path, null, os);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        byte[] data = os.toByteArray();
        return data;
    }

    public static boolean storeData(String userName, String fileName, byte[] data) {
        return false;
    }


    public static boolean deleteData(String userName, String fileName, String accessToken) {
        DbxRequestConfig config = new DbxRequestConfig("SukretBox", Locale.getDefault().toString());
        DbxClient dbxClient = new DbxClient(config, accessToken);
        DbxEntry.WithChildren listing;
        try {
            listing = dbxClient.getMetadataWithChildren("/");
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return listing.children.stream().filter(c -> c.isFile()).map(c -> c.asFile()).anyMatch(f -> {
            if(f.name.equals(fileName)) {
                try {
                    dbxClient.delete(f.path);
                } catch(Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
            return false;
        });
    }

    public boolean copyFile(String userName, String fileName, String newUserName, String newFileName) {
        return false;
    }
}
