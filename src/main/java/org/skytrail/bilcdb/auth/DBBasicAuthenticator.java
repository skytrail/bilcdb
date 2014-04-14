package org.skytrail.bilcdb.auth;

import com.google.inject.Inject;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import org.skytrail.bilcdb.BILCConfiguration;
import org.skytrail.bilcdb.model.security.BasicAuth;
import org.skytrail.bilcdb.model.security.DBUser;
import org.skytrail.bilcdb.db.BasicAuthDAO;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class DBBasicAuthenticator implements BasicAuthenticator {

    private final BasicAuthDAO basicAuthDAO;
    private final Cipher cipher;

    @Inject
    public DBBasicAuthenticator(BasicAuthDAO basicAuthDAO,
                                BILCConfiguration.BasicAuthConfiguration config) {
        this.basicAuthDAO = basicAuthDAO;
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(config.getMasterKey().getBytes(Charset.forName("UTF-8")), "AES"),
                    new IvParameterSpec(new byte[cipher.getBlockSize()]));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Fatal encryption error", e);
        }
    }

    @Override
    public Optional<DBUser> authenticate(BasicCredentials credentials) throws AuthenticationException {
        Optional<BasicAuth> auth = basicAuthDAO.findByKey(getPasswordKey(credentials.getPassword()));
        if(auth.isPresent()) {
            return Optional.of(auth.get().getUser());
        }
        // TODO(jlh): this should probably throw instead of returning absent.
        return Optional.absent();
    }

    private String getPasswordKey(String password) {
        try {
            return new String(cipher.doFinal(password.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        } catch(GeneralSecurityException e) {
            throw new RuntimeException("Fatal encryption error", e);
        }
    }
}
