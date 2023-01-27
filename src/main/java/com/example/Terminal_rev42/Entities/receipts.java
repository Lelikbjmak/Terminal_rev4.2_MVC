package com.example.Terminal_rev42.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
public class receipts {

    public receipts(String type, bill billFrom, bill billTo, BigDecimal summa, String currencyFrom, String currencyTo) {  // for CashTransfer (2 bills take parts)
        setType(type);
        setBillFrom(billFrom);
        setBillTo(billTo);
        setSumma(summa);
        setCurrencyFrom(currencyFrom);
        setCurrencyTo(currencyTo);
        date = LocalDate.now();
    }

    public receipts(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Type of receipt can't be blank.")
    private String type;

    @ManyToOne
    @JoinColumn(name = "billfrom", referencedColumnName = "card")
    @Valid
    private bill billFrom;

    @ManyToOne
    @JoinColumn(name = "billto", referencedColumnName = "card")
    @Nullable
    private bill billTo;

    @Column(length = 5)
    @NotBlank(message = "Currency of executed operation can't be blank.")
    private String currencyFrom;

    @Column(length = 5)
    @NotBlank(message = "Currency transfer is mandatory.")
    private String currencyTo;

    @DecimalMin(value = "00.00", message = "Summa of accomplished operation must be more than 00.00.")
    private BigDecimal summa;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Minsk")
    @PastOrPresent
    private LocalDate date;

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

    public bill getBillFrom() {
        return billFrom;
    }

    public void setBillFrom(bill billFrom) {
        this.billFrom = billFrom;
    }

    @Nullable
    public bill getBillTo() {
        return billTo;
    }

    public void setBillTo(@Nullable bill billTo) {
        this.billTo = billTo;
    }

    @NonNull
    public BigDecimal getSumma() {
        return summa;
    }

    public void setSumma(@NonNull BigDecimal summa) {
        this.summa = summa;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {

        if(billTo == null && type.equals("Convert")){
            return "Receipt: #" + getId() + "\nOperation: " + getType() + " " + getCurrencyFrom().concat(getCurrencyTo()) + "\nConsumer: " + getBillFrom().getCard() + "\nsumma: " + summa +
                    " " + getCurrencyTo() + "\n" + new Date() + "\n-------------Barclays-------------";
        }

        if(billTo == null && type.equals("Cash extradition")){
            return "Receipt: #" + getId() + "\nOperation: " + getType() + " " + getCurrencyFrom() + "\nConsumer: " + getBillFrom().getCard() + "\nsumma: " + summa +
                    " " + getCurrencyTo() + "\n" + new Date() + "\n-------------Barclays-------------";
        }

        if(billTo == null) {
            return "Receipt: #" + getId() + "\nOperation: " + getType() + " " + getCurrencyFrom().concat(getCurrencyTo()) + "\nConsumer: " + getBillFrom().getCard() + "\nsumma: " + summa +
                    " " + getCurrencyFrom() + "\n" + new Date() + "\n-------------Barclays-------------";
        }

        return "Receipt: #" + getId() + "\nOperation: " + getType()  + " " + getCurrencyFrom().concat(getCurrencyTo()) + "\nSender: " + getBillFrom().getCard() + "\nRecipient: " + getBillTo().getCard() + "\nsumma: " + summa + " " + getCurrencyFrom() + "\n" + new Date() + "\n-------------Barclays-------------";

    }
}
