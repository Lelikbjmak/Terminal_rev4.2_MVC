package com.example.Terminal_rev42.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class client implements Serializable {

    public client(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NonNull
    private long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "phone")
    private String phone;

    @NonNull
    @Column(name = "passport")
    private String passport;

    @Column(name = "birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @NonNull
    private Date birth;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private Set<bill> bills = new HashSet();


}



