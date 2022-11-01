package com.example.Terminal_rev42.Model;

import com.example.Terminal_rev42.Entities.client;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class user {

    public user(){
        this.enabled = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private long userid;

    @Column(unique = true)
    private String username;

    @NonNull
    private String password;

    @Transient
    private String confirmedpassword;

    @NonNull
    @Column(unique = true)
    private String mail;


    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(unique = true, name = "reset_password_token")
    private String resetPasswordToken;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UsersRoles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userid") ,
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "roleid"))
    private Set<Role> roleset;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
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

}
