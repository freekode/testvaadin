package com.hickory.views.forms;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;
import org.tepi.filtertable.FilterTable;

import java.util.Set;

public class StoreEditor2 extends AbstractEditForm {

    private Button addItemButton;
    private Button removeItemButton;
    private ComboBox itemsComboBox;
    private Set<com.hickory.models.Item> orderItems;
    private BeanItemContainer<com.hickory.models.Item> orderItemsContainer;
    private FilterTable itemTable;

    public StoreEditor2(Item items) {
        super(items);

        center();
        setWidth("600px");
        setResizable(false);
        setCaption("Добавить товар на склад");

        Table table = new Table();

        table.addContainerProperty("Code", String.class, null);
        table.addContainerProperty("Qty", Float.class, null);

        table.addItem(new Object[]{"123123zsd", 2.0f}, 2);
        table.addItem(new Object[]{"123123zxcxzc", 1.0f}, 2);
        table.addItem(new Object[]{"asdzxc123213", 1.0f}, 4);

        table.setPageLength(table.size());
        add(new ComboBox("Введите код:"), table);
    }
}
