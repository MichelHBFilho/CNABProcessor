package com.michelfilho.cnabprocessorapi.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Table("transactions")
public record Transaction(
        @Id Long id,
        @Column("type") int type,
        @Column("date") Date date,
        @Column("value") BigDecimal value,
        @Column("cpf") String cpf,
        @Column("card") String card,
        @Column("hour") Time hour,
        @Column("store_owner") String storeOwner,
        @Column("store_name") String storeName
) implements Serializable {

    public Transaction withDate(String dateString) throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyyMMdd");
        var date = dateFormat.parse(dateString);

        return new Transaction(
                this.id(),
                this.type(),
                new Date(date.getTime()),
                this.value(),
                this.cpf(),
                this.card(),
                this.hour(),
                this.storeOwner(),
                this.storeName()
        );
    }

    public Transaction withHour(String hourString) throws ParseException {
        var dateFormat = new SimpleDateFormat("HHmmss");
        var hour = dateFormat.parse(hourString);

        return new Transaction(
                this.id(),
                this.type(),
                this.date(),
                this.value(),
                this.cpf(),
                this.card(),
                new Time(hour.getTime()),
                this.storeOwner(),
                this.storeName()
        );
    }

    public Transaction withValue(BigDecimal value) {
        return new Transaction(
                id,
                type,
                date,
                value,
                cpf,
                card,
                hour,
                storeOwner,
                storeName
        );
    }

}
