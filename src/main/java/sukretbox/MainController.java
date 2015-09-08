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
    StorageManager storageManager;

    @RequestMapping(value = "files/{fileName:.+}", method = RequestMethod.POST)
    public @ResponseBody String uploadFile(@PathVariable("fileName") String fileName,
                                           @RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User user = null;

        boolean fileAddSuccess = false, dataStoreSuccess = false, userUpdateSuccess = false;
        try {
            try {
                user = userDao.getByName(userName);
            } catch (UserDoesNotExistException e) {
                return "Failed to add file. User does not exist.";
            }

            try {
                user.increaseCurrentStorage(file.getSize());
            } catch (StorageLimitExceededException e) {
                return "Failed to add file, storage limit would be exceeded.";
            }

            userUpdateSuccess = userDao.update(user);
            if (userUpdateSuccess) {
                fileAddSuccess = storageManager.storeFile(userName, fileName, file.getBytes());
                if (fileAddSuccess) {
                    return "successfully added file " + fileName + " for user " + userName + "\n";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        // roll back updates to metadata when file can not be added to storage
        if(userUpdateSuccess) {
            user.decreaseCurrentStorage(file.getSize());
            userDao.update(user);
        }
        return"failed to add file "+fileName+" for user "+userName+"\n";
    }

    @RequestMapping(value = "files/{fileName:.+}", method = RequestMethod.GET) // :.+ needed to accept file extensions
    public @ResponseBody byte[] getFile(@PathVariable("fileName") String fileName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return storageManager.getData(userName, fileName);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
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
