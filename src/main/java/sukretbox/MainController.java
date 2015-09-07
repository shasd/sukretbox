package sukretbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sukretbox.exceptions.StorageLimitExceededException;
import sukretbox.exceptions.UserDoesNotExistException;
import sukretbox.exceptions.UserExistsException;

import java.util.List;

@RestController
@RequestMapping(value = "/users/")
public class MainController {

    @Autowired
    UserDao userDao;

    @Autowired
    FileDao fileDao;

    DataStore dataStore;

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @RequestMapping(value = "{userName}/{password}/files/{fileName:.+}", method = RequestMethod.POST)
    public @ResponseBody String uploadFile(@PathVariable("userName") String userName,
                                           @PathVariable("fileName") String fileName,
                                           @PathVariable("password") String password,
                                           @RequestParam("file") MultipartFile file) {

        File newFile = new File(userName, fileName, file.getSize());
        User user = null;
        boolean fileAddSuccess = false, dataStoreSuccess = false, userUpdateSuccess = false;
        try {
            try {
                user = userDao.getByName(userName);
            } catch (UserDoesNotExistException e) {
                return "Failed to add file. User does not exist.";
            }
            if(!user.getPassword().equals(password)) {
                return "Password incorrect";
            }
            List<File> existingFiles = fileDao.getByUserName(userName);
            if (existingFiles.stream().anyMatch(f -> f.getFileName().equals(fileName)))
                return "File with same name exists";

            try {
                user.increaseCurrentStorage(newFile.getSize());
            } catch (StorageLimitExceededException e) {
                return "Failed to add file, storage limit would be exceeded.";
            }

            userUpdateSuccess = userDao.update(user);
            if (userUpdateSuccess) {
                fileAddSuccess = fileDao.add(newFile);
                if (fileAddSuccess) {
                    dataStoreSuccess = dataStore.storeData(userName, fileName, file.getBytes());
                    if (dataStoreSuccess)
                        return "successfully added file " + fileName + " for user " + userName + "\n";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        // roll back updates to metadata when file can not be added to storage
        if(userUpdateSuccess) {
            user.decreaseCurrentStorage(newFile.getSize());
            userDao.update(user);
        }
        if(fileAddSuccess)
            fileDao.remove(newFile);

        return"failed to add file "+fileName+" for user "+userName+"\n";
    }




    @RequestMapping(value = "{userName}/{password}/files/{fileName:.+}", method = RequestMethod.GET) // :.+ needed to accept file extensions
    public @ResponseBody byte[] getFile(@PathVariable("userName") String userName,
                                        @PathVariable("password") String password,
                                        @PathVariable("fileName") String fileName) {
        try {
            User user = userDao.getByName(userName);
            if(!user.getPassword().equals(password)) {
                return "Password incorrect".getBytes();
            }
        }
        catch(UserDoesNotExistException e) {
            return "User does not exist".getBytes();
        }
        return dataStore.getData(userName, fileName);
    }

    @RequestMapping(value = "{userName}/{password}", method = RequestMethod.GET) // :.+ needed to accept file extensions
    public @ResponseBody List<File> listFiles(@PathVariable("userName") String userName,
                                              @PathVariable("password") String password) {
        try {
            // check user exists
            User user = userDao.getByName(userName);
            if(!user.getPassword().equals(password)) {
                return null;
            }
            return fileDao.getByUserName(userName);
        }
        catch(UserDoesNotExistException e) {
            return null;
        }

    }


    @RequestMapping(value = "{userName}/{password}", method = RequestMethod.POST)
    public @ResponseBody String newUser(@PathVariable("userName") String userName,
                                        @PathVariable("password") String password) {
        try {
            userDao.add(userName, password);
            return "Successfully added user " + userName;

        } catch(UserExistsException e) {
            return "Failed to add user " + userName + ". The user exists.";
        } catch(Exception e) {
            e.printStackTrace();
        }

        return "Failed to add user " + userName;
    }


}
