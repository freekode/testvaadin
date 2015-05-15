package com.hickory.models;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "stores_rows_cells", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class StoreRowCell {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rowId")
    private StoreRow row;

    private String barcode;

    @ManyToMany
    @JoinTable(name = "cells_items",
            joinColumns = {@JoinColumn(name = "cellId")},
            inverseJoinColumns = {@JoinColumn(name = "itemId")})
    private Set<Item> items = new HashSet<>();

    @OneToMany(mappedBy = "cell")
    private Set<CellItem> cellItems = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;


    public StoreRowCell() {
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

    public StoreRow getRow() {
        return row;
    }

    public void setRow(StoreRow row) {
        this.row = row;
    }

    public Set<Item> getItems() {
        return new HashSet<>(items);
    }

    public void setItems(Set<Item> items) {
        this.items = items;
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

    public Set<CellItem> getCellItems() {
        return new HashSet<>(cellItems);
    }

    public void setCellItems(Set<CellItem> cellItems) {
        this.cellItems = cellItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreRowCell that = (StoreRowCell) o;

        if (createDate != null ? !createDate.equals(that.createDate) : that.createDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}
