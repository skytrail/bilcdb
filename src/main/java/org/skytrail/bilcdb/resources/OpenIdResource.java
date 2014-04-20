package org.skytrail.bilcdb.resources;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.dropwizard.hibernate.UnitOfWork;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.skytrail.bilcdb.BILCConfiguration;
import org.skytrail.bilcdb.auth.openid.DiscoveryInformationMemento;
import org.skytrail.bilcdb.auth.openid.OpenIdCache;
import org.skytrail.bilcdb.db.OpenIdAuthDAO;
import org.skytrail.bilcdb.model.security.DbUser;
import org.skytrail.bilcdb.model.security.OpenIdAuth;
import org.skytrail.bilcdb.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

/**
 * <p>Resource to provide the following to application:</p>
 * <ul>
 * <li>Provision of configuration for public home page</li>
 * </ul>
 *
 * @since 0.0.1
 */
@Path("/openid")
@Produces(MediaType.TEXT_HTML)
public class OpenIdResource extends BaseResource {

    private static final Logger log = LoggerFactory.getLogger(OpenIdResource.class);

    private final static String YAHOO_ENDPOINT = "https://me.yahoo.com";
    private final static String GOOGLE_ENDPOINT = "https://www.google.com/accounts/o8/id";

    private final OpenIdCache openIDCache;
    private final OpenIdAuthDAO dao;
    private final SessionManager sessionManager;

    /**
     * Default constructor
     */
    @Inject
    public OpenIdResource(OpenIdCache openIdCache, OpenIdAuthDAO dao, SessionManager sessionManager) {
        this.openIDCache = openIdCache;
        this.dao = dao;
        this.sessionManager = sessionManager;
    }

