package org.skytrail.bilcdb.model.security;

import javax.persistence.*;

@Entity
@Table(name = "oid_auth")
@NamedQueries({
        @NamedQuery(
                name = "org.skytrail.bilcdb.model.security.OpenIdAuth.findById",
                query = "SELECT oidu FROM oid_auth oid WHERE oid.key = :id"
        )
})

public class OpenIdAuth implements Auth {
    @Id
    @Column(name = "id", nullable = false)
    private String openIdIdentifier;

    @Column(name = "user", nullable = false)
    private DBUser user;

    @Override
    public DBUser getUser() {
        return user;
    }


    public void setUser(DBUser user) {
        this.user = user;
    }

    public String getOpenIdIdentifier() {
        return openIdIdentifier;
    }

    public void setOpenIdIdentifier(String openIdIdentifier) {
        this.openIdIdentifier = openIdIdentifier;
    }
}
