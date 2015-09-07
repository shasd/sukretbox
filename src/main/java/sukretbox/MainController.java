package sukretbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sukretbox.exceptions.StorageLimitExceededException;
import sukretbox.exceptions.UserDoesNotExistException;
import sukretbox.exceptions.UserExistsException;

import java.util.List;

@RestController
@RequestMapping
public class MainController {

    @Autowired
    UserDao userDao;

    @Autowired
    FileDao fileDao;

    @Autowired
    DataStore dataStore;

    @RequestMapping(value = "files/{fileName:.+}", method = RequestMethod.POST)
    public @ResponseBody String uploadFile(@PathVariable("fileName") String fileName,
                                           @RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        File newFile = new File(userName, fileName, file.getSize());
        User user = null;
        Logger logger = LoggerFactory.getLogger(MainController.class);
        boolean fileAddSuccess = false, dataStoreSuccess = false, userUpdateSuccess = false;
        try {
            try {
                user = userDao.getByName(userName);
            } catch (UserDoesNotExistException e) {
                return "Failed to add file. User does not exist.";
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

    @RequestMapping(value = "files/{fileName:.+}", method = RequestMethod.GET) // :.+ needed to accept file extensions
    public @ResponseBody byte[] getFile(@PathVariable("fileName") String fileName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return dataStore.getData(userName, fileName);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET) // :.+ needed to accept file extensions
    public @ResponseBody List<File> listFiles() {
        // check user exists
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return fileDao.getByUserName(userName);
    }


    @RequestMapping(value = "register/{userName}/{password}", method = RequestMethod.POST)
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

    @RequestMapping(value = "username", method = RequestMethod.GET)
    public @ResponseBody String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
