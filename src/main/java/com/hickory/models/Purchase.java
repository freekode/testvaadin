package com.hickory.models;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Evgeny Frolov
 */

@Entity
@Table(name = "purchases", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class Purchase {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "supplierId")
    private Supplier supplier;

    @Temporal(TemporalType.TIMESTAMP)
    private Date selectDate;

    private String file0;

    private String file1;

    private Boolean tax;

    @OneToOne(mappedBy = "purchase")
    private Shipment shipment;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createDate;

    public Purchase() {
        setTax(false);
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

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Date getSelectDate() {
        return selectDate;
    }

    public void setSelectDate(Date date) {
        this.selectDate = date;
    }

    public String getFile0() {
        return file0;
    }

    public void setFile0(String file0) {
        this.file0 = file0;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public Boolean getTax() {
        return tax;
    }

    public void setTax(Boolean tax) {
        this.tax = tax;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
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

        Purchase purchase = (Purchase) o;

        if (createDate != null ? !createDate.equals(purchase.createDate) : purchase.createDate != null) return false;
        if (id != null ? !id.equals(purchase.id) : purchase.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}
