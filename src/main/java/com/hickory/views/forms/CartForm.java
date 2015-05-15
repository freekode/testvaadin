package com.hickory.views.forms;

import com.hickory.models.*;
import com.hickory.popups.ItemAmountPopup;
import com.hickory.popups.ModalPopup;
import com.hickory.services.Utils;
import com.hickory.views.ItemView;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Evgeny Frolov
 */
public class CartForm extends AbstractForm implements ItemClickEvent.ItemClickListener {
    private Container.Filter addressFilter;
    private Set<OrderItem> cartItemSet;
    private Label overallPriceLabel;
    private Label overallPriceLabelTax;
    private FilterTable itemTable;


    public CartForm(EntityItem item, final JPAContainer<Client> clientJpa, final JPAContainer<Address> addressJpa) {
        super(item);


        setCaption("Новый заказ");

        Label section = new Label("Данные заказа");
        section.addStyleName("h3");
        section.addStyleName("colored");
        addToForm(section);

        ComboBox client = new ComboBox("Клиент");
        client.setContainerDataSource(clientJpa);
        client.setConverter(new SingleSelectConverter<Client>(client));
        client.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
        client.setItemCaptionPropertyId("shortcutLegalName");
        client.setNullSelectionAllowed(true);

        addressFilter = new Compare.Equal("id", 0);
        addressJpa.addContainerFilter(addressFilter);
        client.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Client client = clientJpa.getItem(event.getProperty().getValue()).getEntity();
                if (addressFilter != null) {
                    addressJpa.removeContainerFilter(addressFilter);
                }

                addressFilter = new Compare.Equal("client", client);
                addressJpa.addContainerFilter(addressFilter);
            }
        });


        ComboBox delivery = new ComboBox("Доставка");
        for (Order.DeliveryType value : Order.DeliveryType.values()) {
            delivery.addItem(value);
            delivery.setItemCaption(value, value.getValue());
        }


        ComboBox addressComboBox = new ComboBox("Адрес");
        addressComboBox.setContainerDataSource(addressJpa);
        addressComboBox.setConverter(new SingleSelectConverter<Address>(addressComboBox));
        addressComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
        addressComboBox.setItemCaptionPropertyId("title");


        PopupDateField shipmentDate = new PopupDateField("Дата поставки");
        shipmentDate.setResolution(Resolution.SECOND);


        TextArea commentary = new TextArea("Комментарий к заказу");
        commentary.setNullRepresentation("");


        section = new Label("Добавление товара");
        section.addStyleName("h4");
        section.addStyleName("colored");


        cartItemSet = (Set<OrderItem>) VaadinSession.getCurrent().getAttribute("cart");
        if (cartItemSet == null) cartItemSet = new HashSet<>();

        Set<Item> itemSet = new HashSet<>();
        for (OrderItem orderItem : cartItemSet) {
            itemSet.add(orderItem.getItem());
        }
        BeanItemContainer<Item> orderItemsBeanContainer = new BeanItemContainer<>(Item.class, itemSet);
        itemTable = buildCartItemTable(orderItemsBeanContainer);


        overallPriceLabel = new Label();
        overallPriceLabel.setCaption("Общая стоимость");

        overallPriceLabelTax = new Label();
        overallPriceLabelTax.setCaption("В том чилсе НДС");

        recalculatePrice();


        addAndBind(client, "client");
        addAndBind(delivery, "delivery");
        addAndBind(addressComboBox, "address");
        addAndBind(shipmentDate, "shipmentDate");
        addAndBind(commentary, "commentary");

        addToForm(section);
        addToForm(itemTable);
        addToForm(overallPriceLabel);
        addToForm(overallPriceLabelTax);
    }


    public FilterTable buildCartItemTable(final Container container) {
        FilterTable table = ItemView.buildItemTable(container);
        table.setHeight("300px");

        table.addStyleName("no-vertical-lines");
        table.addStyleName("no-horizontal-lines");
        table.addStyleName("borderless");
        table.addItemClickListener(this);
        table.setFilterBarVisible(false);


        table.addGeneratedColumn("genAmount", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, Object itemId, Object columnId) {
                for (OrderItem orderItem : cartItemSet) {
                    if (orderItem.getItem().equals(itemId)) {
                        return orderItem.getAmount();
                    }
                }

                return "error";
            }
        });

        table.addGeneratedColumn("genPrice", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                for (OrderItem orderItem : cartItemSet) {
                    if (orderItem.getItem().equals(itemId)) {
                        return Utils.round(
                                orderItem.getItem().getTradePrice()
                                        .multiply(new BigDecimal(orderItem.getAmount())).floatValue(), 2);
                    }
                }

                return "error";
            }
        });

        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Iterator<OrderItem> iterator = cartItemSet.iterator();
                        while (iterator.hasNext()) {
                            OrderItem orderItem = iterator.next();
                            if (orderItem.getItem().equals(itemId)) {
                                iterator.remove();
                                container.removeItem(itemId);

                                recalculatePrice();

                                break;
                            }
                        }
                    }
                });
                removeBtn.setIcon(FontAwesome.TRASH_O);
                removeBtn.setStyleName("borderless");
                return removeBtn;
            }
        });


        table.setVisibleColumns("factoryCode", "printName", "genAmount", "genPrice", "genRemove");
        table.setColumnHeaders("Код произв.", "Наименование", "Кол-во", "Цена", "Удалить");

        return table;
    }

    private void recalculatePrice() {
        float price = calculateOverallPrice();
        overallPriceLabel.setValue(Utils.round(price, 2).floatValue() + " р.");
        overallPriceLabelTax.setValue(Utils.round(price * 0.18f, 2).floatValue() + " р.");
    }

    public float calculateOverallPrice() {
        Float overallPrice = 0f;
        for (OrderItem orderItem : cartItemSet) {
            overallPrice += orderItem.getItem().getTradePrice().floatValue() * orderItem.getAmount();
        }

        return overallPrice;
    }

    @Override
    public void saveButtonClick() {
        for (OrderItem orderItem : cartItemSet) {
            orderItem.setOrder((Order) getLocalItem().getEntity());
        }

        getLocalItem().getItemProperty("orderItems").setValue(cartItemSet);
        getLocalItem().getItemProperty("manager").setValue(getSession().getAttribute("user"));
    }

    @Override
    public void itemClick(final ItemClickEvent tableEvent) {
        if (tableEvent.getSource() == itemTable && tableEvent.isDoubleClick()) {
            OrderItem selectedOrderItem = null;
            for (OrderItem orderItem : cartItemSet) {
                if (orderItem.getItem().equals(tableEvent.getItemId())) {
                    selectedOrderItem = orderItem;
                    break;
                }
            }

            if (selectedOrderItem == null) {
                return;
            }

            ItemAmountPopup amountPopup = new ItemAmountPopup(selectedOrderItem.getAmount());
            final OrderItem finalSelectedOrderItem = selectedOrderItem;
            amountPopup.addPopupListener(new ModalPopup.ClickListener() {
                @Override
                public void clicked(ModalPopup.ClickEvent event) {
                    ItemAmountPopup.SaveEvent e = (ItemAmountPopup.SaveEvent) event;
                    if (!finalSelectedOrderItem.setAmount(e.getAmount())) {
                        Notification.show("Ошибка выставления количества", Notification.Type.ERROR_MESSAGE);
                        return;
                    }

                    VaadinSession.getCurrent().setAttribute("cart", cartItemSet);

                    itemTable.refreshRowCache();
                    recalculatePrice();
                }
            });

            UI.getCurrent().addWindow(amountPopup);
        }
    }
}
