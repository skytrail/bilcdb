package org.skytrail.bilcdb.resources;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.skytrail.bilcdb.db.DbUserDao;
import org.skytrail.bilcdb.model.security.DbUser;
import org.skytrail.bilcdb.session.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Provides informations about the logged in user
 */
@Path("/user")
@Produces("application/json")
public class UserResource {

    private final DbUserDao dao;
    private final SessionManager sessionManager;

    @Inject
    public UserResource(DbUserDao dao, SessionManager sessionManager) {
        this.dao = dao;
        this.sessionManager = sessionManager;
    }

    @GET
    public Response getUserDetails(@Context HttpServletRequest request) {
        Optional<DbUser> dbUser = sessionManager.get(SessionHelper.getSessionToken(request));
        if (dbUser.isPresent()) {
            return Response.ok(dbUser, MediaType.APPLICATION_JSON_TYPE).build();
        }

        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

}
