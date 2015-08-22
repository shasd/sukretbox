package sukretbox;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

// currently stores all data in memory using a hashmap

public class MemoryStore implements DataStore {

    ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> users;
    
    public MemoryStore() {
    	users = new ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>>();
    }
   
    // returns null if file or user not found, the file data o/w
    public byte[] getData(String userName, String fileName) {
    	Map<String, byte[]> userData = users.get(userName);
		if(userData == null)
			return null;
		return userData.get(fileName);

    }
    
    public boolean storeData(String userName, String fileName, byte[] data) {
        // get user data or create new user if does not exist
		ConcurrentHashMap<String, byte[]> userData = users.get(userName);
        if(userData == null) {
            userData = new ConcurrentHashMap<String, byte[]>();
            users.put(userName, userData);
        }
        // put data
        userData.put(fileName, data);
        return true;
    }

    // to be implemented
    public List<File> listFiles(String userName){
        return null;
    }
    
}
