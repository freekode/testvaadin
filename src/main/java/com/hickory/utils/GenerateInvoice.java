package com.hickory.utils;

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

import static com.hickory.utils.numbertowords.Numerals.russianRubles;

/**
 * Created with IntelliJ IDEA.
 * User: kmax
 * Date: 08.10.14
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
public class GenerateInvoice {

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

            System.out.println(getItemJpa.getItemPropertyIds());

            com.hickory.models.Item orderItem = (com.hickory.models.Item) getItemJpa.getItemProperty("item").getValue();
            Integer orderItemAmount = (Integer) getItemJpa.getItemProperty("amount").getValue();

            number++;

            System.out.print(orderItemJpa.toString());

            content +=
                "    <tr>\n" +
                "        <td>" + number + "</td>\n" +
                "        <td>" + orderItem.getFactoryCode() + "</td>\n" +
                "        <td>" + orderItem.getPrintName() + "</td>\n" +
                "        <td>"+ orderItemAmount + "</td>\n" +
                "        <td>шт.</td>\n";
            if (orderItem.getTradePrice() != null) {
                content +=
                "        <td>" + orderItem.getTradePrice().setScale(2, RoundingMode.HALF_UP) + "</td>\n" +
                "        <td>" + orderItem.getTradePrice().multiply(BigDecimal.valueOf(orderItemAmount)) + "</td>\n" +
                "    </tr>\n";
            } else {
                content +=
                "        <td> 0 </td>\n" +
                "        <td> 0 </td>\n" +
                "    </tr>\n";
            }

        }

        String head =
                "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Бланк \"Счет на оплату\"</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "    <style>\n" +
                "        body { width: 210mm; margin-left: auto; margin-right: auto; border: 1px #efefef solid; font-size: 11pt;}\n" +
                "        table.invoice_bank_rekv { border-collapse: collapse; border: 1px solid; }\n" +
                "        table.invoice_bank_rekv > tbody > tr > td, table.invoice_bank_rekv > tr > td { border: 1px solid; }\n" +
                "        table.invoice_items { border: 1px solid; border-collapse: collapse;}\n" +
                "        table.invoice_items td, table.invoice_items th { border: 1px solid;}\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table width=\"100%\">\n" +
                "    <tr>\n" +
                "        <td>&nbsp;</td>\n" +
                "        <td style=\"width: 155mm;\">\n" +
                "            <div style=\"width:155mm; \">Внимание! Оплата данного счета означает согласие с условиями поставки товара. Уведомление об оплате  обязательно, в противном случае не гарантируется наличие товара на складе. Товар отпускается по факту прихода денег на р/с Поставщика, самовывозом, при наличии доверенности и паспорта.</div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td colspan=\"2\">\n" +
                "            <div style=\"text-align:center;  font-weight:bold;\">\n" +
                "                Образец заполнения платежного поручения                                                                                                                                            </div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "\n" +
                "<table width=\"100%\" cellpadding=\"2\" cellspacing=\"2\" class=\"invoice_bank_rekv\">\n" +
                "    <tr>\n" +
                "        <td colspan=\"2\" rowspan=\"2\" style=\"min-height:13mm; width: 105mm;\">\n" +
                "            <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height: 13mm;\">\n" +
                "                <tr>\n" +
                "                    <td valign=\"top\">\n" +
                "                        <div>"+companyItem.getItemProperty("bank").getValue()+"</div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td valign=\"bottom\" style=\"height: 3mm;\">\n" +
                "                        <div style=\"font-size:10pt;\">Банк получателя        </div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "        <td style=\"min-height:7mm;height:auto; width: 25mm;\">\n" +
                "            <div>БИK</div>\n" +
                "        </td>\n" +
                "        <td rowspan=\"2\" style=\"vertical-align: top; width: 60mm;\">\n" +
                "            <div style=\" height: 7mm; line-height: 7mm; vertical-align: middle;\">"+ companyItem.getItemProperty("bik").getValue() +"</div>\n" +
                "            <div>"+ companyItem.getItemProperty("kbill").getValue() +"</div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td style=\"width: 25mm;\">\n" +
                "            <div>Сч. №</div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td style=\"min-height:6mm; height:auto; width: 50mm;\">\n" +
                "            <div>ИНН " + companyItem.getItemProperty("inn").getValue() + "</div>\n" +
                "        </td>\n" +
                "        <td style=\"min-height:6mm; height:auto; width: 55mm;\">\n" +
                "            <div>КПП "+ companyItem.getItemProperty("kpp").getValue() +"</div>\n" +
                "        </td>\n" +
                "        <td rowspan=\"2\" style=\"min-height:19mm; height:auto; vertical-align: top; width: 25mm;\">\n" +
                "            <div>Сч. №</div>\n" +
                "        </td>\n" +
                "        <td rowspan=\"2\" style=\"min-height:19mm; height:auto; vertical-align: top; width: 60mm;\">\n" +
                "            <div>"+ companyItem.getItemProperty("bill").getValue() +"</div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td colspan=\"2\" style=\"min-height:13mm; height:auto;\">\n" +
                "\n" +
                "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height: 13mm; width: 105mm;\">\n" +
                "                <tr>\n" +
                "                    <td valign=\"top\">\n" +
                "                        <div>"+ companyItem.getItemProperty("shortcutLegalName").getValue() +"</div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td valign=\"bottom\" style=\"height: 3mm;\">\n" +
                "                        <div style=\"font-size: 10pt;\">Получатель</div>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "<br/>\n" +
                "\n" +
                "<div style=\"font-weight: bold; font-size: 16pt; padding-left:5px;\">\n" +
                "    Счет № " + (Integer) (Integer.parseInt(companyItem.getItemProperty("lastBillNumber").toString()) +
                        Integer.parseInt(item.getItemProperty("number").getValue().toString())) + " от " +
                        ""+new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime())+"</div>\n" +
                "<br/>\n" +
                "\n" +
                "<div style=\"background-color:#000000; width:100%; font-size:1px; height:2px;\">&nbsp;</div>\n" +
                "\n" +
                "<table width=\"100%\">\n" +
                "    <tr>\n" +
                "        <td style=\"width: 30mm;\">\n" +
                "            <div style=\" padding-left:2px;\">Поставщик:    </div>\n" +
                "        </td>\n" +
                "        <td>\n" +
                "            <div style=\"font-weight:bold;  padding-left:2px;\">\n" +
                "                " + companyItem.getItemProperty("shortcutLegalName").getValue() + "            </div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "        <td style=\"width: 30mm;\">\n" +
                "            <div style=\" padding-left:2px;\">Покупатель:    </div>\n" +
                "        </td>\n" +
                "        <td>\n" +
                "            <div style=\"font-weight:bold;  padding-left:2px;\">\n" +
                "                " + item.getItemProperty("client").getValue() + "           </div>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "\n" +
                "<table class=\"invoice_items\" width=\"100%\" cellpadding=\"2\" cellspacing=\"2\">\n" +
                "    <thead>\n" +
                "    <tr>\n" +
                "        <th style=\"width:13mm;\">№</th>\n" +
                "        <th style=\"width:20mm;\">Код</th>\n" +
                "        <th>Товар</th>\n" +
                "        <th style=\"width:20mm;\">Кол-во</th>\n" +
                "        <th style=\"width:17mm;\">Ед.</th>\n" +
                "        <th style=\"width:27mm;\">Цена</th>\n" +
                "        <th style=\"width:27mm;\">Сумма</th>\n" +
                "    </tr>\n" +
                "    </thead>\n" +
                "    <tbody >\n";
        String footer =
                "    </tbody>\n" +
                "</table>\n" +
                "\n" +
                "<table border=\"0\" width=\"100%\" cellpadding=\"1\" cellspacing=\"1\">\n" +
                "    <tr>\n" +
                "        <td></td>\n" +
                "        <td style=\"width:27mm; font-weight:bold;  text-align:right;\">Итого:</td>\n" +
                "        <td style=\"width:27mm; font-weight:bold;  text-align:right;\">" + new BigDecimal(overalPrice).setScale(2, RoundingMode.HALF_UP) + "</td>\n" +
                "<tr><td colspan=\"2\" style=\"font-weight:bold;  text-align:right;\">В том числе НДС:</td>\n" +
                "        <td style=\"width:27mm; font-weight:bold;  text-align:right;\">" + new BigDecimal(overalPrice).setScale(2, RoundingMode.HALF_UP) + "</td>\n" +
                "</tr>" +

                "    </tr>\n" +
                "</table>\n" +
                "\n" +
                "<br />\n" +
                "<div>\n" +
                "Всего наименований " + number + " на сумму " + overalPrice + " рублей.<br />\n" +
                "</div>\n" +
                "<div>\n" +
                "" + russianRubles(overalPrice) + "<br />\n" +
                "</div>\n" +
                "<br /><br />\n" +
                "<div style=\"background-color:#000000; width:100%; font-size:1px; height:2px;\">&nbsp;</div>\n" +
                "<br/>\n" +
                "\n" +
                "<div>Руководитель ______________________ ("+companyItem.getItemProperty("fioCeo").getValue()+")</div>\n" +
                "<br/>\n" +
                "\n" +
                "<div>Главный бухгалтер ______________________ ("+companyItem.getItemProperty("fioAc").getValue()+")</div>\n" +
                "<br/>\n" +
                "\n" +
                "<div style=\"width: 85mm;text-align:center;\">М.П.</div>\n" +
                "<br/>\n" +
                "\n" +
                "\n" +
                "<div style=\"width:800px;text-align:left;font-size:10pt;\">Счет действителен к оплате в течении трех дней.</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        doc = head + content + footer;

        String absoluteDiskPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/WEB-INF/classes/data/");
        filePath = absoluteDiskPath + "/invoice_" + System.currentTimeMillis() + ".html";
        File file = new File(filePath);

        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "UTF-8"));
            out.write(doc);
            out.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return filePath;
    }
}
