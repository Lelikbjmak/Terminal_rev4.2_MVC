package com.example.Terminal_rev42.Model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
public class user {

    public user(){
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
    @NonNull
    private String confirmedpassword;

    @ManyToMany
    @JoinTable(name = "UsersRoles", joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "roleid"))
    Set<Role> roleSet;
}
