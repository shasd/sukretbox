package sukretbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

import java.io.FileInputStream;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by sukret on 9/8/15.
 */
public class DropboxStore implements DataStore {

    private DbxClient dbxClient;

    public DropboxStore(String accessToken)  {
        DbxRequestConfig config = new DbxRequestConfig("SukretBox", Locale.getDefault().toString());
        dbxClient = new DbxClient(config, accessToken);
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
