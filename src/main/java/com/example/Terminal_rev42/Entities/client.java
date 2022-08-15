package com.example.Terminal_rev42.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "client")
public class client implements Serializable {

    public client(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private int ID;

    @NonNull
    @Column(name = "Name")
    private String name;

    @NonNull
    @Column(name = "Phone")
    private String phone;

    @NonNull
    @Column(name = "Passport_id")
    private String passport;

    @Column(name = "Birth")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @Nullable
    private Date Birth;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "client")
    private bill bill;

    public int getID() {
        return ID;
    }
}



