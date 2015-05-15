package com.hickory.views.forms;

import com.hickory.models.Supplier;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * @author Evgeny Frolov
 */
public class SupplierForm extends AbstractForm implements Upload.Receiver, Upload.SucceededListener, Upload.ChangeListener {

    private Upload contractFile;
    private TextField contract;
    private boolean isFileChosen;


    public SupplierForm(EntityItem item) {
        super(item);
        setWidth("800px");

        setTitle("Форма заполнения Поставщика");


        NativeSelect clientType = new NativeSelect("Тип поставщика");
        clientType.setNullSelectionAllowed(false);
        for (Supplier.ClientType value : Supplier.ClientType.values()) {
            clientType.addItem(value);
            clientType.setItemCaption(value, value.getValue());
        }

        TextField shortcutLegalName = new TextField("Наименование");
        shortcutLegalName.setNullRepresentation("");
        shortcutLegalName.setRequired(true);

        TextField address = new TextField("Адрес");
        address.setNullRepresentation("");

        TextField orgEmail = new TextField("Email организации");
        orgEmail.setNullRepresentation("");

        TextField orgPhoneNumber = new TextField("Телефон организации");
        orgPhoneNumber.setNullRepresentation("");

        NativeSelect organizationType = new NativeSelect("Тип организации");
        organizationType.setNullSelectionAllowed(false);
        for (Supplier.OrganizationType value : Supplier.OrganizationType.values()) {
            organizationType.addItem(value);
            organizationType.setItemCaption(value, value.getValue());
        }

        TextField inn = new TextField("ИНН");
        inn.setNullRepresentation("");

        TextField kpp = new TextField("КПП");
        kpp.setNullRepresentation("");

        TextField okpo = new TextField("ОКПО");
        okpo.setNullRepresentation("");

        TextField surname = new TextField("Фамилия");
        surname.setNullRepresentation("");

        TextField name = new TextField("Имя");
        name.setNullRepresentation("");

        TextField patronymic = new TextField("Отчество");
        patronymic.setNullRepresentation("");

        TextField phoneNumber = new TextField("Телефон 1");
        phoneNumber.setNullRepresentation("");

        TextField email = new TextField("email");
        email.setNullRepresentation("");

        TextField mobileNumber = new TextField("Телефон 2");
        mobileNumber.setNullRepresentation("");

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

        contractFile = new Upload("Договор", this);
        contractFile.setButtonCaption(null);
        contractFile.addSucceededListener(this);
        contractFile.addChangeListener(this);
        contract = new TextField("Договор");


        bind(contract, "contract");

        addAndBind(clientType, "clientType");
        addAndBind(shortcutLegalName, "shortcutLegalName");
        addAndBind(address, "address");
        addAndBind(orgEmail, "orgEmail");
        addAndBind(orgPhoneNumber, "orgPhoneNumber");
        addAndBind(organizationType, "organizationType");
        addAndBind(inn, "inn");
        addAndBind(kpp, "kpp");
        addAndBind(okpo, "okpo");
        addAndBind(surname, "surname");
        addAndBind(name, "name");
        addAndBind(patronymic, "patronymic");
        addAndBind(phoneNumber, "phoneNumber");
        addAndBind(email, "email");
        addAndBind(mobileNumber, "mobileNumber");
        addAndBind(numberBill, "numberBill");
        addAndBind(currency, "currency");
        addAndBind(bik, "bik");
        addAndBind(bank, "bank");
        addAndBind(korrBill, "korrBill");

        addToForm(contractFile);
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
    public void savingSequence() {
        if (isFileChosen) {
            setProcessing(true);
            contractFile.submitUpload();
        } else {
            super.savingSequence();
        }
    }

    @Override
    public void cancelButtonClick() {
        setProcessing(true);
        if (contractFile.isUploading()) {
            contractFile.interruptUpload();
        }
        fireCancelClickEvent();
    }

    @Override
    public void filenameChanged(Upload.ChangeEvent event) {
        isFileChosen = event.getFilename() != null;
    }
}
