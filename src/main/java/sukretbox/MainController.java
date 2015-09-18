package sukretbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sukretbox.exceptions.StorageLimitExceededException;
import sukretbox.exceptions.UserDoesNotExistException;
import sukretbox.exceptions.UserExistsException;
import sun.applet.Main;

import javax.servlet.http.HttpServletRequest;
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

    // upload a file
    @RequestMapping(value = "files/{fileName:.+}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> uploadFile(@PathVariable("fileName") String fileName,
                                           @RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User user = null;

        boolean fileAddSuccess = false, dataStoreSuccess = false, userUpdateSuccess = false;
        try {
            try {
                user = userDao.getByName(userName);
            } catch (UserDoesNotExistException e) {
                return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
            }

            try {
                user.increaseCurrentStorage(file.getSize());
            } catch (StorageLimitExceededException e) {
                return new ResponseEntity<String>(HttpStatus.INSUFFICIENT_STORAGE);
            }

            userUpdateSuccess = userDao.update(user);
            if (userUpdateSuccess) {
                fileAddSuccess = storageManager.storeFile(userName, fileName, file.getBytes());
                if (fileAddSuccess) {
                    return new ResponseEntity<String>(HttpStatus.OK);
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
        return new ResponseEntity<String>(HttpStatus.METHOD_NOT_ALLOWED);
    }
    // download a file
    @RequestMapping(value = "files/{fileName:.+}", method = RequestMethod.GET) // :.+ needed to accept file extensions
    public @ResponseBody byte[] getFile(@PathVariable("fileName") String fileName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return storageManager.getFile(userName, fileName);
    }
    // list files
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public @ResponseBody List<File> listFiles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return fileDao.getByUserName(userName);
    }

    // register new user
    @RequestMapping(value = "register/{userName}/{password}", method = RequestMethod.PUT)
    public @ResponseBody ResponseEntity<String> newUser(@PathVariable("userName") String userName,
                                                        @PathVariable("password") String password) {
        try {
            userDao.add(userName, password);
            return new ResponseEntity<String>( "Successfully added user " + userName, HttpStatus.OK);

        } catch(UserExistsException e) {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    // get name of logged in user
    @RequestMapping(value = "username", method = RequestMethod.GET)
    public @ResponseBody String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // just to test file deletion, should be RequestMethod.DELETE
    @RequestMapping(value = "deleteFile/{fileName:.+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName) throws Exception {
        File file = fileDao.get(SecurityContextHolder.getContext().getAuthentication().getName(), fileName);
        storageManager.deleteFile(file.getUserName(), file.getFileName());
        User user = userDao.getByName(file.getUserName());
        user.decreaseCurrentStorage(file.getSize());
        userDao.update(user);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
