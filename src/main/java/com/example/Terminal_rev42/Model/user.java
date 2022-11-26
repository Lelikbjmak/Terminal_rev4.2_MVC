package com.example.Terminal_rev42.Model;

import com.example.Terminal_rev42.Entities.client;

import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
public class user {

    public user(){
        this.enabled = false;
        this.failedAttempts = 0;
        this.temporalLock = false;
    }

    @Transient
    public static final int MAX_FAILED_ATTEMPTS = 3;
    @Transient
    public static final long LOCK_TIME_DURATION =  1; // 24 hours

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long userid;

    @Column(unique = true)
    @NotBlank(message = "Username can't be blank.")
    @Size(min = 4, message = "Username must contain at least 4 symbols.")
    @Size(max = 20, message = "Username is too long.")
    private String username;

    @NotBlank(message = "Password can't be blank.")
    @Size(min = 8, message = "Password must contain at least 8 symbols.")
    private String password;

    @Transient
    @NotBlank(message = "ConfirmedPassword can't be blank.")
    @Size(min = 8, message = "ConfirmedPassword must contain at least 8 symbols.")
    private String confirmedpassword;

    @Column(unique = true)
    @NotBlank(message = "Mail must contain at least 8 symbols.")
    @Email(message = "Not valid format.")
    private String mail;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(unique = true, name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "temporalLock", nullable = false)
    private boolean temporalLock;

    @Column(name = "failed_attempts")
    @Min(0)
    @Max(3)
    private int failedAttempts;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UsersRoles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userid") ,
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "roleid"))
    private Set<Role> roleset;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @Valid
    private client client;

    public com.example.Terminal_rev42.Entities.client getClient() {
        return client;
    }

    public void setClient(com.example.Terminal_rev42.Entities.client client) {
        this.client = client;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedpassword() {
        return confirmedpassword;
    }

    public void setConfirmedpassword(String confirmedpassword) {
        this.confirmedpassword = confirmedpassword;
    }

    public Set<Role> getRoleset() {
        return roleset;
    }

    public void setRoleset(Set<Role> roleset) {
        this.roleset = roleset;
    }

    @NonNull
    public String getMail() {
        return mail;
    }

    public void setMail(@NonNull String mail) {
        this.mail = mail;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getLockTime() {
        return lockTime;
    }

    public void setLockTime(LocalDateTime lockTime) {
        this.lockTime = lockTime;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public boolean isTemporalLock() {
        return temporalLock;
    }

    public void setTemporalLock(boolean temporalLock) {
        this.temporalLock = temporalLock;
    }


}
