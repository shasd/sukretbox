package sukretbox;

import com.dropbox.core.*;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

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

    DbxWebAuth webAuth;

    @Autowired
    UserDao userDao;

    @Autowired
    StorageManager storageManager;

    // returns URI for dropbox linking authorization on dropbox website
    @RequestMapping(value = "dropboxAuth", method = RequestMethod.GET)
    public @ResponseBody String getAuthorizeUrl(HttpServletRequest request) throws Exception {
        // check if already linked to dropbox
        if(!userDao.getByName(SecurityContextHolder.getContext().getAuthentication().getName())
                   .getDbxToken()
                   .equals("NONE"))
            return "already authorized";
        // read credentials from file, should be created for each deployment
        Scanner sc = new Scanner(new FileInputStream(new java.io.File("credentials")));
        String appKey = sc.next();
        String appSecret = sc.next();
        DbxAppInfo appInfo = new DbxAppInfo(appKey, appSecret);
        DbxRequestConfig config = new DbxRequestConfig("SukretBox", Locale.getDefault().toString());
        String redirectUri = "https://" + request.getLocalName() + ":" + request.getServerPort() + "/" + "dropboxAuthCode";
        HttpSession session = request.getSession(true);
        String sessionKey = "dropbox-auth-csrf-token";
        DbxSessionStore csrfTokenStore = new DbxStandardSessionStore(session, sessionKey);
        webAuth = new DbxWebAuth(config, appInfo, redirectUri, csrfTokenStore);
        return webAuth.start();
    }

    // dropbox posts the authentication code here after the user allows linking
    @RequestMapping(value = "dropboxAuthCode")
    public RedirectView postCode(HttpServletRequest request) throws Exception {
        DbxAuthFinish authFinish;
        try {
            authFinish = webAuth.finish(request.getParameterMap());
        } catch(Exception e) {
            e.printStackTrace();
            return new RedirectView("index?dbxfail");
        }
        String accessToken = authFinish.accessToken;
        User user = userDao.getByName(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setDbxToken(accessToken);
        userDao.update(user);
        storageManager.mergeDropbox(user.getName(), accessToken);
        return new RedirectView("index?dbxsuccess");
    }
}
