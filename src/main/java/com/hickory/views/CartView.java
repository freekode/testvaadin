package com.hickory.views;

import com.hickory.models.*;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.CartForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.server.VaadinSession;

import java.util.HashSet;
import java.util.Set;

public class CartView extends CommonView {
    private JPAContainer<Order> orderJpa;
    private JPAContainer<Client> clientJpa;
    private JPAContainer<Address> addressJpa;


    public CartView() {
        orderJpa = ContainerBuilder.getContainer(Order.class);
        clientJpa = ContainerBuilder.getContainer(Client.class);
        addressJpa = ContainerBuilder.getContainer(Address.class);


        final EntityItem<Order> newOrder = orderJpa.createEntityItem(new Order());

        CartForm cartForm = new CartForm(newOrder, clientJpa, addressJpa);
        cartForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                orderJpa.addEntity(newOrder.getEntity());
                commit();

                EntityItem<Order> thisOrder = orderJpa.getItem(orderJpa.lastItemId());
                thisOrder.getItemProperty("number").setValue(orderJpa.lastItemId().toString());
                commit();


//                Set<OrderItem> orderItemSet = (Set<OrderItem>) thisOrder.getItemProperty("orderItems").getValue();
//                for (OrderItem orderItem : orderItemSet) {
//                    EntityItem<Item> entityItem = itemJpa.getItem(orderItem.getItem().getId());
//                    Set<OrderItem> itemOrders = (Set<OrderItem>) entityItem.getItemProperty("itemOrders").getValue();
//                    itemOrders.add(orderItem);
//
//                    entityItem.getItemProperty("itemOrders").setValue(itemOrders);
//                }
//                itemJpa.commit();

                VaadinSession.getCurrent().setAttribute("cart", new HashSet<>());

                getUI().getNavigator().navigateTo("cart");
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
            }
        });

        setContent(cartForm);
    }


    public void commitContainers() {
        orderJpa.commit();
    }

    @Override
    public void commitFailed() {
        orderJpa.refresh();
        clientJpa.refresh();
        addressJpa.refresh();
    }
}
