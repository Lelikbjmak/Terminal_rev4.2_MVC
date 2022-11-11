package com.example.Terminal_rev42.Entities;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class receipts {

    public receipts(){}

    public receipts(String type, bill billfrom, bill billto, BigDecimal summa, String currency){
        setType(type);
        setBillfrom(billfrom);
        setBillto(billto);
        setSumma(summa);
        setCurrency(currency);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String type;

    @ManyToOne
    @JoinColumn(name = "billfrom", referencedColumnName = "card")
    @NotBlank
    private bill billfrom;

    @ManyToOne
    @JoinColumn(name = "billto", referencedColumnName = "card")
    @Nullable
    private bill billto;

    @NotBlank
    private String currency;

    @NotBlank
    @DecimalMin("00.00")
    private BigDecimal summa;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public bill getBillfrom() {
        return billfrom;
    }

    public void setBillfrom(@NonNull bill billfrom) {
        this.billfrom = billfrom;
    }

    @Nullable
    public bill getBillto() {
        return billto;
    }

    public void setBillto(@Nullable bill billto) {
        this.billto = billto;
    }

    @NonNull
    public BigDecimal getSumma() {
        return summa;
    }

    public void setSumma(@NonNull BigDecimal summa) {
        this.summa = summa;
    }

    @NonNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@NonNull String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {

        if(billto == null) {
            return "Receipt: #" + getId() + "\nOperation: " + getType() + "\nConsumer: " + getBillfrom().getCard() + "\nsumma: " + summa +
                    " " + getCurrency() + "\n" + new Date() + "\n-------------Barclays-------------";
        }else {
            return "Receipt: #" + getId() + "\nOperation: " + getType() + "\nSender: " + getBillfrom().getCard() + "\nRecipient: " + getBillto().getCard() + "\nsumma: " + summa +
                    " " + getCurrency() + "\n" + new Date() + "\n-------------Barclays-------------";
        }

    }
}
