package com.example.Terminal_rev42.Model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "Roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private long roleid;

    @NonNull
    private String rolename;

    @ManyToMany(mappedBy = "roleSet")
    private Set<user> userSet;

}
