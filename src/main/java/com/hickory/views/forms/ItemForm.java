package com.hickory.views.forms;

import com.hickory.models.*;
import com.hickory.services.Utils;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Evgeny Frolov
 */
public class ItemForm extends AbstractForm {
    private ComboBox cellComboBox;
    private Button addCellBtn;
    private Set<OrderItem> itemOrdersSet;
    private Set<CellItem> cellItemsSet;
    private BeanItemContainer<StoreRowCell> cellContainer;
    private JPAContainer<CellItem> cellItemJpa;


    public ItemForm(EntityItem item, JPAContainer<Category> categoryJpa,
                    JPAContainer<StoreRowCell> cellJpa, JPAContainer<CellItem> cellItemJpa) {
        super(item);

        this.cellItemJpa = cellItemJpa;

        setTitle("Форма заполнения Продукта");

        TextField printName = new TextField("Отображаемое имя");
        printName.setNullRepresentation("");
//        printName.setRequired(true);

        TextField factoryCode = new TextField("Код производителя");
        factoryCode.setNullRepresentation("");

        TextField applicable = new TextField("Применимость");
        applicable.setNullRepresentation("");
        applicable.setWidth("400px");

        TextField originalCode = new TextField("Код оригинала");
        originalCode.setNullRepresentation("");

        TextField inPrice = new TextField("Цена вход");
        inPrice.setNullRepresentation("");
        inPrice.setConverter(BigDecimal.class);

        TextField tradePrice = new TextField("Цена опт");
        tradePrice.setNullRepresentation("");
        tradePrice.setConverter(BigDecimal.class);

        TextField retailPrice = new TextField("Цена роз");
        retailPrice.setNullRepresentation("");
        retailPrice.setConverter(BigDecimal.class);

        TextField barcode = new TextField("Штрихкод");
        barcode.setNullRepresentation("");

        TextField brand = new TextField("Бренд");
        brand.setNullRepresentation("");

        TextField country = new TextField("Страна");
        country.setNullRepresentation("");

        TextField gtd = new TextField("ГТД");
        gtd.setNullRepresentation("");

        TextField amount = new TextField("Количество на складе");
        amount.setNullRepresentation("");
        amount.setConverter(Integer.class);

        TextField reserved = new TextField("Зарезервированно");
        reserved.setNullRepresentation("0");

        ComboBox categoryComboBox = new ComboBox("Категория");
        categoryComboBox.setContainerDataSource(categoryJpa);
        categoryComboBox.setConverter(new SingleSelectConverter<Category>(categoryComboBox));
        categoryComboBox.setItemCaptionPropertyId("title");

        itemOrdersSet = (Set<OrderItem>) getLocalItem().getItemProperty("itemOrders").getValue();
        Table orderTable = buildOrderTable();

        cellItemsSet = (Set<CellItem>) getLocalItem().getItemProperty("itemCells").getValue();
        Set<StoreRowCell> cellSet = new HashSet<>();
        for (CellItem cellItem : cellItemsSet) {
            cellSet.add(cellItem.getCell());
        }
        cellContainer = new BeanItemContainer<>(StoreRowCell.class, cellSet);
        Table cellTable = buildCellTable(cellContainer);

        cellComboBox = new ComboBox(null, cellJpa);
        cellComboBox.setConverter(new SingleSelectConverter<StoreRowCell>(cellComboBox));
        cellComboBox.setItemCaptionPropertyId("barcode");
        cellComboBox.setNullSelectionAllowed(false);

        addCellBtn = new Button("+", this);



        addAndBind(printName, "printName");
        addAndBind(factoryCode, "factoryCode");
        addAndBind(applicable, "applicable");
        addAndBind(originalCode, "originalCode");
        addAndBind(inPrice, "inPrice");
        addAndBind(tradePrice, "tradePrice");
        addAndBind(retailPrice, "retailPrice");
        addAndBind(barcode, "barcode");
        addAndBind(brand, "brand");
        addAndBind(country, "country");
        addAndBind(gtd, "gtd");
        addAndBind(amount, "amount");
        addAndBind(reserved, "reservedAmount");
        addAndBind(categoryComboBox, "category");


        GridLayout grid = new GridLayout(2, 2);
        grid.setSpacing(true);

        HorizontalLayout hl = new HorizontalLayout(cellComboBox, addCellBtn);
        hl.setSpacing(true);
        grid.addComponent(hl, 0, 0);

        grid.addComponent(cellTable, 0, 1);

        grid.addComponent(orderTable, 1, 1);

        addToForm(grid);



        for (Field field : getFieldGroup().getFields()) {
            if (field.equals(reserved)) {
                field.setEnabled(false);
            }
        }
    }


