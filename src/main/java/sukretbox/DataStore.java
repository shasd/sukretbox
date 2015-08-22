package sukretbox;

import java.util.List;

public interface DataStore {

    byte[] getData(String userName, String fileName);
    boolean storeData(String userName, String fileName, byte[] data);
    public List<File> listFiles(String userName);
}
