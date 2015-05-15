package com.hickory.views;

import com.hickory.models.Order;
import com.hickory.services.ContainerBuilder;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.vaadin.addon.JFreeChartWrapper;

import java.awt.*;
import java.util.Calendar;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kmax
 * Date: 15.09.14
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class DashboardView extends CommonPanel implements View, Property.ValueChangeListener {
    private JPAContainer<Order> orderJpa;
    private PopupDateField fromDate;
    private PopupDateField toDate;
    private NativeSelect statusFirst;
    private NativeSelect statusSecond;
    private Panel lineChartPanel;


    public DashboardView() {
        orderJpa = ContainerBuilder.getContainer(Order.class);
        Label allOrders = new Label("Всего заказов: " + orderJpa.size());

        Embedded pieChart = getPieChart(getOverallOrders());


        Calendar now = new GregorianCalendar();

        toDate = new PopupDateField("До");
        toDate.setResolution(Resolution.DAY);
        toDate.setDateFormat("yyyy-MM-dd");
        Calendar toDateValue = new GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        toDate.setValue(toDateValue.getTime());
        toDate.addValueChangeListener(this);


        fromDate = new PopupDateField("От");
        fromDate.setResolution(Resolution.DAY);
        fromDate.setDateFormat("yyyy-MM-dd");
        Calendar fromDateValue = (Calendar) toDateValue.clone();
        fromDateValue.set(Calendar.MONTH, fromDateValue.get(Calendar.MONTH) - 1);
        fromDate.setValue(fromDateValue.getTime());
        fromDate.addValueChangeListener(this);


        statusFirst = new NativeSelect("Статус 1й");
        statusFirst.setNullSelectionAllowed(false);
        for (Order.StatusType value : Order.StatusType.values()) {
            statusFirst.addItem(value);
            statusFirst.setItemCaption(value, value.getValue());
        }
        statusFirst.select(Order.StatusType.NEW);
        statusFirst.addValueChangeListener(this);

        statusSecond = new NativeSelect("Статус 2й");
        statusSecond.setNullSelectionAllowed(false);
        for (Order.StatusType value : Order.StatusType.values()) {
            statusSecond.addItem(value);
            statusSecond.setItemCaption(value, value.getValue());
        }
        statusSecond.select(Order.StatusType.SHIPPED);
        statusSecond.addValueChangeListener(this);


        HorizontalLayout chartControlDateLayout = new HorizontalLayout(fromDate, toDate);
        chartControlDateLayout.setSpacing(true);
        HorizontalLayout chartControlStatusLayout = new HorizontalLayout(statusFirst, statusSecond);
        chartControlStatusLayout.setSpacing(true);
        VerticalLayout chartControlLayout = new VerticalLayout(chartControlDateLayout, chartControlStatusLayout);
        chartControlLayout.setSpacing(true);

        lineChartPanel = new Panel();
        lineChartPanel.addStyleName("borderless");
        lineChartPanel.setSizeUndefined();


        getStandardViewLayout().addContent(allOrders);
        getStandardViewLayout().addContent(pieChart);
        getStandardViewLayout().addContent(new Label("Заказы по статусам:"));
        getStandardViewLayout().addContent(chartControlLayout);
        getStandardViewLayout().addContent(lineChartPanel);
        setContent(getStandardViewLayout());
    }

    public static Embedded getPieChart(Map<String, Double> data) {
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        for (String key : data.keySet()) {
            pieDataset.setValue(key, data.get(key));
        }


        JFreeChart chart = ChartFactory.createPieChart(null, pieDataset, true, true, false);

        return new JFreeChartWrapper(chart);
    }

    public static Embedded getDateLineChart(Map<String, Map<Date, Integer>> lineDataMap) {
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        for (String key : lineDataMap.keySet()) {
            TimeSeries timeSeries = new TimeSeries(key);

            Map<Date, Integer> dataMap = lineDataMap.get(key);
            for (Date date : dataMap.keySet()) {
                timeSeries.add(new Day(date), dataMap.get(date));
            }

            timeSeriesCollection.addSeries(timeSeries);
        }


        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null,
                "Дата",
                "Кол-во",
                timeSeriesCollection,
                true,
                true,
                false
        );

        chart.setAntiAlias(true);
        chart.setBackgroundPaint(Color.decode("#fafafa"));


        XYPlot plot = (XYPlot) chart.getPlot();

        // background color of plot
//        plot.setBackgroundPaint(Color.decode("#fafafa"));
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        plot.getDomainAxis().setStandardTickUnits(DateAxis.createStandardDateTickUnits());
        plot.getDomainAxis().setVerticalTickLabels(true);


        XYSplineRenderer renderer = new XYSplineRenderer();
        for (int i = 0; i < plot.getSeriesCount(); i++) {
            renderer.setSeriesStroke(i, new BasicStroke(2));
        }

        plot.setRenderer(renderer);

        return new JFreeChartWrapper(chart, JFreeChartWrapper.RenderingMode.PNG);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        drawLineChart(getDateLineChart(getFilteredOrders()));
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        drawLineChart(getDateLineChart(getFilteredOrders()));
    }

    private Map<String, Double> getOverallOrders() {
        Map<String, Double> orderMap = new HashMap<>();

        orderJpa.removeAllContainerFilters();
        Double onePiePartValue = Integer.valueOf(orderJpa.size()).doubleValue() / 100;


        Container.Filter statusFilter = null;
        for (Order.StatusType status : Order.StatusType.values()) {
            if (statusFilter != null) {
                orderJpa.removeContainerFilter(statusFilter);
            }

            statusFilter = new Compare.Equal("status", status);
            orderJpa.addContainerFilter(statusFilter);

            Double piePart = orderJpa.size() / onePiePartValue;

            orderMap.put(status.getValue(), piePart);
        }
        orderJpa.removeAllContainerFilters();


        return orderMap;
    }

    public Map<String, Map<Date, Integer>> getFilteredOrders() {
        Map<Date, Integer> firstLine = new HashMap<>();
        Map<Date, Integer> secondLine = new HashMap<>();


        DateTime fromDateValue = new DateTime(fromDate.getValue());
        DateTime toDateValue = new DateTime(toDate.getValue());
        Order.StatusType statusFirstValue = (Order.StatusType) statusFirst.getValue();
        Order.StatusType statusSecondValue = (Order.StatusType) statusSecond.getValue();

        Integer period = Days.daysBetween(fromDateValue, toDateValue).getDays() + 1;


        Container.Filter fromDayFilter = null;
        Container.Filter toDayFilter = null;
        orderJpa.removeAllContainerFilters();
        // every day
        for (int i = 0; i < period; i++) {
            DateTime fromDateNew = fromDateValue.plusDays(i);
            DateTime toDateNew = fromDateValue.plusDays(i + 1);

            if (fromDayFilter != null) {
                orderJpa.removeContainerFilter(fromDayFilter);
            }
            if (toDayFilter != null) {
                orderJpa.removeContainerFilter(toDayFilter);
            }

            // order from one day
            fromDayFilter = new Compare.GreaterOrEqual("createDate", fromDateNew.toDate());
            toDayFilter = new Compare.LessOrEqual("createDate", toDateNew.toDate());
            orderJpa.addContainerFilter(fromDayFilter);
            orderJpa.addContainerFilter(toDayFilter);

            int statusFirstSize = 0;
            int statusSecondSize = 0;
            for (Object itemId : orderJpa.getItemIds()) {
                Order order = orderJpa.getItem(itemId).getEntity();

                if (order.getStatus() == statusFirstValue) {
                    statusFirstSize++;
                } else if (order.getStatus() == statusSecondValue) {
                    statusSecondSize++;
                }
            }

            firstLine.put(fromDateNew.toDate(), statusFirstSize);
            secondLine.put(fromDateNew.toDate(), statusSecondSize);
        }
        orderJpa.removeAllContainerFilters();


        Map<String, Map<Date, Integer>> filtered = new HashMap<>();
        filtered.put(statusFirstValue.getValue(), firstLine);
        filtered.put(statusSecondValue.getValue(), secondLine);


        return filtered;
    }

    public void drawLineChart(Embedded embedded) {
        lineChartPanel.setContent(embedded);
    }
}
