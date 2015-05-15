package com.hickory.views.forms;

import com.hickory.models.*;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * @author Evgeny Frolov
 */
public class PurchaseForm extends AbstractForm implements Upload.Receiver, Upload.SucceededListener, Upload.ChangeListener {
    private Upload uploadFile0;
    private Upload uploadFile1;
    private Upload uploadExcelFile;
    private TextField file0;
    private TextField file1;
    private boolean isFileChosen0;
    private boolean isFileChosen1;
    private boolean isExcelFileChosen;
    private boolean isFileUploaded0;
    private boolean isFileUploaded1;
    private boolean isExcelFileUploaded;
    private ByteArrayOutputStream csvStream;
    private List<Map<String, String>> csvList;
    private JPAContainer<StoreRowCell> cellJpa;
    private CheckBox tax;


    public PurchaseForm(EntityItem item, JPAContainer supplierJpa, JPAContainer categoryJpa, JPAContainer<StoreRowCell> cellJpa) {
        super(item);

        this.cellJpa = cellJpa;

        setWidth("700px");
        setTitle("Форма заполнения Закупки");

        ComboBox supplier = new ComboBox("Поставщик");
        supplier.setContainerDataSource(supplierJpa);
        supplier.setConverter(new SingleSelectConverter<Supplier>(supplier));
        supplier.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ID);
        supplier.setItemCaptionPropertyId("shortcutLegalName");
        supplier.setRequired(true);
        supplier.setNullSelectionAllowed(false);

        PopupDateField date = new PopupDateField("Дата заказа");
        date.setResolution(Resolution.SECOND);
        date.setConverter(Date.class);
        date.setRequired(true);

        uploadFile0 = new Upload("Счет-фактура", this);
        uploadFile0.setButtonCaption(null);
        uploadFile0.addSucceededListener(this);
        uploadFile0.addChangeListener(this);
        file0 = new TextField();

        uploadFile1 = new Upload("Накладная", this);
        uploadFile1.setButtonCaption(null);
        uploadFile1.addSucceededListener(this);
        uploadFile1.addChangeListener(this);
        file1 = new TextField();


