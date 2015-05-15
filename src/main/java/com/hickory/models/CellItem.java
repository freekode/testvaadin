package com.hickory.models;

import javax.persistence.*;

/**
 * @author Evgeny Frolov
 */
@Entity
@Table(name = "cells_items", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class CellItem {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cellId")
    private StoreRowCell cell;

    @ManyToOne
    @JoinColumn(name = "itemId")
    private Item item;

//    private Integer amount = 0;


    public CellItem() {
    }

    public CellItem(StoreRowCell cell, Item item) {
        this.cell = cell;
        this.item = item;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StoreRowCell getCell() {
        return cell;
    }

    public void setCell(StoreRowCell cell) {
        this.cell = cell;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellItem cellItem = (CellItem) o;

        if (cell != null ? !cell.equals(cellItem.cell) : cellItem.cell != null) return false;
        if (item != null ? !item.equals(cellItem.item) : cellItem.item != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cell != null ? cell.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }
}
