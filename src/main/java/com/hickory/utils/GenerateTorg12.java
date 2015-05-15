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

import static com.hickory.utils.numbertowords.Numerals.russian;
import static com.hickory.utils.numbertowords.Numerals.russianRubles;
/**
 * Created with IntelliJ IDEA.
 * User: kmax
 * Date: 13.10.14
 * Time: 18:44
 */

public class GenerateTorg12 {

    private String doc;
    private String filePath;
    private String filePathPDF;
    private String itogo;
    private int orderItemAmountAll;

    public String getFile(EntityItem item, JPAContainer<OrderItem> orderItemJpa, float overalPrice, BigDecimal overalPriceNds) {

        int number = 0;

        doc = new String();

        String content = new String();

        JPAContainer<Company> companyJpa = ContainerBuilder.getContainer(Company.class);
        EntityItem<Company> companyItem = companyJpa.getItem(1);

        JPAContainer<Order> orderJpa = ContainerBuilder.getContainer(Order.class);
        Order order = orderJpa.getItem(Integer.parseInt(item.getItemProperty("number").getValue().toString())).getEntity();
        orderItemJpa.addContainerFilter(new Compare.Equal("order", order));
        orderItemAmountAll = 0;


        for (Object itemId : orderItemJpa.getItemIds()) {
            com.vaadin.data.Item getItemJpa = orderItemJpa.getItem(itemId);

            com.hickory.models.Item orderItem = (com.hickory.models.Item) getItemJpa.getItemProperty("item").getValue();
            Integer orderItemAmount = (Integer) getItemJpa.getItemProperty("amount").getValue();

            number++;
            orderItemAmountAll += orderItemAmount;

            System.out.print(orderItemJpa.toString());
            content +=
                    " <tr>\n" +
                            "  <td style=\"widtd:36px;\">\n" +
                            "    <div id=\"1_id\" class=\"editable_sys\" edtype=\"text\">"+ number +"</div>  \n" +
                            "  </td> \n" +
                            "  <td style=\"widtd:200px;\">\n" +
                            "    <div id=\"2_id\" class=\"editable_sys\" edtype=\"text\">" + orderItem.getPrintName() + "</div>    \n" +
                            "  </td> \n" +
                            "  <td style=\"widtd:55px;\">\n" +
                            "    <div id=\"3_id\" class=\"editable_sys\" edtype=\"text\">" + orderItem.getId() +"</div>       \n" +
                            "  </td>\n" +
                            "  <td style=\"widtd:54px;\">\n" +
                            "    <div id=\"4_id\" class=\"editable_sys\" edtype=\"text\">шт</div>         \n" +
                            "  </td>\n" +
                            "  <td style=\"widtd:54px;\">\n" +
                            "    <div id=\"5_id\" class=\"editable_sys\" edtype=\"text\">796</div>         \n" +
                            "  </td>\n" +
                            "  <td style=\"widtd:42px;\">\n" +
                            "    <div id=\"6_id\" class=\"editable_sys\" edtype=\"text\">шт</div>           \n" +
                            "  </td>  \n" +
                            "  <td style=\"widtd:42px;\">\n" +
                            "    <div id=\"7_id\" class=\"editable_sys\" edtype=\"text\">1</div>           \n" +
                            "  </td>    \n" +
                            "  <td style=\"widtd:55px;\">\n" +
                            "    <div id=\"8_id\" class=\"editable_sys\" edtype=\"text\">1</div>           \n" +
                            "  </td>    \n" +
                            "  <td style=\"widtd:53px;\">\n" +
                            "    <div id=\"9_id\" class=\"editable_sys\" edtype=\"text\"></div>           \n" +
                            "  </td>    \n" +
                            "  <td style=\"widtd:61px;\">\n" +
                            "    <div id=\"10_id\" class=\"editable_sys\" edtype=\"text\">"+orderItemAmount+"</div>           \n" +
                            "  </td>      \n" +
                            "  <td style=\"widtd:67px;\">\n" +
                            "    <div id=\"11_id\" class=\"editable_sys\" edtype=\"text\">"+ orderItem.getTradePrice().setScale(2, RoundingMode.HALF_UP) +"</div>           \n" +
                            "  </td>   \n" +
                            "  <td style=\"widtd:60px;\">\n" +
                            "    <div id=\"12_id\" class=\"editable_sys\" edtype=\"text\">"+ orderItem.getTradePrice().setScale(2, RoundingMode.HALF_UP) +"</div>           \n" +
                            "  </td>    \n" +
                            "  <td style=\"widtd:60px;\">\n" +
                            "    <div id=\"13_id\" class=\"editable_sys\" edtype=\"text\">18%</div>           \n" +
                            "  </td>    \n" +
                            "  <td style=\"widtd:67px;\">\n" +
                            "    <div id=\"14_id\" class=\"editable_sys\" edtype=\"text\">"+ orderItem.getTradePrice().setScale(2, RoundingMode.HALF_UP).divide(new BigDecimal(100)).multiply(new BigDecimal(18)).setScale(2, RoundingMode.HALF_UP) +"</div>           \n" +
                            "  </td>    \n" +
                            "  <td style=\"widtd:67px;\">\n" +
                            "    <div id=\"15_id\" class=\"editable_sys\" edtype=\"text\">"+ orderItem.getTradePrice().setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(1.18f)).setScale(2, RoundingMode.HALF_UP) +"</div>           \n" +
                            "  </td>                   \n" +
                            "  \n" +
                            " </tr> ";


        }

