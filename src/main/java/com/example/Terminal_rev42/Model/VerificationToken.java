package com.example.Terminal_rev42.Model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "VerToken")
public class VerificationToken {

    private static final int EXPIRATION = 60 * 8;  // validity - 8 hours to confirm email

    public VerificationToken(){
        this.setExpiryDate(calculateExpiryDate(VerificationToken.EXPIRATION));
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String token;

    @Column(updatable = true)
    @Basic(optional = false)
    private Date expiryDate;

    @OneToOne(targetEntity = user.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private user user;

    @Column(name = "expiredAt")
    private Date expiredAt;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public void setToken(@NonNull String token) {
        this.token = token;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public com.example.Terminal_rev42.Model.user getUser() {
        return user;
    }

    public void setUser(com.example.Terminal_rev42.Model.user user) {
        this.user = user;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    public void rebuildExistingToken(){
        this.setExpiryDate(calculateExpiryDate(VerificationToken.EXPIRATION));   // reset expired date to prolong | or use setter for expired date
    }

}
