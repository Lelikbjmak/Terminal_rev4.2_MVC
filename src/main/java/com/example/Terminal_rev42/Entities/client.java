package com.example.Terminal_rev42.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
public class client implements Serializable {

    client(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private int ID;

    @NonNull
    @Column(name = "Name")
    private String name;

    @NonNull
    @Column(name = "Phone")
    private String Phone;

    @NonNull
    @Column(name = "Passport_id")
    private String Passport_id;

    @Column(name = "Birth")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @NonNull
    private Date Birth;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "client")
    private bill bill;


}
