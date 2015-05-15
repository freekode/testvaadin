package com.hickory.views.forms;

import com.hickory.views.ItemView;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.logging.Logger;


/**
 * @author Evgeny Frolov
 */
public class ReturnEditor extends AbstractEditForm implements Upload.Receiver, Upload.SucceededListener {
    private static final Logger log = Logger.getLogger(ReturnEditor.class.getName());

    private Upload contractFile;
    private TextField contract;
    private Button addItemButton;
    private Button removeItemButton;
    private ComboBox itemsComboBox;
    private Set<com.hickory.models.Item> orderItems;
    private BeanItemContainer<com.hickory.models.Item> orderItemsContainer;
    private FilterTable itemTable;
    private Button btn;
    private Button btnBillFact;

    public ReturnEditor(Item item, Container clients, Container items) {
        super(item);
        setWidth("1200px");
        center();
        setResizable(false);
        setCaption("Форма заполнения Заказа");

//        HorizontalLayout v1 = new HorizontalLayout();
//        v1.setSpacing(true);
//
//        TextField number = new TextField("Номер");
//        number.setNullRepresentation("");
//        number.setRequired(true);
//        v1.addComponent(number);
//
//        PopupDateField fromDate = new PopupDateField("Дата заказа");
//        fromDate.setResolution(Resolution.SECOND);
//        v1.addComponent(fromDate);
//
//        ComboBox client = new ComboBox("Клиент");
//        client.setContainerDataSource(clients);
//        client.setConverter(new SingleSelectConverter<Client>(client));
//        client.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
//        client.setItemCaptionPropertyId("login");
//        v1.addComponent(client);
//
//        contractFile = new Upload("Договор", this);
//        contractFile.setButtonCaption("Загрузить");
//        contractFile.addSucceededListener(this);
//        contract = new TextField("Договор");
//
//        HorizontalLayout v2 = new HorizontalLayout();
//        v2.setSpacing(true);
//
//        NativeSelect status = new NativeSelect("Статус");
//        status.setRequired(true);
//        status.setNullSelectionAllowed(false);
//        for (Order.StatusType value : Order.StatusType.values()) {
//            status.addItem(value);
//            status.setItemCaption(value, value.getValue());
//        }
//
//        NativeSelect priority = new NativeSelect("Приоритет");
//        priority.setRequired(true);
//        priority.setNullSelectionAllowed(false);
//        for (Order.PriorityType value : Order.PriorityType.values()) {
//            priority.addItem(value);
//            priority.setItemCaption(value, value.getValue());
//        }
//        v1.addComponent(priority);
//
//        NativeSelect delivery = new NativeSelect("Доставка");
//        delivery.setRequired(true);
//        delivery.setNullSelectionAllowed(false);
//        for (Order.DeliveryType value : Order.DeliveryType.values()) {
//            delivery.addItem(value);
//            delivery.setItemCaption(value, value.getValue());
//        }
//
//        PopupDateField shipmentDate = new PopupDateField("Дата поставки");
//        shipmentDate.setResolution(Resolution.SECOND);
//
//        TextArea commentary = new TextArea("Комментарий к заказу");
//        commentary.setNullRepresentation("");
//
//        itemsComboBox = new ComboBox();
//        itemsComboBox.setContainerDataSource(items);
//        itemsComboBox.setConverter(new SingleSelectConverter<com.hickory.models.Item>(itemsComboBox));
//        itemsComboBox.setItemCaptionPropertyId("printName");
//        itemsComboBox.setNullSelectionAllowed(false);
//
//        addItemButton = new Button("Добавить товар");
//        addItemButton.addClickListener(this);
//
        removeItemButton = new Button("Возвратить на склад");
        removeItemButton.addClickListener(this);

        orderItems = (Set<com.hickory.models.Item>) getLocalItem().getItemProperty("items").getValue();
        orderItemsContainer = new BeanItemContainer<>(com.hickory.models.Item.class, orderItems);
        itemTable = ItemView.buildItemTable(orderItemsContainer);

        VerticalLayout itemPanel = new VerticalLayout();
        itemPanel.setSpacing(true);
        itemTable.setHeight("300px");
        HorizontalLayout controlItemPanel = new HorizontalLayout();
        controlItemPanel.setSpacing(true);

//        controlItemPanel.addComponent(itemsComboBox);
//        controlItemPanel.addComponent(addItemButton);
        controlItemPanel.addComponent(removeItemButton);

        HorizontalLayout hr = new HorizontalLayout();
        hr.setSpacing(true);

//        btn = new Button("Счет");
//        btn.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent clickEvent) {
//                generateBill();
//            }
//        });
////        hr.addComponent(new Button("Счет фактуры"));
////        hr.addComponent(new Button("ТОРГ 12"));
//
//        btnBillFact = new Button("Счет фактуры");
//        btnBillFact.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent clickEvent) {
//                generateBillFact();
//            }
//        });
//
//        hr.addComponent(btn);
//        hr.addComponent(btnBillFact);
//        hr.addComponent(new Button("ТОРГ 12"));

        itemPanel.addComponent(controlItemPanel);
        itemPanel.addComponent(itemTable);


//        formLayout.addComponent(v1);
//
//        formLayout.addComponent(contractFile);
//        getFieldGroup().bind(contract, "contract");

//        addAndBind(number, "number");
//        addAndBind(fromDate, "fromDate");
//        addAndBind(client, "client");
//        addAndBind(status, "status");
//        addAndBind(priority, "priority");
//        addAndBind(delivery, "delivery");
//        addAndBind(shipmentDate, "shipmentDate");

//        addAndBind(number, "number", true);
//        addAndBind(fromDate, "fromDate", true);
//        addAndBind(client, "client", true);
//        addAndBind(status, "status", true);
//        addAndBind(priority, "priority", true);
//        addAndBind(delivery, "delivery", true);
//        addAndBind(shipmentDate, "shipmentDate", true);
//
//        v2.addComponent(status);
//        v2.addComponent(fromDate);
//        v2.addComponent(delivery);
//        v2.addComponent(shipmentDate);
//        formLayout.addComponent(v2);
//
//        addAndBind(commentary, "commentary");

        add(itemPanel);
        add(hr);
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
        Notification.show("ok");
//        getSaveButton().setEnabled(true);
    }

