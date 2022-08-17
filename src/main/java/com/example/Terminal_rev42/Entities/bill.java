package com.example.Terminal_rev42.Entities;


import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Entity
public class bill implements Serializable {

    public bill(){
        this.card = Long.toString(ThreadLocalRandom.current().nextLong(1_000_000_000_000_000L, 9_999_999_999_999_999L)).replaceAll("(.{4})", "$1 ").trim();
        this.ledger = 0.00;
        this.pin = "1212";
        this.active = true;
    }

    @Id
    @NonNull
    private String card;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private client client;

    @NonNull
    private String type;

    @NonNull
    private String currency;

    @Nullable
    private double ledger;

    @Nullable
    private String pin;

//    @NonNull
//    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private Date validity;
//
//    @NonNull
//    private String cvv;

    @NonNull
    private boolean active;

}
