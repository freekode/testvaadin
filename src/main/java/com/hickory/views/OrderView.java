package com.hickory.views;

import com.hickory.models.Address;
import com.hickory.models.Item;
import com.hickory.models.Order;
import com.hickory.models.OrderItem;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.OrderForm;
import com.hickory.views.services.ClientConverter;
import com.hickory.views.services.ConvertibleEnumConverter;
import com.hickory.views.services.FilterTableDecorator;
import com.hickory.views.services.ItemsConverter;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import org.tepi.filtertable.FilterTable;

/**
 * @author Evgeny Frolov
 */
public class OrderView extends CommonView implements ItemClickEvent.ItemClickListener {
    private JPAContainer<Order> orderJpa;
    private JPAContainer<OrderItem> orderItemJpa;
    private JPAContainer<Item> itemJpa;
    private JPAContainer<Address> addressJpa;
    private FilterTable orderTable;

    public OrderView() {
        orderJpa = ContainerBuilder.getContainer(Order.class);
        orderItemJpa = ContainerBuilder.getContainer(OrderItem.class);
        itemJpa = ContainerBuilder.getContainer(Item.class);
        addressJpa = ContainerBuilder.getContainer(Address.class);

        orderJpa.addContainerFilter(new Compare.Equal("status", Order.StatusType.values()[0]));

        orderTable = configureOrderTable(buildOrderTable(orderJpa));

        getStandardViewLayout().addContent(buildTabSheet(), orderTable);

        setContent(getStandardViewLayout());
    }

    public static FilterTable buildOrderTable(Container container) {
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");
        table.setWidth("100%");

        table.setVisibleColumns("number", "fromDate", "client", "status", "shipmentDate", "commentary");
        table.setColumnHeaders("Номер", "Дата (от)", "Клиент", "Статус", "Дата доставки", "Комментарий");

        table.setConverter("client", new ClientConverter());
        table.setConverter("items", new ItemsConverter());
        table.setConverter("status", new ConvertibleEnumConverter());
        table.setConverter("priority", new ConvertibleEnumConverter());

        table.setFilterDecorator(new FilterTableDecorator());


        return table;
    }

    private TabSheet buildTabSheet() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.addStyleName("equal-width-tabs");

        for (Order.StatusType type : Order.StatusType.values()) {
            tabSheet.addTab(new VerticalLayout(), type.getValue()).setDescription(type.toString());
        }

        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                TabSheet tabSheet = event.getTabSheet();
                TabSheet.Tab tab = tabSheet.getTab(tabSheet.getSelectedTab());

                orderJpa.removeAllContainerFilters();
                orderJpa.addContainerFilter(new Compare.Equal("status", Order.StatusType.valueOf(tab.getDescription())));
            }
        });


        return tabSheet;
    }

    public FilterTable configureOrderTable(FilterTable table) {
        table.addItemClickListener(this);
        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {


                        orderJpa.removeItem(itemId);
                        commit();
                    }
                });
                removeBtn.setIcon(FontAwesome.TRASH_O);
                removeBtn.setStyleName("borderless");
                return removeBtn;
            }
        });


        String[] columnHeaders = table.getColumnHeaders();
        columnHeaders[columnHeaders.length - 1] = "Удалить";
        table.setColumnHeaders(columnHeaders);


        return table;
    }

    @Override
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == orderTable && event.isDoubleClick()) {
            EntityItem<Order> item = orderJpa.getItem(event.getItemId());

            OrderForm orderForm = new OrderForm(item, itemJpa, addressJpa, orderItemJpa);
            orderForm.addSaveListener(new AbstractForm.ClickListener() {
                @Override
                public void saved(AbstractForm.ClickEvent event) {
                    commit();
                    setContent(getStandardViewLayout());
                }

                @Override
                public void cancelled(AbstractForm.ClickEvent event) {
                    orderItemJpa.discard();
                    setContent(getStandardViewLayout());
                }
            });
            setContent(orderForm);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        orderJpa.refresh();
        orderItemJpa.refresh();
        itemJpa.refresh();
        addressJpa.refresh();
    }

    @Override
    public void commitContainers() {
        orderJpa.commit();
        orderItemJpa.commit();
    }

    @Override
    public void commitFailed() {
        orderJpa.refresh();
        orderItemJpa.refresh();
    }
}
