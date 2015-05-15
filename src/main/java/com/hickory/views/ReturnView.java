package com.hickory.views;

import com.hickory.models.Client;
import com.hickory.models.Item;
import com.hickory.models.Order;
import com.hickory.views.forms.AbstractEditForm;
import com.hickory.views.forms.ReturnEditor;
import com.hickory.views.services.ClientConverter;
import com.hickory.views.services.ConvertibleEnumConverter;
import com.hickory.views.services.ItemsConverter;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * @author Evgeny Frolov
 */
public class ReturnView extends CommonView implements Button.ClickListener {
    private JPAContainer<Order> orders;
    private JPAContainer<Client> clients;
    private JPAContainer<Item> items;
    private Table orderTable;
    private Button newButton;
    private Button editButton;
    private Button deleteButton;


    public ReturnView() {

        editButton = new Button("Редактировать", this);
        deleteButton = new Button("Удалить", this);

//        orders = ContainerUtil.getContainer(Order.class);
//        clients = ContainerUtil.getContainer(Client.class);
//        items = ContainerUtil.getContainer(Item.class);

        orderTable = buildOrderTable(orders);


        getStandardViewLayout().addTop(editButton, deleteButton);
        getStandardViewLayout().addContent(orderTable);
        setContent(getStandardViewLayout());
    }


    public static Table buildOrderTable(Container container) {
        Table table = new Table();
        table.setContainerDataSource(container);
        table.setSelectable(true);
        table.setVisibleColumns("number", "fromDate", "client", "contract", "priority", "commentary");
        table.setColumnHeaders("Номер", "Дата (от)", "Клиент", "Договор", "Приоритет", "Комментарий");

        table.setConverter("client", new ClientConverter());
        table.setConverter("items", new ItemsConverter());
        table.setConverter("status", new ConvertibleEnumConverter());
        table.setConverter("priority", new ConvertibleEnumConverter());

        return table;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newButton) {
            newButtonClick();
        } else if (event.getButton() == editButton) {
            editButtonClick();
        } else if (event.getButton() == deleteButton) {
            deleteButtonClick();
        }
    }

    private void newButtonClick() {
        final BeanItem<Order> newOrderItem = new BeanItem<>(new Order());

        final ReturnEditor orderEditor = new ReturnEditor(newOrderItem, clients, items);
        orderEditor.addSaveListener(new AbstractEditForm.SaveListener() {
            @Override
            public void editorSaved(AbstractEditForm.SaveEvent event) {
                orders.addEntity(newOrderItem.getBean());
                commit();
            }
        });

        UI.getCurrent().addWindow(orderEditor);
    }

    private void editButtonClick() {
        com.vaadin.data.Item item = orderTable.getItem(orderTable.getValue());

        if (item == null) {
            Notification.show("Please select item");
            return;
        }

        final ReturnEditor orderEditor = new ReturnEditor(item, clients, items);
        orderEditor.addSaveListener(new AbstractEditForm.SaveListener() {
            @Override
            public void editorSaved(AbstractEditForm.SaveEvent event) {
                commit();
            }
        });

        UI.getCurrent().addWindow(orderEditor);
    }

    private void deleteButtonClick() {
        if (orderTable.getItem(orderTable.getValue()) == null) {
            Notification.show("Please select item");
            return;
        }

        orders.removeItem(orderTable.getValue());
        commit();
    }

    public void commitContainers() {
        orders.commit();
        clients.commit();
        items.commit();
    }
}

