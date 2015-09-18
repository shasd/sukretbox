package sukretbox;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuthNoRedirect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by sukret on 9/8/15.
 */
@RestController
@Scope("session")
@RequestMapping
public class DropboxAuthController {

    DbxWebAuthNoRedirect webAuth;

    @Autowired
    UserDao userDao;

    @RequestMapping(value = "dropboxAuth", method = RequestMethod.GET)
    public @ResponseBody String getAuthorizeUrl(HttpServletRequest request) throws Exception {
        // read credentials from file, should be created for each deployment
        Scanner sc = new Scanner(new FileInputStream(new java.io.File("credentials")));
        String appKey = sc.next();
        String appSecret = sc.next();
        DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
        DbxRequestConfig config = new DbxRequestConfig("SukretBox",
                Locale.getDefault().toString());
        Logger logger = LoggerFactory.getLogger(DropboxAuthController.class);
        logger.info( request.getRequestURI());
        logger.info(request.getLocalName() + ":" + request.getServerPort() + "/");
        webAuth = new DbxWebAuthNoRedirect(config, appInfo);
        return webAuth.start();
    }

    @RequestMapping(value = "dropboxAuthCode")
    public @ResponseBody String postCode(@RequestParam("") String code) throws Exception {
        String accessToken = webAuth.finish(code).accessToken;
        User user = userDao.getByName(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setDbxToken(accessToken);
        userDao.update(user);

        return "code accepted";
    }
}
