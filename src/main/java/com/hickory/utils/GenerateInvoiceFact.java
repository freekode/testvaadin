package com.hickory.utils;

import com.hickory.models.Client;
import com.hickory.models.Company;
import com.hickory.models.Order;
import com.hickory.models.OrderItem;
import com.hickory.services.ContainerBuilder;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.VaadinServlet;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: kmax
 * Date: 08.10.14
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
public class GenerateInvoiceFact {

    private String doc;
    private String filePath;
    private String filePathPDF;

    public String getFile(EntityItem item, JPAContainer<OrderItem> orderItemJpa, float overalPrice, BigDecimal overalPriceNds) {

        int number = 0;

        doc = new String();

        String content = new String();

        JPAContainer<Company> companyJpa = ContainerBuilder.getContainer(Company.class);
        EntityItem<Company> companyItem = companyJpa.getItem(1);

        JPAContainer<Order> orderJpa = ContainerBuilder.getContainer(Order.class);
        Order order = orderJpa.getItem(Integer.parseInt(item.getItemProperty("number").getValue().toString())).getEntity();
        orderItemJpa.addContainerFilter(new Compare.Equal("order", order));


        for (Object itemId : orderItemJpa.getItemIds()) {
            com.vaadin.data.Item getItemJpa = orderItemJpa.getItem(itemId);

            com.hickory.models.Item orderItem = (com.hickory.models.Item) getItemJpa.getItemProperty("item").getValue();
            Integer orderItemAmount = (Integer) getItemJpa.getItemProperty("amount").getValue();

            number++;

            System.out.print(orderItemJpa.toString());

            content +=
                    "                    <tr valign=\"top\">\n"+
                    "                        <td >\n"+
                    "                            "+orderItem.getPrintName()+"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            796\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            шт\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+ orderItemAmount +"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+orderItem.getTradePrice().setScale(2, RoundingMode.HALF_UP)+"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+ orderItem.getTradePrice().multiply(BigDecimal.valueOf(orderItemAmount)).setScale(2, RoundingMode.HALF_UP).subtract(orderItem.getTradePrice().multiply(BigDecimal.valueOf(orderItemAmount)).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(0.18f))).setScale(2, RoundingMode.HALF_UP) +"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            без акциза\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            18%\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+orderItem.getTradePrice().multiply(BigDecimal.valueOf(orderItemAmount)).multiply(new BigDecimal(0.18)).setScale(2, RoundingMode.HALF_UP)+"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+ orderItem.getTradePrice().multiply(BigDecimal.valueOf(orderItemAmount)).setScale(2, RoundingMode.HALF_UP) +"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            &nbsp;\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+ orderItem.getCountry() +"\n"+
                    "                        </td>\n"+
                    "                        <td >\n"+
                    "                            "+orderItem.getGtd() +"\n"+
                    "                        </td>\n"+
                    "                    </tr>\n";

        }

        String head =
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2//EN\">\n"+
                        "<HTML>\n"+
                        "<STYLE>\n"+
                        "body\n"+
                        "{\n"+
                        "    font-family:\"Arial\", sans-serif;\n"+
                        "    font-size: 10pt;\n"+
                        "}\n"+
                        "\n"+
                        "p\n"+
                        "{\n"+
                        "    line-height: 150%;\n"+
                        "}\n"+
                        "table.footer\n"+
                        "{\n"+
                        "    font-size: 10pt;\n"+
                        "    margin-top: 15px;\n"+
                        "    line-height: 150%;\n"+
                        "}\n"+
                        "table.order\n"+
                        "{\n"+
                        "    border-collapse: collapse;\n"+
                        "}\n"+
                        "table.order td\n"+
                        "{\n"+
                        "    font-size: 10pt;\n"+
                        "    border: 1px solid #000000;\n"+
                        "}\n"+
                        "\n"+
                        "</STYLE>\n"+
                        "\n"+
                        "    <head>\n"+
                        "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"+
                        "        <title langs=\"ru\">Счет</title>\n"+
                        "    </head>\n"+
                        "    <body bgcolor=\"white\" lang=\"RU\">\n"+
                        "\n"+
                        "\n"+
                        "        <div class=\"Section1\">\n"+
                        "\n"+
                        "            <h1>Счет-фактура № " + (Integer) (Integer.parseInt(companyItem.getItemProperty("lastActNumber").toString()) +
                        Integer.parseInt(item.getItemProperty("number").getValue().toString())) + " от " +
                        ""+new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime())+"</h1>\n"+
                        "            <p>\n"+
                        "                Продавец: "+companyItem.getItemProperty("shortcutLegalName").getValue()+"<br>\n"+
                        "                Адрес: "+companyItem.getItemProperty("address").getValue()+"<br>\n"+
                        "                ИНН: "+companyItem.getItemProperty("inn").getValue()+"<br>\n"+
                        "                Грузотправитель и его адрес: "+companyItem.getItemProperty("shortcutLegalName").getValue()+" "+companyItem.getItemProperty("address").getValue()+"<br>\n"+
                        "                Грузополучатель и его адрес: "+item.getItemProperty("client").getValue()+" "+((Client)item.getItemProperty("client").getValue()).getAddress()+"<br>\n"+
                        "                К платежно-расчетному документу №" + (Integer) (Integer.parseInt(companyItem.getItemProperty("lastActNumber").toString()) +
                        Integer.parseInt(item.getItemProperty("number").getValue().toString())) + " от "+new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime())+"\n"+
                        "                Покупатель: "+item.getItemProperty("client").getValue()+"<br>\n"+
                        "                Адрес: "+((Client)item.getItemProperty("client").getValue()).getAddress()+"<br>\n"+
                        "                Валюта: наименование, код Российский рубль, 643<br>\n"+
                        "            </p>\n"+
                        "\n"+
                        "            <table border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"100%\" class=\"order\">\n"+
                        "                <TR>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Наименование товара (описание выполненных работ, оказанных услуг), имущественного права</TD>\n"+
                        "                    <TD COLSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Единицы измерения</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Количество объем</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Цена (тариф) за единицу измерения</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Стоимость товаров (работ, услуг) имущественных прав, без налога — всего</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>В том числе сумма акциза</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Налоговая ставка</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Сумма налога предъявляемая полкупателю</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Стоимость товаров (работ, услуг) имущественных прав с налогом — всего</TD>\n"+
                        "                    <TD COLSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Страна происхождения товара</TD>\n"+
                        "                    <TD ROWSPAN=2 ALIGN=\"CENTER\" VALIGN=MIDDLE>Номер таможенной декларации</TD>\n"+
                        "                </TR>\n"+
                        "                <TR>\n"+
                        "                        <TD ALIGN=\"LEFT\">Код</TD>\n"+
                        "                        <TD ALIGN=\"LEFT\">Условное обозначение (национальное)</TD>\n"+
                        "                        <TD ALIGN=\"LEFT\">Цифровой код</TD>\n"+
                        "                        <TD ALIGN=\"LEFT\">Краткое наименование</TD>\n"+
                        "                        </TR>\n"+
                        "                <tr>\n"+
                        "                    <td>1</td>\n"+
                        "                    <td>2</td>\n"+
                        "                    <td>2а</td>\n"+
                        "                    <td>3</td>\n"+
                        "                    <td>4</td>\n"+
                        "                    <td>5</td>\n"+
                        "                    <td>6</td>\n"+
                        "                    <td>7</td>\n"+
                        "                    <td>8</td>\n"+
                        "                    <td>9</td>\n"+
                        "                    <td>10</td>\n"+
                        "                    <td>10а</td>\n"+
                        "                    <td>11</td>\n"+
                        "                </tr>\n";
        String footer =
                "                <tr valign=\"top\">\n"+
                        "                        <td colspan=\"5\">\n"+
                        "                            <h3>Всего к оплате:</h3>\n"+
                        "                        </td>\n"+
                        "\n"+
                        "                        <td >\n"+
                        "                            "+BigDecimal.valueOf(overalPrice).subtract(overalPriceNds).setScale(2, RoundingMode.HALF_UP)+"\n"+
                        "                        </td>\n"+
                        "                        <td colspan=\"2\">\n"+
                        "                            X\n"+
                        "                        </td>\n"+
                        "                        <td >\n"+
                        "                            "+overalPriceNds+"\n"+
                        "                        </td>\n"+
                        "                        <td >\n"+
                        "                            "+overalPrice+"\n"+
                        "                        </td>\n"+
                        "                        <td colspan=\"3\">\n"+
                        "                            &nbsp;\n"+
                        "                        </td>\n"+
                        "                    </tr>\n"+
                "            </table>\n"+
                        "            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\" class=\"footer\">\n"+
                        "                <tr>\n"+
                        "                    <td width=\"20%\">\n"+
                        "                        Руководитель организации:\n"+
                        "                    </td>\n"+
                        "                    <td width=\"80%\">\n"+
                        "                        _______________________ / ______________________________ /\n"+
                        "                    </td>\n"+
                        "                </tr>\n"+
                        "                <tr>\n"+
                        "                    <td width=\"20%\">\n"+
                        "                        Гл. бухгалтер:\n"+
                        "                    </td>\n"+
                        "                    <td width=\"80%\">\n"+
                        "                        _______________________ / ______________________________ /\n"+
                        "                    </td>\n"+
                        "                </tr>\n"+
                        "            </table>\n"+
                        "        </div>\n"+
                        "    </body>\n"+
                        "</html>";

        doc = head + content + footer;

        String absoluteDiskPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/WEB-INF/classes/data/");
        filePath = absoluteDiskPath + "/invoice_fact_" + System.currentTimeMillis() + ".html";
        File file = new File(filePath);

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"));
            out.write(doc);
            out.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;
    }
}

