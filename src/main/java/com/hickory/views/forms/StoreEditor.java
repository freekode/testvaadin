package com.hickory.views.forms;

import com.vaadin.data.Item;
import com.vaadin.ui.TextField;


/**
 * @author Evgeny Frolov
 */
public class StoreEditor extends AbstractEditForm {
    public StoreEditor(Item item) {
        super(item);
        setWidth("500px");
        center();
        setResizable(false);
        setCaption("Склад");


        TextField title = new TextField("Название");
        title.setNullRepresentation("");
        title.setRequired(true);


        addAndBind(title, "title");
    }
}
