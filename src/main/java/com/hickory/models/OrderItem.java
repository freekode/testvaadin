package com.hickory.models;

import javax.persistence.*;

/**
 * @author Evgeny Frolov
 */
@Entity
@Table(name = "orders_items", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
public class OrderItem {
    @Id
    @Column(columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "itemId")
    private Item item;

    private Integer amount = 0;


    public OrderItem() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getAmount() {
        return amount;
    }

    public boolean setAmount(Integer amount) {
        if ((getItem().getLeftAmount() + this.amount) >= amount) {
            this.amount = amount;
            return true;
        }

        return false;
    }
}
