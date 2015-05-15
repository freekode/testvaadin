package com.hickory.views.forms;

import com.hickory.models.Address;
import com.hickory.models.Item;
import com.hickory.models.Order;
import com.hickory.models.OrderItem;
import com.hickory.popups.ItemAmountPopup;
import com.hickory.popups.ModalPopup;
import com.hickory.services.Utils;
import com.hickory.utils.GenerateInvoice;
import com.hickory.utils.GenerateInvoiceFact;
import com.hickory.utils.GenerateTorg12;
import com.hickory.views.ItemView;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.*;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Evgeny Frolov
 */
public class OrderForm extends AbstractForm implements Upload.Receiver, Upload.SucceededListener,
        Upload.ChangeListener, ItemClickEvent.ItemClickListener {
    private Upload uploadFile;
    private TextField contract;
    private boolean isFileChosen;
    private Button addItemBtn;
    private ComboBox itemsComboBox;
    private Set<OrderItem> orderItemSet;
    private JPAContainer<OrderItem> orderItemJpa;
    private BeanItemContainer<Item> itemBeanContainer;
    private FilterTable itemTable;
    private Button billBtn;
    private Button invoiceBtn;
    private Button torg12;
    private NativeSelect status;
    private float price;
    private BrowserWindowOpener opener;
    private FileDownloader fd;
    private Label overallPriceLabel;
    private Label overallPriceLabelTax;


    public OrderForm(final EntityItem item, JPAContainer<com.hickory.models.Item> itemJpa,
                     final JPAContainer<Address> address, final JPAContainer<OrderItem> orderItemJpa) {
        super(item);



        setTitle("Форма заказа");

        this.orderItemJpa = orderItemJpa;

        Object managerValue = item.getItemProperty("manager").getValue();
        Label manager = new Label("");
        if (managerValue != null) {
            manager = new Label(managerValue.toString());
        }
        manager.setCaption("Менеджер");


        Object numberValue = item.getItemProperty("number").getValue();
        Label number = new Label("");
        if (numberValue != null) {
            number = new Label(numberValue.toString());
        }
        number.setCaption("Номер");


        PopupDateField fromDate = new PopupDateField("Дата заказа");
        fromDate.setResolution(Resolution.SECOND);


        Object clientValue = item.getItemProperty("client").getValue();
        Label client = new Label("");
        if (clientValue != null) {
            client = new Label(clientValue.toString());
        }
        client.setCaption("Клиент");

        status = new NativeSelect("Статус");
        status.setReadOnly(true);
        status.setNullSelectionAllowed(false);
        for (Order.StatusType value : Order.StatusType.values()) {
            status.addItem(value);
            status.setItemCaption(value, value.getValue());
        }

        NativeSelect priority = new NativeSelect("Приоритет");
        for (Order.PriorityType value : Order.PriorityType.values()) {
            priority.addItem(value);
            priority.setItemCaption(value, value.getValue());
        }

        NativeSelect delivery = new NativeSelect("Доставка");
        for (Order.DeliveryType value : Order.DeliveryType.values()) {
            delivery.addItem(value);
            delivery.setItemCaption(value, value.getValue());
        }

        PopupDateField shipmentDate = new PopupDateField("Дата поставки");
        shipmentDate.setResolution(Resolution.SECOND);

        ComboBox addressComboBox = new ComboBox("Адрес");
        addressComboBox.setContainerDataSource(address);
        addressComboBox.setConverter(new SingleSelectConverter<Address>(addressComboBox));
        addressComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
        addressComboBox.setItemCaptionPropertyId("title");
        address.addContainerFilter(new Compare.Equal("client", item.getItemProperty("client").getValue()));

        TextArea commentary = new TextArea("Комментарий к заказу");
        commentary.setNullRepresentation("");

        itemsComboBox = new ComboBox(null, itemJpa);
        itemsComboBox.setConverter(new SingleSelectConverter<com.hickory.models.Item>(itemsComboBox));
        itemsComboBox.setItemCaptionPropertyId("printName");
        itemsComboBox.setNullSelectionAllowed(false);

        addItemBtn = new Button("Добавить товар", this);


        orderItemSet = (Set<OrderItem>) getLocalItem().getItemProperty("orderItems").getValue();

        Set<Item> itemSet = (Set<Item>) getLocalItem().getItemProperty("items").getValue();
        itemBeanContainer = new BeanItemContainer<>(Item.class, itemSet);
        itemTable = buildTable();


        overallPriceLabel = new Label();
        overallPriceLabel.setCaption("Общая стоимость");

        overallPriceLabelTax = new Label();
        overallPriceLabelTax.setCaption("В том числе НДС");

        recalculatePrice();


        uploadFile = new Upload("Договор", this);
        uploadFile.setButtonCaption(null);
        uploadFile.addSucceededListener(this);
        uploadFile.addChangeListener(this);
        contract = new TextField();

        billBtn = new Button("Счет", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                GenerateInvoice gi = new GenerateInvoice();
                final Resource res = new FileResource(
                        new File(gi.getFile(item, orderItemJpa,
                                Utils.round(price, 2).floatValue(),
                                Utils.round((Utils.round(price * 0.18f, 2).floatValue()), 2))));
                fd = new FileDownloader(res);
                Page.getCurrent().open(res, "_blank", false);
            }
        });

        invoiceBtn = new Button("Счет фактуры", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                GenerateInvoiceFact gi = new GenerateInvoiceFact();
                final Resource res = new FileResource(
                        new File(gi.getFile(item, orderItemJpa,
                                Utils.round(price, 2).floatValue(),
                                Utils.round((Utils.round(price * 0.18f, 2).floatValue()), 2))));
                fd = new FileDownloader(res);
                Page.getCurrent().open(res, "_blank", false);
            }
        });

        torg12 = new Button("ТОРГ 12", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                GenerateTorg12 gi = new GenerateTorg12();
                final Resource res = new FileResource(
                        new File(gi.getFile(item, orderItemJpa,
                                Utils.round(price, 2).floatValue(),
                                Utils.round((Utils.round(price * 0.18f, 2).floatValue()), 2))));
                fd = new FileDownloader(res);
                Page.getCurrent().open(res, "_blank", false);
            }
        });


        Label sectionTitle = new Label("Данные заказа");
        sectionTitle.addStyleName("h4");
        sectionTitle.addStyleName("colored");
        addToForm(sectionTitle);

        addToForm(manager);
        addToForm(number);

        addToForm(client);
        addToForm(delivery);
        addToForm(addressComboBox);

        addToForm(status);
        addToForm(fromDate);
        addToForm(shipmentDate);

        addToForm(commentary);

        sectionTitle = new Label("Товары");
        sectionTitle.addStyleName("h4");
        sectionTitle.addStyleName("colored");
        addToForm(sectionTitle);


        HorizontalLayout horizontalLayout = new HorizontalLayout(itemsComboBox, addItemBtn);
        horizontalLayout.setSpacing(true);
        addToForm(horizontalLayout);


        addToForm(itemTable);


        sectionTitle = new Label("Документы");
        sectionTitle.addStyleName("h4");
        sectionTitle.addStyleName("colored");
        addToForm(sectionTitle);

        addToForm(overallPriceLabel);
        addToForm(overallPriceLabelTax);

        addToForm(uploadFile);


        horizontalLayout = new HorizontalLayout(billBtn, invoiceBtn, torg12);
        horizontalLayout.setSpacing(true);
        addToForm(horizontalLayout);


        // binding
        bind(fromDate, "fromDate");
        bind(status, "status");
        bind(priority, "priority");
        bind(delivery, "delivery");
        bind(shipmentDate, "shipmentDate");
        bind(addressComboBox, "address");
        bind(commentary, "commentary");
        bind(contract, "contract");



        if (status.getValue() == Order.StatusType.SHIPPED || status.getValue() == Order.StatusType.RETURN) {
            getFieldGroup().setEnabled(false);
            itemsComboBox.setEnabled(false);
            addItemBtn.setEnabled(false);
            itemTable.setEnabled(false);
            uploadFile.setEnabled(false);
            billBtn.setEnabled(false);
            invoiceBtn.setEnabled(false);
            torg12.setEnabled(false);
            getSaveButton().setEnabled(false);
        }
    }


    public FilterTable buildTable() {
        FilterTable table = ItemView.buildItemTable(itemBeanContainer);
        table.addItemClickListener(this);
        table.setHeight("300px");
        table.setWidth("880px");

        table.addGeneratedColumn("genAmount", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, Object itemId, Object columnId) {
                for (OrderItem orderItem : orderItemSet) {
                    if (orderItem.getItem().equals(itemId)) {
                        return orderItem.getAmount();
                    }
                }

                return "error";
            }
        });
        table.addGeneratedColumn("genPrice", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable source, Object itemId, Object columnId) {
                for (OrderItem orderItem : orderItemSet) {
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
                        Iterator<OrderItem> iterator = orderItemSet.iterator();
                        while (iterator.hasNext()) {
                            OrderItem orderItem = iterator.next();
                            if (orderItem.getItem().equals(itemId)) {
                                orderItemJpa.removeItem(orderItem.getId());
                                itemBeanContainer.removeItem(itemId);

                                iterator.remove();

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

        table.setVisibleColumns("factoryCode", "printName", "brand",
                "inPrice", "tradePrice", "genAmount", "genPrice", "genRemove");

        table.setColumnHeaders("Код произв.", "Наименование", "Бренд",
                "Цена", "Оптовая", "Кол-во", "Сумма", "");


        return table;
    }

    @Override
    public void cancelButtonClick() {
        setProcessing(true);
        if (uploadFile.isUploading()) {
            uploadFile.interruptUpload();
        }
    }

    @Override
    public void itemClick(final ItemClickEvent tableEvent) {
        if (tableEvent.getSource() == itemTable && tableEvent.isDoubleClick()) {
            OrderItem selectedOrderItem = null;
            for (OrderItem orderItem : orderItemSet) {
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

                    itemTable.refreshRowCache();
                    recalculatePrice();
                }
            });

            UI.getCurrent().addWindow(amountPopup);
        }

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() == addItemBtn) {
            addItemButtonClick();
        }
    }

    protected void addItemButtonClick() {
        final JPAContainerItem<com.hickory.models.Item> newItem =
                (JPAContainerItem<com.hickory.models.Item>) itemsComboBox.getItem(itemsComboBox.getValue());

        if (newItem == null) {
            Notification.show("Выберите товар");
            return;
        }

        ItemAmountPopup amountPopup = new ItemAmountPopup(1);
        amountPopup.addPopupListener(new ModalPopup.ClickListener() {
            @Override
            public void clicked(ModalPopup.ClickEvent event) {
                ItemAmountPopup.SaveEvent e = (ItemAmountPopup.SaveEvent) event;
                OrderItem orderItem = new OrderItem();
                orderItem.setItem(newItem.getEntity());

                if (!orderItem.setAmount(e.getAmount())) {
                    Notification.show("Ошибка выставления количества", Notification.Type.ERROR_MESSAGE);
                    return;
                }

                orderItem.setOrder((Order) getLocalItem().getEntity());

                orderItemSet.add(orderItem);
                itemTable.getContainerDataSource().addItem(orderItem.getItem());

                recalculatePrice();
            }
        });

        UI.getCurrent().addWindow(amountPopup);
    }

    @Override
    public void savingSequence() {
        if (isFileChosen) {
            setProcessing(true);
            uploadFile.submitUpload();
        } else {
            super.savingSequence();
        }
    }

    @Override
    public void saveButtonClick() {
        if (status.isModified()) {
            if (status.getValue() == Order.StatusType.SHIPPED) {
                for (OrderItem orderItem : orderItemSet) {
                    Item item = orderItem.getItem();
                    item.setAmount(item.getAmount() - orderItem.getAmount());
                }
            }
        }

        getLocalItem().getItemProperty("orderItems").setValue(orderItemSet);
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        try {
            String absoluteDiskPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/WEB-INF/classes/data/");
            return new FileOutputStream(new File(absoluteDiskPath + "/" + filename));
        } catch (FileNotFoundException e) {
            Notification.show("Error upload");
            setProcessing(false);
            return null;
        }

    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        contract.setValue(event.getFilename());
        super.savingSequence();
    }

    @Override
    public void filenameChanged(Upload.ChangeEvent event) {
        isFileChosen = event.getFilename() != null;
    }

    private void recalculatePrice() {
        price = calculateOverallPrice();
        overallPriceLabel.setValue(Utils.round(price, 2).floatValue() + " р.");
        overallPriceLabelTax.setValue(Utils.round(price * 0.18f, 2).floatValue() + " р.");
    }

    public float calculateOverallPrice() {
        Float overallPrice = 0f;
        for (OrderItem orderItem : orderItemSet) {
            overallPrice += orderItem.getItem().getTradePrice().floatValue() * orderItem.getAmount();
        }

        return overallPrice;
    }
}
