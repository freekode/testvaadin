package com.hickory.views.forms;

import com.hickory.models.Address;
import com.hickory.models.Client;
import com.hickory.models.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Evgeny Frolov
 */
public class ClientForm extends AbstractForm implements Upload.Receiver, Upload.SucceededListener, Upload.ChangeListener {
    private Upload uploadFile;
    private TextField contract;
    private boolean isFileChosen;
    private Button addAddressBtn;
    private BeanItemContainer<Address> addressBeanContainer;


    public ClientForm(EntityItem item, JPAContainer<User> managerJpa, final JPAContainer<Address> addressJpa) {
        super(item);
        setWidth("800px");

        setCaption("Форма заполнения Клиента");

        Label section = new Label("Данные по клиенту");
        section.addStyleName("h3");
        section.addStyleName("colored");
        addToForm(section);

        TextField login = new TextField("Логин");
        login.setNullRepresentation("");
        login.setRequired(true);

        TextField password = new TextField("Пароль");
        password.setNullRepresentation("");
        password.setRequired(true);

        NativeSelect clientType = new NativeSelect("Тип клиента");
        clientType.setNullSelectionAllowed(false);
        for (Client.ClientType value : Client.ClientType.values()) {
            clientType.addItem(value);
            clientType.setItemCaption(value, value.getValue());
        }

        TextField shortcutLegalName = new TextField("Название орг.");
        shortcutLegalName.setNullRepresentation("");
        shortcutLegalName.setRequired(true);


        TextField orgEmail = new TextField("Email организации");
        orgEmail.setNullRepresentation("");

        TextField address = new TextField("Адрес");
        address.setNullRepresentation("");

        TextField orgPhoneNumber = new TextField("Телефон организации");
        orgPhoneNumber.setNullRepresentation("");


        NativeSelect organizationType = new NativeSelect("Тип организации");

        organizationType.setNullSelectionAllowed(false);
        for (Client.OrganizationType value : Client.OrganizationType.values()) {
            organizationType.addItem(value);
            organizationType.setItemCaption(value, value.getValue());
        }

        TextField inn = new TextField("ИНН");
        inn.setNullRepresentation("");


        TextField kpp = new TextField("КПП");
        kpp.setNullRepresentation("");


        TextField okpo = new TextField("ОКПО");
        okpo.setNullRepresentation("");


        section = new Label("Данные по контактному лицу");
        section.addStyleName("h4");
        section.addStyleName("colored");

        // TODO  контактное лицо раздел написать
        TextField surname = new TextField("Фамилия");
        surname.setNullRepresentation("");

        TextField name = new TextField("Имя");
        name.setNullRepresentation("");

        TextField patronymic = new TextField("Отчество");
        patronymic.setNullRepresentation("");

        TextField phoneNumber = new TextField("Телефон");
        phoneNumber.setNullRepresentation("");

        TextField email = new TextField("Email");
        email.setNullRepresentation("");

        TextField mobileNumber = new TextField("Телефон");
        mobileNumber.setNullRepresentation("");

        ComboBox manager = new ComboBox("Менеджер");
        manager.setContainerDataSource(managerJpa);
        manager.setConverter(new SingleSelectConverter<User>(manager));
        manager.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ITEM);
        managerJpa.addContainerFilter(new Compare.Equal("userType", User.UserType.MANAGER));

        uploadFile = new Upload("Договор", this);
        uploadFile.setButtonCaption(null);
        uploadFile.addSucceededListener(this);
        uploadFile.addChangeListener(this);
        contract = new TextField();

        TextField numberBill = new TextField("Номер счета");
        numberBill.setNullRepresentation("");

        TextField currency = new TextField("Валюта счета");
        currency.setNullRepresentation("");

        TextField bik = new TextField("Бик");
        bik.setNullRepresentation("");

        TextField korrBill = new TextField("Корр. счет");
        korrBill.setNullRepresentation("");

        TextField bank = new TextField("Банк");
        bank.setNullRepresentation("");

        Set<Address> addressSet = (Set<Address>) getLocalItem().getItemProperty("addresses").getValue();
        addressBeanContainer = new BeanItemContainer<>(Address.class, addressSet);
        Table addressTable = new Table(null, addressBeanContainer);
//        addressTable.setWidth("500px");
        addressTable.setHeight("250px");
        addressTable.setVisibleColumns("title", "address");
        addressTable.setColumnHeaders("Название", "Адрес");
        addressTable.setSelectable(true);
        addressTable.setEditable(true);
        addressTable.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        addressTable.addGeneratedColumn("genRemove", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        if (addressBeanContainer.getItem(itemId).getBean().getId() != null) {
                            addressJpa.removeItem(addressBeanContainer.getItem(itemId).getBean().getId());
                        }

                        addressBeanContainer.removeItem(itemId);
                    }
                });
                removeBtn.setIcon(FontAwesome.TRASH_O);
                removeBtn.setStyleName("borderless");
                return removeBtn;
            }
        });

        addressTable.setVisibleColumns("title", "address", "genRemove");
        addressTable.setColumnHeaders("Название", "Адрес", "Удалить");


        addAddressBtn = new Button("+", this);


        bind(contract, "contract");

        addAndBind(login, "login");
        addAndBind(password, "password");
//        addAndBind(clientType, "clientType");
        addAndBind(shortcutLegalName, "shortcutLegalName");
        addAndBind(address, "address");
        addAndBind(orgEmail, "orgEmail");
        addAndBind(orgPhoneNumber, "orgPhoneNumber");
        addAndBind(organizationType, "organizationType");
        addAndBind(inn, "inn");
        addAndBind(kpp, "kpp");
        addAndBind(okpo, "okpo");

        addToForm(section);
        addAndBind(surname, "surname");
        addAndBind(name, "name");
        addAndBind(patronymic, "patronymic");
        addAndBind(phoneNumber, "phoneNumber");
        addAndBind(email, "email");
//        addAndBind(mobileNumber, "mobileNumber");
        addAndBind(manager, "manager");
        addAndBind(numberBill, "numberBill");
        addAndBind(currency, "currency");
        addAndBind(bik, "bik");
        addAndBind(bank, "bank");
        addAndBind(korrBill, "korrBill");

        addToForm(uploadFile, addAddressBtn, addressTable);
    }


    @Override
    public void filenameChanged(Upload.ChangeEvent event) {
        isFileChosen = event.getFilename() != null;
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
    public void buttonClick(Button.ClickEvent event) {
        super.buttonClick(event);

        if (event.getSource() == addAddressBtn) {
            addAddressClick();
        }
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
        Set<Address> addressSet = new HashSet<>();
        for (Object itemId : addressBeanContainer.getItemIds()) {
            Address address = addressBeanContainer.getItem(itemId).getBean();
            addressSet.add(address);
        }

        getLocalItem().getItemProperty("addresses").setValue(addressSet);
    }

    @Override
    public void cancelButtonClick() {
        if (uploadFile.isUploading()) {
            uploadFile.interruptUpload();
        }
    }

    private void addAddressClick() {
        Address newAddress = new Address("", "", ((EntityItem<Client>) getLocalItem()).getEntity());
        addressBeanContainer.addBean(newAddress);
    }
}
