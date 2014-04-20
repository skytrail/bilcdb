package org.skytrail.bilcdb.model.security;

import javax.persistence.*;

@Entity
@Table(name = "dbusers")
@NamedQueries({
    @NamedQuery(
        name = "org.skytrail.bilcdb.model.security.DBUser.findById",
        query = "SELECT u FROM DbUser u WHERE u.userId = :id"
    )
})

public class DbUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userId")
    private long userId;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "emailAddress", nullable = false)
    private String emailAddress;

    public long getId() {
        return userId;
    }

    public void setId(long id) {
        this.userId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
