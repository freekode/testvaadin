package com.hickory.models;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "stores_rows", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class StoreRow {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "storeId")
    private Store store;

    @OneToMany(mappedBy = "row")
    private Set<StoreRowCell> cells = new HashSet<>();

    private String barcode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public StoreRow() {
    }

    @PrePersist
    protected void onCreate() {
        setCreateDate(new Date());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Set<StoreRowCell> getCells() {
        return new HashSet<>(cells);
    }

    public void setCells(Set<StoreRowCell> cells) {
        this.cells = cells;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreRow storeRow = (StoreRow) o;

        if (createDate != null ? !createDate.equals(storeRow.createDate) : storeRow.createDate != null) return false;
        if (id != null ? !id.equals(storeRow.id) : storeRow.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}
