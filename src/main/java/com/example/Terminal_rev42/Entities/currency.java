package com.example.Terminal_rev42.Entities;

import com.example.Terminal_rev42.resoursec.currencyenum;
import lombok.NonNull;

import javax.persistence.*;

@Entity
public class currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NonNull
    private long id;

    @Column(name = "Currency")
    @Enumerated(EnumType.STRING)
    private currencyenum currencyenum;

}
