package com.hickory.views.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.TextField;


/**
 * @author Evgeny Frolov
 */
public class StoreRowEditor extends AbstractEditForm {
    public StoreRowEditor(Item item) {
        super(item);

        center();
        setResizable(false);
        setCaption("Ряд");


        TextField barcode = new TextField("Штрих-код");
        barcode.setNullRepresentation("");
        barcode.setRequired(true);


        addAndBind(barcode, "barcode");
    }
}