    @GET
    @UnitOfWork
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        // TODO (jlh): clear all user sessions
        sessionManager.deleteSession(SessionHelper.getSessionToken(request));
        return Response.seeOther(URI.create("www.google.com")).build();
    }

    /**
     * Handles the authentication request from the user after they select their OpenId server
     *
     * @param identifier The identifier for the OpenId server
     * @return A redirection or a form view containing user-specific permissions
     */
    @GET
    @Path("/login")
    @UnitOfWork
    @SuppressWarnings("unchecked")
    public Response authenticationRequest(
            @Context
            HttpServletRequest request,
            @QueryParam("identifier")
            String identifier
    ) {


        // TODO(jlh): Make this not awful
        try {
            // Create a consumer manager for this specific request and cache it
            // (this is to preserve session state such as nonce values etc)
            ConsumerManager consumerManager = new ConsumerManager();

            // Perform discovery on the user-supplied identifier
            List discoveries = consumerManager.discover(identifier);

            // Attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = consumerManager.associate(discoveries);

            // Create a memento to rebuild the discovered information in a subsequent request
            DiscoveryInformationMemento memento = new DiscoveryInformationMemento();
            if (discovered.getClaimedIdentifier() != null) {
                memento.setClaimedIdentifier(discovered.getClaimedIdentifier().getIdentifier());
            }
            memento.setDelegate(discovered.getDelegateIdentifier());
            if (discovered.getOPEndpoint() != null) {
                memento.setOpEndpoint(discovered.getOPEndpoint().toString());
            }

            memento.setTypes(discovered.getTypes());
            memento.setVersion(discovered.getVersion());

            // Create a temporary User to preserve state between requests without
            // using a session (we could be in a cluster)
            memento.setConsumerManager(consumerManager);

            // Persist the memento
            String sessionToken = openIDCache.putMemento(memento);

            // Redirect the user to their OpenId server authentication process
            // The OpenId server will use this endpoint to provide authentication
            // Parts of this may be shown to the user
            final String returnToUrl;
            if (request.getServerPort() == 80) {
                returnToUrl = String.format(
                        "http://%s/openid/verify?token=%s",
                        request.getServerName(),
                        sessionToken);
            } else {
                returnToUrl = String.format(
                        "http://%s:%d/openid/verify?token=%s",
                        request.getServerName(),
                        request.getServerPort(),
                        sessionToken);
            }
            log.debug("Return to URL '{}'", returnToUrl);

            // Build the AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = consumerManager.authenticate(discovered, returnToUrl);

            // Build the FetchRequest containing the information to be copied
            // from the OpenID provider
            FetchRequest fetch = FetchRequest.createFetchRequest();
            // Attempt to decode each entry
            if (identifier.startsWith(GOOGLE_ENDPOINT)) {
                fetch.addAttribute("email", "http://axschema.org/contact/email", true);
                fetch.addAttribute("firstName", "http://axschema.org/namePerson/first", true);
                fetch.addAttribute("lastName", "http://axschema.org/namePerson/last", true);
            } else if (identifier.startsWith(YAHOO_ENDPOINT)) {
                fetch.addAttribute("email", "http://axschema.org/contact/email", true);
                fetch.addAttribute("fullname", "http://axschema.org/namePerson", true);
            } else { // works for myOpenID
                fetch.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
                fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
            }

            // Attach the extension to the authentication request
            authReq.addExtension(fetch);

            Response r = Response
                    .seeOther(URI.create(authReq.getDestinationUrl(true)))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
            return  r;

        } catch (MessageException e1) {
            log.error("MessageException:", e1);
        } catch (DiscoveryException e1) {
            log.error("DiscoveryException:", e1);
        } catch (ConsumerException e1) {
            log.error("ConsumerException:", e1);
        }
        return Response.ok().build();
    }

    /**
     * Handles the OpenId server response to the earlier AuthRequest
     *
     * @return The OpenId identifier for this user if verification was successful
     */
    @GET
    @UnitOfWork
    @Path("/verify")
    public Response verifyOpenIdServerResponse(
            @Context HttpServletRequest request,
            @QueryParam("token") String rawToken) {

        // TODO(jlh): make this less awful
        // Retrieve the previously stored discovery information from the temporary User
        if (rawToken == null) {
            log.debug("Authentication failed due to no session token");
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        // Attempt to locate the consumer manager by the session token
        Optional<DiscoveryInformationMemento> mementoOptional = openIDCache.getMemento(rawToken);

        if (!mementoOptional.isPresent()) {
            log.debug("Authentication failed due to no consumer manager matching session token {}", rawToken);
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        final DiscoveryInformationMemento memento = mementoOptional.get();
        ConsumerManager consumerManager = memento.getConsumerManager();

        // Retrieve the discovery information
        Identifier identifier = new Identifier() {
            @Override
            public String getIdentifier() {
                return memento.getClaimedIdentifier();
            }
        };

        DiscoveryInformation discovered;
        try {
            discovered = new DiscoveryInformation(
                    URI.create(memento.getOpEndpoint()).toURL(),
                    identifier,
                    memento.getDelegate(),
                    memento.getVersion(),
                    memento.getTypes()
            );
        } catch (DiscoveryException e) {
            throw new WebApplicationException(e, Response.Status.UNAUTHORIZED);
        } catch (MalformedURLException e) {
            throw new WebApplicationException(e, Response.Status.UNAUTHORIZED);
        }

        // Extract the receiving URL from the HTTP request
        StringBuffer receivingURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            receivingURL.append("?").append(request.getQueryString());
        }
        log.debug("Receiving URL = '{}", receivingURL.toString());

        // Extract the parameters from the authentication response
        // (which comes in as a HTTP request from the OpenID provider)
        ParameterList parameterList = new ParameterList(request.getParameterMap());

        try {
            // Verify the response
            // ConsumerManager needs to be the same (static) instance used
            // to place the authentication request
            // This could be tricky if this service is load-balanced
            VerificationResult verification = consumerManager.verify(
                    receivingURL.toString(),
                    parameterList,
                    discovered);

            // Examine the verification result and extract the verified identifier
            Optional<Identifier> verified = Optional.fromNullable(verification.getVerifiedId());
            if (verified.isPresent()) {
                // Verified
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();

                // We have successfully authenticated so remove the temp user
                // and replace it with a potentially new one
                openIDCache.deleteMemento(rawToken);

                Optional<OpenIdAuth> authOptional = dao.findById(verified.get().getIdentifier());
                OpenIdAuth auth;
                if (!authOptional.isPresent()) {
                    auth = new OpenIdAuth();
                    auth.setOpenIdIdentifier(verified.get().getIdentifier());
                    DbUser newUser = new DbUser();

                    // TODO(jlh): add the user roles here
                    // tempUser.getAuthorities().add(Authority.ROLE_PUBLIC);
                    // Extract additional information
                    if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                        newUser.setEmailAddress(extractEmailAddress(authSuccess));
                        newUser.setFirstName(extractFirstName(authSuccess));
                        newUser.setLastName(extractLastName(authSuccess));
                    }
                    auth.setUser(newUser);
                } else {
                    auth = authOptional.get();
                }
                String newSessionId = sessionManager.create(auth);

                // TODO(jlh): this needs to return the right data so angular can render that the user is logged in
                // maybe the email address?

                String home = String.format(
                        "http://%s:%d/bilc/dbui/index.html",
                        request.getServerName(),
                        request.getServerPort());

                return Response
                        .seeOther(URI.create(home))
                        .cookie(replaceSessionTokenCookie(newSessionId))
                        .build();

            } else {
                log.debug("Failed verification");
            }
        } catch (OpenIDException e) {
            // present error to the user
            log.error("OpenIDException", e);
        }

        // Must have failed to be here
        throw new WebApplicationException(Response.Status.UNAUTHORIZED);
    }

    /**
     * @return A cookie with a long term expiry date suitable for use as a session token for OpenID
     */
    private NewCookie replaceSessionTokenCookie(String sessionId) {
        log.debug("Replacing session token with {}", sessionId);
        return new NewCookie(
                BILCConfiguration.SESSION_TOKEN_NAME,
                sessionId,   // Value
                "/",     // Path
                null,    // Domain
                null,    // Comment
                86400 * 30, // 30 days
                false);
    }


    private String extractEmailAddress(AuthSuccess authSuccess) throws MessageException {
        FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
        return getAttributeValue(
                fetchResp,
                "email",
                "",
                String.class);
    }

    private String extractFirstName(AuthSuccess authSuccess) throws MessageException {
        FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
        return getAttributeValue(
                fetchResp,
                "firstname",
                "",
                String.class);
    }

    private String extractLastName(AuthSuccess authSuccess) throws MessageException {
        FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
        return getAttributeValue(
                fetchResp,
                "lastname",
                "",
                String.class);
    }

    @SuppressWarnings({"unchecked", "unused"})
    private <T> T getAttributeValue(FetchResponse fetchResponse, String attribute, T defaultValue, Class<T> clazz) {
        List list = fetchResponse.getAttributeValues(attribute);
        if (list != null && !list.isEmpty()) {
            return (T) list.get(0);
        }

        return defaultValue;

    }

}
