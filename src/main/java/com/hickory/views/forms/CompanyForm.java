package com.hickory.views.forms;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.ui.TextField;


/**
 * @author Evgeny Frolov
 */
public class CompanyForm extends AbstractForm {
    public CompanyForm(EntityItem item, Container managers) {
        super(item);
        setWidth("600px");

        setTitle("Форма заполнения Компании");

        TextField shortcutLegalName = new TextField("Наименование");
        shortcutLegalName.setNullRepresentation("");
        shortcutLegalName.setRequired(true);

        TextField address = new TextField("Адрес");
        address.setNullRepresentation("");

        TextField orgEmail = new TextField("Email организации");
        orgEmail.setNullRepresentation("");

        TextField orgPhoneNumber = new TextField("Телефон организации");
        orgPhoneNumber.setNullRepresentation("");

        TextField inn = new TextField("ИНН");
        inn.setNullRepresentation("");

        TextField ogrn = new TextField("ОГРН");
        ogrn.setNullRepresentation("");

        TextField bill = new TextField("Расчетный счет");
        bill.setNullRepresentation("");

        TextField kbill = new TextField("Кор. счет");
        kbill.setNullRepresentation("");

        TextField bik = new TextField("БИК");
        bik.setNullRepresentation("");

        TextField bank = new TextField("Название банка");
        bank.setNullRepresentation("");

        TextField kpp = new TextField("КПП");
        kpp.setNullRepresentation("");

        TextField okpo = new TextField("ОКПО");
        okpo.setNullRepresentation("");

        TextField lastBillNumber = new TextField("Последний номер Счета");
        lastBillNumber.setNullRepresentation("");

        TextField lastActNumber = new TextField("Последний номер Акта");
        lastActNumber.setNullRepresentation("");

        TextField lastTorg12Number = new TextField("Последний номер Торг 12");
        lastTorg12Number.setNullRepresentation("");

        TextField fioCeo = new TextField("Руководитель (ФИО)");
        fioCeo.setNullRepresentation("");

        TextField fioAc = new TextField("Гл. бухгалтер (ФИО)");
        fioAc.setNullRepresentation("");

        addAndBind(shortcutLegalName, "shortcutLegalName");
        addAndBind(address, "address");
        addAndBind(orgEmail, "orgEmail");
        addAndBind(orgPhoneNumber, "orgPhoneNumber");
        addAndBind(inn, "inn");
        addAndBind(ogrn, "ogrn");
        addAndBind(bill, "bill");
        addAndBind(kbill, "kbill");
        addAndBind(bik, "bik");
        addAndBind(bank, "bank");
        addAndBind(kpp, "kpp");
        addAndBind(okpo, "okpo");
        addAndBind(lastBillNumber, "lastBillNumber");
        addAndBind(lastActNumber, "lastActNumber");
        addAndBind(lastTorg12Number, "lastTorg12Number");
        addAndBind(fioCeo, "fioCeo");
        addAndBind(fioAc, "fioAc");
    }
}
