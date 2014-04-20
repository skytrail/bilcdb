package org.skytrail.bilcdb.resources;

import org.skytrail.bilcdb.BILCConfiguration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by herndon on 4/20/14.
 */
public class SessionHelper {

    private SessionHelper() {
    }

    public static String getSessionToken(HttpServletRequest request) {
        for(Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(BILCConfiguration.SESSION_TOKEN_NAME)) {
                return cookie.getValue();
            }
        }

        // Todo - add an exception mapper that turns exceptions into HTTP resonse codes
        throw new RuntimeException("Not found");
    }
}
