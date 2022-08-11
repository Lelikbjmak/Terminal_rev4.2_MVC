package com.example.Terminal_rev42.Entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class bill implements Serializable {

    bill(){}

    @Id
    @NonNull
    private String card;

    @OneToOne(optional = false)
    @JoinColumn(name = "client_id", referencedColumnName = "ID", nullable = false)
    private client client;


    @NonNull
    private String currency;


    private double Ledger;

    @Nullable
    private short password;

}