        itogo = "<tr>\n" +
                "  <td style=\"widtd:36px; border: none\">\n" +
                "    \n" +
                "  </td> \n" +
                "  <td style=\"widtd:200px; border: none\">\n" +
                "    \n" +
                "  </td> \n" +
                "  <td style=\"widtd:55px; border: none\">\n" +
                "  \n" +
                "  </td>\n" +
                "  <td style=\"widtd:54px; border: none\">\n" +
                "  \n" +
                "  </td>\n" +
                "  <td style=\"widtd:54px; border: none\">\n" +
                "  \n" +
                "  </td>\n" +
                "  <td style=\"widtd:42px; border: none\">\n" +
                "           \n" +
                "  </td>  \n" +
                "  <td style=\"widtd:42px; border: none\">\n" +
                "    <div id=\"7_id\" class=\"editable_sys\" edtype=\"text\">Итого</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:55px;\">\n" +
                "    <div id=\"8_id\" class=\"editable_sys\" edtype=\"text\">"+number+"</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:53px;\">\n" +
                "    <div id=\"9_id\" class=\"editable_sys\" edtype=\"text\"> </div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:61px;\">\n" +
                "    <div id=\"10_id\" class=\"editable_sys\" edtype=\"text\">"+orderItemAmountAll+"</div>           \n" +
                "  </td>      \n" +
                "  <td style=\"widtd:67px;\">\n" +
                "    <div id=\"11_id\" class=\"editable_sys\" edtype=\"text\"> X</div>           \n" +
                "  </td>   \n" +
                "  <td style=\"widtd:60px;\">\n" +
                "    <div id=\"12_id\" class=\"editable_sys\" edtype=\"text\">"+new BigDecimal(overalPrice).subtract(overalPriceNds).setScale(2, RoundingMode.HALF_UP)+"</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:60px;\">\n" +
                "    <div id=\"13_id\" class=\"editable_sys\" edtype=\"text\"> X </div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:67px;\">\n" +
                "    <div id=\"14_id\" class=\"editable_sys\" edtype=\"text\"> "+overalPriceNds.setScale(2, RoundingMode.HALF_UP)+"</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:67px;\">\n" +
                "    <div id=\"15_id\" class=\"editable_sys\" edtype=\"text\"> "+new BigDecimal(overalPrice).setScale(2, RoundingMode.HALF_UP)+"</div>           \n" +
                "  </td>                   \n" +
                "  \n" +
                " </tr> " +
                "<tr>\n" +
                "  <td style=\"widtd:36px; border: none\">\n" +
                "    \n" +
                "  </td> \n" +
                "  <td style=\"widtd:200px; border: none\">\n" +
                "    \n" +
                "  </td> \n" +
                "  <td style=\"widtd:55px; border: none\">\n" +
                "  \n" +
                "  </td>\n" +
                "  <td style=\"widtd:54px; border: none\">\n" +
                "  \n" +
                "  </td>\n" +
                "\n" +
                "  \n" +
                "  <td style=\" border: none; text-align: right\" colspan=\"3\">\n" +
                "    <div id=\"7_id\" class=\"editable_sys\" style=\"padding-right: 20px\" edtype=\"text\">Всего по накладной</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:55px;\">\n" +
                "    <div id=\"8_id\" class=\"editable_sys\" edtype=\"text\">"+number+"</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:53px;\">\n" +
                "    <div id=\"9_id\" class=\"editable_sys\" edtype=\"text\"> </div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:61px;\">\n" +
                "    <div id=\"10_id\" class=\"editable_sys\" edtype=\"text\">"+orderItemAmountAll+"</div>           \n" +
                "  </td>      \n" +
                "  <td style=\"widtd:67px;\">\n" +
                "    <div id=\"11_id\" class=\"editable_sys\" edtype=\"text\">X </div>           \n" +
                "  </td>   \n" +
                "  <td style=\"widtd:60px;\">\n" +
                "    <div id=\"12_id\" class=\"editable_sys\" edtype=\"text\">"+new BigDecimal(overalPrice).subtract(overalPriceNds).setScale(2, RoundingMode.HALF_UP)+"</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:60px;\">\n" +
                "    <div id=\"13_id\" class=\"editable_sys\" edtype=\"text\"> X</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:67px;\">\n" +
                "    <div id=\"14_id\" class=\"editable_sys\" edtype=\"text\"> "+overalPriceNds.setScale(2, RoundingMode.HALF_UP)+"</div>           \n" +
                "  </td>    \n" +
                "  <td style=\"widtd:67px;\">\n" +
                "    <div id=\"15_id\" class=\"editable_sys\" edtype=\"text\"> "+new BigDecimal(overalPrice).setScale(2, RoundingMode.HALF_UP)+" </div>           \n" +
                "  </td>                   \n" +
                "  \n" +
                " </tr>";