    @Override
    public void cancelButtonClick() {
//        getSaveButton().setEnabled(true);
        if (contractFile.isUploading()) {
            contractFile.interruptUpload();
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() == addItemButton) {
            addItemButtonClick();
        } else if (event.getButton() == removeItemButton) {
            removeItemButtonClick();
        }
    }

    protected void generateBill() {
        Resource res = new ClassResource("/docs/invoice.html");
        FileDownloader fd = new FileDownloader(res);
        fd.extend(btn);
    }

    protected void generateBillFact() {
        Resource res = new ClassResource("/docs/billFact.html");
        FileDownloader fd = new FileDownloader(res);
        fd.extend(btnBillFact);
    }

    protected void addItemButtonClick() {
        JPAContainerItem<com.hickory.models.Item> newItem =
                (JPAContainerItem<com.hickory.models.Item>) itemsComboBox.getItem(itemsComboBox.getValue());

        if (newItem == null) {
            Notification.show("Please select item");
            return;
        }

        if (orderItems.add(newItem.getEntity())) {
            orderItemsContainer.addBean(newItem.getEntity());
            getLocalItem().getItemProperty("items").setValue(orderItems);
        }
    }

    protected void removeItemButtonClick() {
        BeanItem<com.hickory.models.Item> removingItem = orderItemsContainer.getItem(itemTable.getValue());

        if (removingItem == null) {
            Notification.show("Please select item");
            return;
        }

        if (orderItems.remove(removingItem.getBean())) {
            orderItemsContainer.removeItem(removingItem.getBean());
            getLocalItem().getItemProperty("items").setValue(orderItems);
        }
    }

    @Override
    public void saveButtonClick() {
        getLocalItem().getItemProperty("items").setValue(orderItems);
    }
}