        csvStream = new ByteArrayOutputStream();
        uploadExcelFile = new Upload("CSV файл", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String s, String s2) {
                return csvStream;
            }
        });
        uploadExcelFile.addSucceededListener(this);
        uploadExcelFile.addChangeListener(this);
        uploadExcelFile.setButtonCaption(null);


        ComboBox categoryComboBox = new ComboBox("Категория");
        categoryComboBox.setContainerDataSource(categoryJpa);
        categoryComboBox.setConverter(new SingleSelectConverter<Category>(categoryComboBox));
        categoryComboBox.setItemCaptionPropertyId("title");

        tax = new CheckBox("НДС");


        bind(file0, "file0");
        bind(file1, "file1");

        addAndBind(supplier, "supplier");
        addAndBind(date, "selectDate");
        addToForm(uploadFile0, uploadFile1, uploadExcelFile);
        addAndBind(tax, "tax");
        addAndBind(categoryComboBox, "category");
    }


    @Override
    public void filenameChanged(Upload.ChangeEvent event) {
        if (event.getSource() == uploadFile0) {
            isFileChosen0 = event.getFilename() != null;
        }
        if (event.getSource() == uploadFile1) {
            isFileChosen1 = event.getFilename() != null;
        }
        if (event.getSource() == uploadExcelFile) {
            isExcelFileChosen = event.getFilename() != null;
        }

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
        try {
            if (event.getSource() == uploadFile0) {
                file0.setValue(event.getFilename());
                isFileUploaded0 = true;
            } else if (event.getSource() == uploadFile1) {
                file1.setValue(event.getFilename());
                isFileUploaded1 = true;
            } else if (event.getSource() == uploadExcelFile) {
                isExcelFileUploaded = true;

                csvList = fixPrices(parseCsvFile(
                        new ByteArrayInputStream(csvStream.toByteArray())));
            }

            if ((isFileChosen0 == isFileUploaded0) &&
                    (isFileChosen1 == isFileUploaded1) &&
                    (isExcelFileChosen == isExcelFileUploaded)) {

                super.savingSequence();
            }
        } catch (Exception e) {
            setProcessing(false);
            Notification.show("Ошибка CSV", Notification.Type.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @Override
    public void savingSequence() {
        if (isFileChosen0) {
            setProcessing(true);
            uploadFile0.submitUpload();
        }
        if (isFileChosen1) {
            setProcessing(true);
            uploadFile1.submitUpload();
        }
        if (isExcelFileChosen) {
            setProcessing(true);
            uploadExcelFile.submitUpload();
        }
        if (!isFileChosen0 && !isFileChosen1 && !isExcelFileChosen) {
            super.savingSequence();
        }
    }

    @Override
    public void saveButtonClick() {
        if (csvList == null) {
            return;
        }

        cellJpa.addContainerFilter(new Compare.Equal("barcode", "default"));
        if (!cellJpa.getItemIds().iterator().hasNext()) {
            return;
        }


        Shipment shipment = new Shipment();

        StoreRowCell cell = cellJpa.getItem(cellJpa.getItemIds().iterator().next()).getEntity();

        BigDecimal taxValue = BigDecimal.valueOf(1.18);
        if (!tax.getValue()) {
            taxValue = BigDecimal.valueOf(1);
        }

        Set<Item> items = new HashSet<>();
        for (Map<String, String> map : csvList) {
            Item item = new Item();
            item.setPrintName(map.get("Наименование"));
            item.setBrand(map.get("Производитель"));
            item.setFactoryCode(map.get("Код производителя"));
            item.setCodeother(map.get("Код прочее"));
            item.setApplicable(map.get("Применимость"));
            item.setOriginalCode(map.get("Оригинальный код"));
            item.setInPrice(
                    new BigDecimal(map.get("Цена"))
                            .multiply(taxValue)
                            .setScale(2, RoundingMode.HALF_UP));
            item.setTradePrice(new BigDecimal(map.get("Опт")));
//                    new BigDecimal(map.get("Опт"))
//                            .multiply(taxValue)
//                            .setScale(2, RoundingMode.HALF_UP));
            item.setRetailPrice(new BigDecimal(map.get("Розница")));
//                    new BigDecimal(map.get("Розница"))
//                            .multiply(taxValue)
//                            .setScale(2, RoundingMode.HALF_UP));
            item.setCountry(map.get("Страна"));
            item.setGtd(map.get("ГТД"));
            item.setShipment(shipment);
            item.setAmount(new Integer(map.get("Кол-во")));

            CellItem cellItem = new CellItem(cell, item);
            item.setItemCells(new HashSet<>(Arrays.asList(cellItem)));

            items.add(item);
        }

        shipment.setItems(items);
        shipment.setPurchase((Purchase) getLocalItem().getEntity());

        getLocalItem().getItemProperty("shipment").setValue(shipment);
    }

    @Override
    public void cancelButtonClick() {
        if (uploadFile0.isUploading()) {
            uploadFile0.interruptUpload();
        }
        if (uploadFile1.isUploading()) {
            uploadFile1.interruptUpload();
        }
        if (uploadExcelFile.isUploading()) {
            uploadExcelFile.interruptUpload();
        }
    }

    private List<Map<String, String>> parseCsvFile(InputStream stream) {
        String separator = ";";

        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(stream, "windows-1251"));


            String input = in.readLine();
            String[] headerArr = input.split(separator);

            List<Map<String, String>> csvList = new ArrayList<>();
            while ((input = in.readLine()) != null) {
                String[] contentArr = input.split(separator);

                if (headerArr.length != contentArr.length) {
                    throw new IOException("wrong csv");
                }

                Map<String, String> elementMap = new HashMap<>();
                for (int i = 0; i < headerArr.length; i++) {
                    String head = headerArr[i];
                    String value = contentArr[i];

                    elementMap.put(head, value);
                }

                csvList.add(elementMap);
            }

            return csvList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, String>> fixPrices(List<Map<String, String>> list) {
        String priceColumn = "Цена";
        String priceColumnTrade = "Опт";
        String priceColumnSale = "Розница";

        for (Map<String, String> map : list) {
            String price = map.get(priceColumn);
            String priceTrade = map.get(priceColumnTrade);
            String priceSale = map.get(priceColumnSale);

            String newPrice = price.replaceAll(" ", "").replace(",", ".");
            newPrice = newPrice.equals("") ? "0" : newPrice;

            String newPriceTrade = priceTrade.replaceAll(" ", "").replace(",", ".");
            newPriceTrade = newPriceTrade.equals("") ? "0" : newPriceTrade;

            String newPriceSale = priceSale.replaceAll(" ", "").replace(",", ".");
            newPriceSale = newPriceSale.equals("") ? "0" : newPriceSale;


            map.put(priceColumn, newPrice);
            map.put(priceColumnTrade, newPriceTrade);
            map.put(priceColumnSale, newPriceSale);
        }

        return list;
    }
}
