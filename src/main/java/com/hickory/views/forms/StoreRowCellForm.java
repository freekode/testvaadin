package com.hickory.views.forms;

import com.hickory.models.CellItem;
import com.hickory.models.Item;
import com.hickory.models.StoreRowCell;
import com.hickory.popups.ItemAmountPopup;
import com.hickory.popups.ModalPopup;
import com.hickory.views.ItemView;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import org.tepi.filtertable.FilterTable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Evgeny Frolov
 */
public class StoreRowCellForm extends AbstractForm {
    private Button addItemButton;
    private ComboBox itemsComboBox;
    private JPAContainer itemJpa;
    private BeanItemContainer<Item> itemContainer;


    public StoreRowCellForm(EntityItem item, JPAContainer itemJpa) {
        super(item);

        this.itemJpa = itemJpa;

        setCaption("Ячейка");

        TextField barcode = new TextField("Штрих-код");
        barcode.setNullRepresentation("");
        barcode.setRequired(true);

        itemsComboBox = new ComboBox();
        itemsComboBox.setContainerDataSource(itemJpa);
        itemsComboBox.setConverter(new SingleSelectConverter<Item>(itemsComboBox));
        itemsComboBox.setItemCaptionPropertyId("printName");
        itemsComboBox.setNullSelectionAllowed(false);

        addItemButton = new Button("Добавить товар");
        addItemButton.addClickListener(this);


        Set<Item> itemSet = (Set<Item>) getLocalItem().getItemProperty("items").getValue();
        itemContainer = new BeanItemContainer<>(Item.class, itemSet);
        FilterTable itemTable = buildItemTable();




        VerticalLayout itemPanel = new VerticalLayout();
        HorizontalLayout controlItemPanel = new HorizontalLayout();

        controlItemPanel.addComponent(itemsComboBox);
        controlItemPanel.addComponent(addItemButton);

        itemPanel.addComponent(controlItemPanel);
        itemPanel.addComponent(itemTable);


        addAndBind(barcode, "barcode");
        addToForm(itemPanel);
    }


    private FilterTable buildItemTable() {
        FilterTable table = ItemView.buildItemTable(itemContainer);

        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(CustomTable customTable, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        itemContainer.removeItem(itemId);


//                        Iterator<CellItem> itemIterator = cellItemsSet.iterator();
//                        while (itemIterator.hasNext()) {
//                            CellItem cellItem = itemIterator.next();
//                            if (cellItem.getItem() == itemId) {
//                                itemIterator.remove();
//
//                                if (cellItem.getId() != null) {
//                                    cellItemJpa.removeItem(cellItem.getId());
//                                }
//                            }
//                        }
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
    public void buttonClick(Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() == addItemButton) {
            addItemButtonClick();
        }
    }

    protected void addItemButtonClick() {
        final EntityItem<Item> newItemEntity = itemJpa.getItem(itemsComboBox.getValue());

        if (newItemEntity == null) {
            Notification.show("Please select item");
            return;
        }

        itemContainer.addBean(newItemEntity.getEntity());
    }

    @Override
    public void saveButtonClick() {
        Set<Item> itemSet = new HashSet<>();
        for (Object itemId : itemContainer.getItemIds()) {
            itemSet.add(itemContainer.getItem(itemId).getBean());
        }

        getLocalItem().getItemProperty("items").setValue(itemSet);
    }
}