        String head =
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                        "<html>\n" +
                        "<STYLE>\n" +
                        "body\n" +
                        "{\n" +
                        "    font-family:\"Arial\", sans-serif;\n" +
                        "    font-size: 10pt;\n" +
                        "}\n" +
                        "\n" +
                        "p\n" +
                        "{\n" +
                        "    line-height: 150%;\n" +
                        "}\n" +
                        "table.footer\n" +
                        "{\n" +
                        "    font-size: 10pt;\n" +
                        "    margin-top: 15px;\n" +
                        "    line-height: 150%;\n" +
                        "}\n" +
                        "table#items\n" +
                        "{\n" +
                        "    border-collapse: collapse;\n" +
                        "}\n" +
                        "table#items td\n" +
                        "{\n" +
                        "    font-size: 10pt;\n" +
                        "    border: 1px solid #000000;\n" +
                        "}\n" +
                        "\n" +
                        "</STYLE>\n" +
                        "<head>\n" +
                        "<title>Бланк \"Расходная накладная ТОРГ-12\"</title>\n" +
                        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                        "<meta name=\"description\" content=\"Бланк расходной накладной ТОРГ-12\">\n" +
                        "<meta name=\"keywords\" content=\"расходная накладная ТОРГ-12, ТОРГ-12, с НДС, без НДС\">\n" +
                        "<!--[if lt IE 7]>\n" +
                        " <style type=\"text/css\">\n" +
                        " .dock img { behavior: url(/css/iepngfix.htc) }\n" +
                        " </style>\n" +
                        "<![endif]-->\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/css/style.css\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/css/blanks.css\">\n" +
                        "</head>\n" +
                        "<body><div class=\"blank_name\">\n" +
                        "\n" +
                        "\n" +
                        "<table border=\"0\" style=\"width:100%;border:0px;\">\n" +
                        "<tr>\n" +
                        "<td width=\"770\" style=\"z-index:100;\">\n" +
                        "<br />\n" +
                        "<div id=\"gruzootpavitel\" class=\"editable\" edtype=\"textarea\" style=\"width:750px;\">\n" +
                        ""+companyItem.getItemProperty("shortcutLegalName").getValue() + ", ИНН "+ companyItem.getItemProperty("inn").getValue() +", "+ companyItem.getItemProperty("address").getValue() +", р/с" +companyItem.getItemProperty("bill").getValue()+ ", в банке "+companyItem.getItemProperty("bank").getValue()+ ", БИК "+ companyItem.getItemProperty("bik").getValue() + ", к/с " + companyItem.getItemProperty("kbill").getValue()
                        +"</div>\n" +
                        "<div id=\"go_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:6.5pt;border-top:1px solid;text-align:center;width:750px;height:15px;\">\n" +
                        "грузоотправитель, адрес, номер телефона, банковские реквизиты\n" +
                        "</div>\n" +
                        "<div id=\"po_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:6.5pt;border-top:1px solid;text-align:center;width:750px;\">\n" +
                        "структурное подразделение\n" +
                        "</div><br /><br />\n" +
                        "    <table width=\"100%\">\n" +
                        "        <tr>\n" +
                        "            <td width=\"125\" align=\"right\"><div id=\"gruzopoluchatel_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Грузополучатель</div></td>\n" +
                        "            <td style=\"border-bottom:1px solid;\"><div id=\"gruzopoluchatel\" class=\"editable\" edtype=\"textarea\" style=\"width:100%;font-size:8pt;\">"+item.getItemProperty("client").getValue() + ", ИНН "+ ((Client)item.getItemProperty("client").getValue()).getInn() +", "+ ((Client)item.getItemProperty("client").getValue()).getAddress() +", р/с" +((Client)item.getItemProperty("client").getValue()).getNumberBill()+ ", в банке "+((Client)item.getItemProperty("client").getValue()).getBank()+ ", БИК "+ ((Client)item.getItemProperty("client").getValue()).getBik()+ ", к/с " + ((Client)item.getItemProperty("client").getValue()).getKorrBill()+"</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>                                     \n" +
                        "            <td width=\"125\" align=\"right\"><div id=\"postavshik_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Поставщик</div></td>\n" +
                        "            <td style=\"border-bottom:1px solid;\"><div id=\"postavshik\" class=\"editable\" edtype=\"textarea\" style=\"width:100%;font-size:8pt;\">"+companyItem.getItemProperty("shortcutLegalName").getValue() + ", ИНН "+ companyItem.getItemProperty("inn").getValue() +", "+ companyItem.getItemProperty("address").getValue() +", р/с" +companyItem.getItemProperty("bill").getValue()+ ", в банке "+companyItem.getItemProperty("bank").getValue()+ ", БИК "+ companyItem.getItemProperty("bik").getValue() + ", к/с " + companyItem.getItemProperty("kbill").getValue()
                        +"</div></td>\n" +
                        "        </tr>       \n" +
                        "        <tr>\n" +
                        "            <td width=\"125\" align=\"right\"><div id=\"platelshik_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Плательщик</div></td>\n" +
                        "            <td style=\"border-bottom:1px solid;\"><div id=\"platelshik\" class=\"editable\" edtype=\"textarea\" style=\"width:100%;font-size:8pt;\">"+item.getItemProperty("client").getValue() + ", ИНН "+ ((Client)item.getItemProperty("client").getValue()).getInn() +", "+ ((Client)item.getItemProperty("client").getValue()).getAddress() +", р/с" +((Client)item.getItemProperty("client").getValue()).getNumberBill()+ ", в банке "+((Client)item.getItemProperty("client").getValue()).getBank()+ ", БИК "+ ((Client)item.getItemProperty("client").getValue()).getBik()+ ", к/с " + ((Client)item.getItemProperty("client").getValue()).getKorrBill()+"</div></td>\n" +
                        "        </tr>         \n" +
                        "        <tr>\n" +
                        "            <td width=\"125\" align=\"right\"><div id=\"osnovanie_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Основание</div></td>\n" +
                        "            <td style=\"border-bottom:1px solid;\"><div id=\"osnovanie\" class=\"editable\" edtype=\"textarea\" style=\"width:100%;font-size:8pt;\">Заказ клиента №"+ (Integer) (Integer.parseInt(companyItem.getItemProperty("lastBillNumber").toString()) +
                        Integer.parseInt(item.getItemProperty("number").getValue().toString())) +" от "+new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime())+" </div></td>\n" +
                        "        </tr>        \n" +
                        "        <tr>\n" +
                        "            <td width=\"125\" align=\"right\"></td>\n" +
                        "            <td valign=\"top\"><div id=\"naryad_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:6.5pt;text-align:center;\">договор, заказ-наряд</div></td>\n" +
                        "        </tr>                   \n" +
                        "    </table>\n" +
                        "</td>\n" +
                        "<td align=\"right\" valign=\"top\"><br />\n" +
                        "<div style=\"position:relative;z-index:0; margin-left: -50px;\">\n" +
                        "<div style=\"right:0px;width:310px;z-index:2;\">\n" +
                        "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"310\" style=\"background-color: transparent;\">\n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\"></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;\"><div id=\"kod_okud_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:6.5pt;text-align:center;\">Код</div></td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;\"><div id=\"okud_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Форма по ОКУД</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-left:2px solid;border-right:2px solid;\"><div id=\"okud\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">0330212</div></td>\n" +
                        "    </tr>  \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;\"><div id=\"okpo_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">по ОКПО</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"okpo\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">&nbsp;</div></td>\n" +
                        "    </tr>   \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;\">&nbsp;</td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\">&nbsp;</td>\n" +
                        "    </tr> \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;\"><div id=\"okpd_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Вид деятельности по ОКДП</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"okpd\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">&nbsp;</div></td>\n" +
                        "    </tr>   \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;height:25px;vertical-align:middle;\"><div id=\"okpo2_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">по ОКПО</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"okpo2\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">"+((Client)item.getItemProperty("client").getValue()).getOkpo()+"</div></td>\n" +
                        "    </tr>    \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;height:25px;vertical-align:middle;\"><div id=\"okpo3_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">по ОКПО</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"okpo3\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">&nbsp;</div></td>\n" +
                        "    </tr>                      \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;height:25px;vertical-align:middle;\"><div id=\"okpo4_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">по ОКПО</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"okpo4\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">"+((Client)item.getItemProperty("client").getValue()).getOkpo()+"</div></td>\n" +
                        "    </tr>     \n" +
                        "\n" +
                        "    <tr>\n" +
                        "        <td></td>\n" +
                        "        <td width=\"70\" align=\"right\" style=\"padding-right:5px;border:1px solid;border-right:0px;\"><div id=\"nomer1_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">номер</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"nomer1\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">"+ (Integer) (Integer.parseInt(companyItem.getItemProperty("lastBillNumber").toString()) +
                        Integer.parseInt(item.getItemProperty("number").getValue().toString())) +"</div></td>\n" +
                        "    </tr>     \n" +
                        "    <tr>\n" +
                        "        <td></td>\n" +
                        "        <td width=\"70\" align=\"right\" style=\"padding-right:5px;border:1px solid;border-right:0px;border-top:0px;\"><div id=\"data1_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">дата</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"data1\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">"+new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime())+"</div></td>\n" +
                        "    </tr>     \n" +
                        "    <tr>\n" +
                        "        <td align=\"left\" style=\"padding-right:0px;\"><div id=\"transp_nakl_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Транспортная накладная</div></td>\n" +
                        "        <td width=\"70\" align=\"right\" style=\"padding-right:5px;border:1px solid;border-right:0px;border-top:0px;\"><div id=\"nomer2_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">номер</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"nomer2\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">&nbsp;</div></td>\n" +
                        "    </tr>     \n" +
                        "    <tr>\n" +
                        "        <td></td>\n" +
                        "        <td width=\"70\" align=\"right\" style=\"padding-right:5px;border:1px solid;border-right:0px;border-top:0px;\"><div id=\"data2_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">дата</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;\"><div id=\"data2\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">&nbsp;</div></td>\n" +
                        "    </tr>         \n" +
                        "    \n" +
                        "    \n" +
                        "    <tr>\n" +
                        "        <td colspan=\"2\" align=\"right\" style=\"padding-right:5px;\"><div id=\"vido_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:100%;font-size:10pt;\">Вид операции</div></td>\n" +
                        "        <td width=\"70\" align=\"center\" style=\"border:1px solid;border-top:0px;border-left:2px solid;border-right:2px solid;border-bottom:2px solid;\"><div id=\"vido\" class=\"editable\" edtype=\"text\" style=\"width:100%;font-size:10pt;font-weight:bold;\">&nbsp;</div></td>\n" +
                        "    </tr>                                                  \n" +
                        "</table>\n" +
                        "</div>\n" +
                        "</div>\n" +
                        "</td>\n" +
                        "</tr>\n" +
                        "<tr>\n" +
                        "    <td width=\"770\" align=\"center\">\n" +
                        "        <table width=\"100%\">\n" +
                        "            <tr>\n" +
                        "                <td align=\"right\" width=\"50%\"><div id=\"nakladnaya_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:10pt;font-weight:bold;\">ТОВАРНАЯ НАКЛАДНАЯ</div></td>\n" +
                        "                <td align=\"left\">\n" +
                        "                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                        "                        <tr><td width=\"100\" style=\"border:1px solid;\" align=\"center\"><div id=\"nomerdoc_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:6.5pt;\">Номер документа</div></td><td width=\"100\" style=\"border:1px solid;border-left:0px;\" align=\"center\"><div id=\"datesoz_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:6.5pt;\">Дата составления</div></td></tr>\n" +
                        "                        <tr><td style=\"border:2px solid;\" align=\"center\"><div id=\"nomerdoc\" class=\"editable\" edtype=\"text\" style=\"font-size:10pt;font-weight:bold;\">" + (Integer) (Integer.parseInt(companyItem.getItemProperty("lastBillNumber").toString()) +
                        Integer.parseInt(item.getItemProperty("number").getValue().toString())) +"</div></td><td align=\"center\" style=\"border:2px solid;border-left:0px;\"><div id=\"date_soz\" class=\"editable\" edtype=\"text\" style=\"font-size:10pt;font-weight:bold;\">"+new SimpleDateFormat("dd.MM.yyyy").format(new Date().getTime())+"</div></td></tr>\n" +
                        "                    </table>\n" +
                        "                </td>\n" +
                        "            </tr>\n" +
                        "        </table>\n" +
                        "    </td>\n" +
                        "    <td></td>\n" +
                        "</tr>\n" +
                        "</table>\n" +
                        "\n" +
                        "\n" +
                        "<br /><br />\n" +
                        "\n" +
                        "<table id=\"items\" class=\"invoice_com_items\" border=\"0\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                        "<thead>\n" +
                        "<tr height=\"10\">\n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"36\"><div id=\"nomerpp_name\" class=\"editable_sys\" edtype=\"textarea\">Но-<br />мер<br />по по-<br />рядку</div></td>\n" +
                        "    <td colspan=\"2\" height=\"12\"><div id=\"tovar_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:255px;\">Товар</div></td>\n" +
                        "    <td colspan=\"2\" height=\"12\"><div id=\"ed_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:108px;\">Единица измерения</div></td>   \n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"42\"><div id=\"vidupak_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:42px;\">Вид<br />упа-<br />ковки</div></td>\n" +
                        "    <td colspan=\"2\" height=\"12\"><div id=\"kolvo_name\" class=\"editable_sys\" edtype=\"text\">Количество</div></td>   \n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"53\"><div id=\"mbrutto_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:53px;\">Масса<br />брутто</div></td>\n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"61\"><div id=\"mnetto_name\" class=\"editable_sys\" edtype=\"textarea\">Количе-<br />ство<br />(масса<br />нетто)</div></td>\n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"67\"><div id=\"price_name\" class=\"editable_sys\" edtype=\"textarea\">Цена,<br />руб. коп.</div></td>\n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"60\"><div id=\"summwo_nds_name\" class=\"editable_sys\" edtype=\"textarea\">Сумма без<br />учета НДС,<br />руб. коп.</div></td>\n" +
                        "    <td colspan=\"2\" height=\"12\"><div id=\"nds_name\" class=\"editable_sys\" edtype=\"text\">НДС</div></td>   \n" +
                        "    <td rowspan=\"2\" height=\"12\" width=\"67\"><div id=\"summw_nds_name\" class=\"editable_sys\" edtype=\"textarea\">Сумма с<br />учетом<br />НДС,<br />руб. коп.</div></td>\n" +
                        "    <!-- <td rowspan=\"2\" height=\"12\" width=\"20\"></td> -->\n" +
                        "</tr>\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "<tr>\n" +
                        "\n" +
                        "    <td width=\"200\"><div id=\"naim_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:200px;\">наименование, характеристика, сорт,<br />артикул товара</div></td>\n" +
                        "    <td width=\"55\"><div id=\"kod_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:55px;\">код</div></td>\n" +
                        "    <td width=\"54\"><div id=\"ednaim_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:54px;\">наиме-<br />нование</div></td>\n" +
                        "    <td width=\"54\"><div id=\"okei_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:54px;\">код по<br />ОКЕИ</div></td>\n" +
                        "\n" +
                        "    <td width=\"42\"><div id=\"inplace_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:42px;\">в<br />одном<br />месте</div></td>\n" +
                        "    <td width=\"55\"><div id=\"mest_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:55px;\">мест,<br />штук</div></td>\n" +
                        "\n" +
                        "    <td width=\"60\"><div id=\"stavka_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:60px;\">ставка, %</div></td>   \n" +
                        "    <td width=\"67\"><div id=\"total_summ_name\" class=\"editable_sys\" edtype=\"textarea\" style=\"width:67px;\">сумма,<br />руб. коп.</div></td>   \n" +
                        "\n" +
                        "</tr>\n" +
                        " <tr>\n" +
                        "  <td style=\"widtd:36px;\">\n" +
                        "    <div id=\"1_id\" class=\"editable_sys\" edtype=\"text\">1</div>  \n" +
                        "  </td> \n" +
                        "  <td style=\"widtd:200px;\">\n" +
                        "    <div id=\"2_id\" class=\"editable_sys\" edtype=\"text\">2</div>    \n" +
                        "  </td> \n" +
                        "  <td style=\"widtd:55px;\">\n" +
                        "    <div id=\"3_id\" class=\"editable_sys\" edtype=\"text\">3</div>       \n" +
                        "  </td>\n" +
                        "  <td style=\"widtd:54px;\">\n" +
                        "    <div id=\"4_id\" class=\"editable_sys\" edtype=\"text\">4</div>         \n" +
                        "  </td>\n" +
                        "  <td style=\"widtd:54px;\">\n" +
                        "    <div id=\"5_id\" class=\"editable_sys\" edtype=\"text\">5</div>         \n" +
                        "  </td>\n" +
                        "  <td style=\"widtd:42px;\">\n" +
                        "    <div id=\"6_id\" class=\"editable_sys\" edtype=\"text\">6</div>           \n" +
                        "  </td>  \n" +
                        "  <td style=\"widtd:42px;\">\n" +
                        "    <div id=\"7_id\" class=\"editable_sys\" edtype=\"text\">7</div>           \n" +
                        "  </td>    \n" +
                        "  <td style=\"widtd:55px;\">\n" +
                        "    <div id=\"8_id\" class=\"editable_sys\" edtype=\"text\">8</div>           \n" +
                        "  </td>    \n" +
                        "  <td style=\"widtd:53px;\">\n" +
                        "    <div id=\"9_id\" class=\"editable_sys\" edtype=\"text\">9</div>           \n" +
                        "  </td>    \n" +
                        "  <td style=\"widtd:61px;\">\n" +
                        "    <div id=\"10_id\" class=\"editable_sys\" edtype=\"text\">10</div>           \n" +
                        "  </td>      \n" +
                        "  <td style=\"widtd:67px;\">\n" +
                        "    <div id=\"11_id\" class=\"editable_sys\" edtype=\"text\">11</div>           \n" +
                        "  </td>   \n" +
                        "  <td style=\"widtd:60px;\">\n" +
                        "    <div id=\"12_id\" class=\"editable_sys\" edtype=\"text\">12</div>           \n" +
                        "  </td>    \n" +
                        "  <td style=\"widtd:60px;\">\n" +
                        "    <div id=\"13_id\" class=\"editable_sys\" edtype=\"text\">13</div>           \n" +
                        "  </td>    \n" +
                        "  <td style=\"widtd:67px;\">\n" +
                        "    <div id=\"14_id\" class=\"editable_sys\" edtype=\"text\">14</div>           \n" +
                        "  </td>    \n" +
                        "  <td style=\"widtd:67px;\">\n" +
                        "    <div id=\"15_id\" class=\"editable_sys\" edtype=\"text\">15</div>           \n" +
                        "  </td>                   \n" +
                        "  \n" +
                        " </tr> \n" +
                        "</thead>";
        String footer =
                "<!-- END ADDITIONAL ROW itogo -->\n" +
                        "\n" +
                        "\n" +
                        "</table>\n" +
                        "\n" +
                        "\n" +
                        "<br /><br />\n" +
                        "<table width=\"100%\">\n" +
                        "    <tr>\n" +
                        "        <td align=\"center\">\n" +
                        "        <div style=\"width:700px;text-align:left;font-size:8pt;\">Товарная накладная имеет приложение на  листах<br />и содержит    __"+russian(number)+"_________________________________________________________ порядковых номеров записей\n" +
                        "        <div id=\"propis1_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:87%;font-size:6.5pt;text-align:center;\">прописью</div>\n" +
                        "        </div>\n" +
                        "        </td>\n" +
                        "    </tr>            \n" +
                        "</table>   \n" +
                        "<table width=\"100%\">\n" +
                        "    <tr>\n" +
                        "        <td align=\"right\" valign=\"bottom\" width=\"365\">\n" +
                        "        <div id=\"vsego_mest\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:8pt;display:inline;\">Всего мест</div><div style=\"display:inline;\">___"+russian(orderItemAmountAll)+"______________</div>\n" +
                        "        <div id=\"propis2_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:180px;font-size:6.5pt;text-align:center;\">прописью</div>\n" +
                        "        </div>\n" +
                        "        </td>\n" +
                        "        <td valign=\"top\" style=\"padding-left:10px;\" align=\"right\">        \n" +
                        "        <div id=\"massa_netto\" class=\"editable_sys\" edtype=\"text\" style=\"display:inline;text-align:left;\">Масса груза (нетто)</div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style=\"border:2px solid;width:100px;margin-bottom:2px;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>\n" +
                        "        <div id=\"propis3_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:450px;font-size:6.5pt;text-align:center;border-top:1px solid;\">прописью</div>\n" +
                        "        <div id=\"massa_brutto\" class=\"editable_sys\" edtype=\"text\" style=\"display:inline;\">Масса груза (брутто)</div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span style=\"border:2px solid;width:100px;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>\n" +
                        "        <div id=\"propis4_name\" class=\"editable_sys\" edtype=\"text\" style=\"width:450px;font-size:6.5pt;text-align:center;border-top:1px solid;\">прописью</div>\n" +
                        "        </td>\n" +
                        "    </tr>            \n" +
                        "</table> \n" +
                        "<table width=\"100%\">  \n" +
                        "    <tr>\n" +
                        "        <td style=\"border-right:1px solid;width:50%;\">\n" +
                        "<div id=\"pril_pasport\" class=\"editable_sys\" edtype=\"text\" style=\"display:inline;\">Приложение (паспорта, сертификаты и т.п) на</div><div style=\"display:inline;\">  _________________</div> <div id=\"pril_list\" class=\"editable_sys\" edtype=\"text\" style=\"display:inline;\">листах</div></div>\n" +
                        "        <div id=\"propis5_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:6.5pt;padding-left:270px;\">прописью</div>\n" +
                        "        <br />\n" +
                        "\n" +
                        "<div style=\"font-weight:bold;\">\n" +
                        "  Всего отпущено наименований <span id=\"vsego_otpusheno_naimen\">"+russian(number)+"</span> \n" +
                        "  <br />\n" +
                        "  на сумму <span id=\"vsego_otpusheno_naimen_na_summ\">"+russianRubles(overalPriceNds)+"</span> \n" +
                        "</div><br />\n" +
                        "\n" +
                        "<table width=\"100%\" cellspacing=\"1\">\n" +
                        "    <tr>\n" +
                        "        <td align=\"left\"><div id=\"otpus_raz_name\" class=\"editable_sys\" edtype=\"text\">Отпуск разрешил</div></td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"text-align:center;padding:2px;\"><div id=\"otpus_raz_mile\" class=\"editable\" edtype=\"text\">/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/</div></td>\n" +
                        "   </tr>\n" +
                        "    <tr>\n" +
                        "        <td align=\"left\" style=\"font-size:6.5pt;\">&nbsp;</td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"dolznost1_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">должность</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"podpis1_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">подпись</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"rashifr1_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">расшифровка подписи</div></td>\n" +
                        "   </tr>   \n" +
                        "   \n" +
                        "    <tr>\n" +
                        "        <td align=\"left\" colspan=\"2\"><div id=\"buhgalter_name\" class=\"editable_sys\" edtype=\"text\">Главный (старший бухгалтер)</div></td>        \n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"text-align:center;padding:2px;\"><div id=\"buhgalter_mile\" class=\"editable\" edtype=\"text\">/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/</td>\n" +
                        "   </tr>\n" +
                        "    <tr>\n" +
                        "        <td align=\"left\" colspan=\"2\" style=\"font-size:6.5pt;\">&nbsp;</td>        \n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"podpis2_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">подпись</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"rashifr2_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">расшифровка подписи</div></td>\n" +
                        "   </tr>      \n" +
                        "   \n" +
                        "    <tr>\n" +
                        "        <td align=\"left\"><div id=\"otpus_pro_name\" class=\"editable_sys\" edtype=\"text\">Отпуск груза произвел</div></td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"text-align:center;padding:2px;\"><div id=\"otpus_pro_mile\" class=\"editable\" edtype=\"text\">/&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/</div></td>\n" +
                        "   </tr>\n" +
                        "    <tr>\n" +
                        "        <td align=\"left\" style=\"font-size:6.5pt;\">&nbsp;</td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"dolznost3_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">должность</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"podpis3_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">подпись</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"rashifr3_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">расшифровка подписи</div></td>\n" +
                        "   </tr>      \n" +
                        "</table>       \n" +
                        "<table width=\"100%\">\n" +
                        "<tr>\n" +
                        "    <td width=\"50%\" align=\"center\"><div id=\"mp1_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:8pt;text-align:center;\">М.П.</div></td>\n" +
                        "    <td><div id=\"date1_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:8pt;text-align:center;\">''&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;''&nbsp;______________ 20&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;года</div></td>\n" +
                        "</tr>\n" +
                        "</table>     \n" +
                        "         \n" +
                        "        </td>\n" +
                        "        <td style=\"width:50%;padding-left:30px;vertical-align:top;\"><br />\n" +
                        "<div id=\"po_dover\" class=\"editable_sys\" edtype=\"text\">По доверенности №&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; от</div>\n" +
                        "<div id=\"dover_vid\" class=\"editable_sys\" edtype=\"text\" style=\"display:inline;\">выданной</div>&nbsp;&nbsp;&nbsp;&nbsp;<div style=\"display:inline;width:300px;border-bottom:1px solid;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>        \n" +
                        "<div id=\"kem_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:6.5pt;padding-left:150px;\">кем, кому (организация, должность, фамилия, и.о.)</div>\n" +
                        "<div style=\"font-size:6.5pt;\"><br /><br /></div>\n" +
                        "\n" +
                        "<div style=\"border-bottom:1px solid;width:100%;\">&nbsp;</div>\n" +
                        "<br />\n" +
                        "<table width=\"100%\" cellspacing=\"1\">\n" +
                        "    <tr>\n" +
                        "        <td align=\"left\"><div id=\"gruz_prin_name\" class=\"editable_sys\" edtype=\"text\">Груз принял</div></td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"text-align:center;padding:2px;\"><div id=\"gruz_prin_mile\" class=\"editable_sys\" edtype=\"text\"> </div></td>\n" +
                        "   </tr>\n" +
                        "    <tr>\n" +
                        "        <td align=\"left\" style=\"font-size:6.5pt;\">&nbsp;</td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"dolznost4_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">должность</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"podpis4_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">подпись</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"rashifr4_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">расшифровка подписи</div></td>\n" +
                        "   </tr>   \n" +
                        "   \n" +
                        "    <tr>\n" +
                        "        <td rowspan=\"2\" align=\"left\" valign=\"top\"><div id=\"gruz_pol_name\" class=\"editable_sys\" edtype=\"textarea\">Груз получил<br />грузополучатель</div></td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"padding:2px;\">&nbsp;</td>\n" +
                        "        <td width=\"100\" style=\"text-align:center;padding:2px;\"><div id=\"gruz_pol_mile\" class=\"editable_sys\" edtype=\"text\"> </div></td>\n" +
                        "   </tr>\n" +
                        "    <tr>\n" +
                        "        \n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"dolznost5_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">должность</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"podpis5_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">подпись</div></td>\n" +
                        "        <td style=\"padding:0px;padding-left:3px;padding-right:3px;\"><div id=\"rashifr5_name\" class=\"editable_sys\" edtype=\"text\" style=\"border-top:1px solid;font-size:6.5pt;text-align:center;\">расшифровка подписи</div></td>\n" +
                        "   </tr>      \n" +
                        "</table> \n" +
                        "<table width=\"100%\">\n" +
                        "<tr>\n" +
                        "    <td width=\"50%\" align=\"center\"><div id=\"mp2_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:8pt;text-align:center;\">М.П.</div></td>\n" +
                        "    <td><div id=\"date2_name\" class=\"editable_sys\" edtype=\"text\" style=\"font-size:8pt;text-align:center;\">''&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;''&nbsp;______________ 20&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;года</div></td>\n" +
                        "</tr>\n" +
                        "</table>    \n" +
                        "\n" +
                        "\n" +
                        "</html>\n";

        doc = head + content + itogo + footer;

        String absoluteDiskPath = VaadinServlet.getCurrent().getServletContext().getRealPath("/WEB-INF/classes/data/");
        filePath = absoluteDiskPath + "/torg12_" + System.currentTimeMillis() + ".html";
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

