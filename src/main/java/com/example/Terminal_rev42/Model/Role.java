package com.example.Terminal_rev42.Model;

import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Roles")
public class Role {

    public Role(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roleid;   //Pattern = "ROLE_NAME"

    @NotBlank
    @Column(unique = true)
    private String role;

    @ManyToMany(mappedBy = "roleset")
    private Set<User> userSet;

    public long getRoleid() {
        return roleid;
    }

    public void setRoleid(long roleid) {
        this.roleid = roleid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }
}