    public Table buildOrderTable() {
        Set<Order> orderSet = new HashSet<>();
        for (OrderItem orderItem : itemOrdersSet) {
            orderSet.add(orderItem.getOrder());
        }
        BeanItemContainer<Order> ordersContainer = new BeanItemContainer<>(Order.class, orderSet);

        Table table = new Table("Заказы", ordersContainer);
        table.setHeight("250px");


        table.addGeneratedColumn("genNumber", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object orderId, Object columnId) {
                return ((Order) orderId).getNumber();
            }
        });

        table.addGeneratedColumn("genStatus", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object orderId, Object columnId) {
                return ((Order) orderId).getStatus().getValue();
            }
        });

        table.addGeneratedColumn("genAmount", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object orderId, Object columnId) {
                for (OrderItem orderItem : itemOrdersSet) {
                    if (orderItem.getOrder().equals(orderId)) {
                        return orderItem.getAmount();
                    }
                }

                return "error";
            }
        });

        table.addGeneratedColumn("genPrice", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object orderId, Object columnId) {
                for (OrderItem orderItem : itemOrdersSet) {
                    if (orderItem.getOrder().equals(orderId)) {
                        return Utils.round(
                                orderItem.getItem().getTradePrice()
                                        .multiply(new BigDecimal(orderItem.getAmount())).floatValue(), 2);
                    }
                }

                return "error";
            }
        });

        table.setVisibleColumns("genNumber", "genStatus", "genAmount", "genPrice");

        table.setColumnHeaders("Номер", "Статус", "Кол-во", "Цена");


        return table;
    }

    public Table buildCellTable(Container container) {
        Table table = new Table("Ячейки", container);
        table.setHeight("250px");
        table.setWidth("210px");


        table.addGeneratedColumn("genRemove", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, final Object cellId, Object columnId) {
                Button del = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        Iterator<CellItem> iterator = cellItemsSet.iterator();
                        while (iterator.hasNext()) {
                            CellItem cellItem = iterator.next();
                            if (cellItem.getCell().equals(cellId)) {
                                cellItemJpa.removeItem(cellItem.getId());
                                cellContainer.removeItem(cellId);

                                iterator.remove();
                                break;
                            }
                        }
                    }
                });

                del.setIcon(FontAwesome.TRASH_O);
                del.setStyleName("borderless");
                return del;
            }
        });



        table.setVisibleColumns("barcode", "genRemove");

        table.setColumnHeaders("Штрих-код", "Удалить");


        return table;
    }

    public void addCell() {
        EntityItem<StoreRowCell> newCell =
                (EntityItem<StoreRowCell>) cellComboBox.getItem(cellComboBox.getValue());

        if (newCell == null) {
            Notification.show("Выберите ячейку");
            return;
        }

        if (cellItemsSet.add(new CellItem(newCell.getEntity(), (Item) getLocalItem().getEntity()))) {
            cellContainer.addBean(newCell.getEntity());
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() == addCellBtn) {
            addCell();
        }
    }

    @Override
    public void saveButtonClick() {
        getLocalItem().getItemProperty("itemCells").setValue(cellItemsSet);
    }

}
