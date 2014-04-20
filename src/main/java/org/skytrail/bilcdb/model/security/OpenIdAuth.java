package org.skytrail.bilcdb.model.security;

import javax.persistence.*;

@Entity
@Table(name = "openid")
@NamedQueries({
        @NamedQuery(
                name = "org.skytrail.bilcdb.model.security.OpenIdAuth.findById",
                query = "SELECT o FROM OpenIdAuth o WHERE o.id = :id"
        )
})

public class OpenIdAuth implements Auth {
    @Id
    @Column(name = "id", nullable = false)
    private String openIdIdentifier;

    @OneToOne
    @JoinColumn(name="userId")
    private DbUser user;

    @Override
    public DbUser getUser() {
        return user;
    }

    public void setUser(DbUser user) {
        this.user = user;
    }

    public String getOpenIdIdentifier() {
        return openIdIdentifier;
    }

    public void setOpenIdIdentifier(String openIdIdentifier) {
        this.openIdIdentifier = openIdIdentifier;
    }
}
