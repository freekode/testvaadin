package com.hickory.views;

import com.hickory.models.Order;
import com.hickory.models.User;
import com.hickory.services.ContainerBuilder;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.*;

/**
 * Created by Evgeny Frolov on 19/11/14.
 */
public class GraphView extends CommonPanel implements View, Property.ValueChangeListener {
    private JPAContainer<Order> orderJpa;
    private JPAContainer<User> managerJpa;
    private ComboBox managerComboBox;
    private PopupDateField fromDate;
    private PopupDateField toDate;
    private NativeSelect statusFirst;
    private NativeSelect statusSecond;
    private Panel lineChartPanel;


    public GraphView() {
        orderJpa = ContainerBuilder.getContainer(Order.class);
        managerJpa = ContainerBuilder.getContainer(User.class);


        managerComboBox = new ComboBox("Менеджер");
        managerComboBox.setContainerDataSource(managerJpa);
        managerComboBox.setConverter(new SingleSelectConverter<User>(managerComboBox));
        managerComboBox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.ITEM);
        managerComboBox.setNullSelectionAllowed(false);

        managerJpa.addContainerFilter(new Compare.Equal("userType", User.UserType.MANAGER));
        Collection<Object> managers = managerJpa.getItemIds();
        if (managers.iterator().hasNext()) {
            managerComboBox.select(managers.iterator().next());
        }
        managerComboBox.addValueChangeListener(this);


        java.util.Calendar now = new GregorianCalendar();

        toDate = new PopupDateField("До");
        toDate.setResolution(Resolution.DAY);
        toDate.setDateFormat("yyyy-MM-dd");
        java.util.Calendar toDateValue = new GregorianCalendar(now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH), now.get(java.util.Calendar.DAY_OF_MONTH));
        toDate.setValue(toDateValue.getTime());
        toDate.addValueChangeListener(this);


        fromDate = new PopupDateField("От");
        fromDate.setResolution(Resolution.DAY);
        fromDate.setDateFormat("yyyy-MM-dd");
        java.util.Calendar fromDateValue = (java.util.Calendar) toDateValue.clone();
        fromDateValue.set(java.util.Calendar.MONTH, fromDateValue.get(java.util.Calendar.MONTH) - 1);
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


        HorizontalLayout chartControlDateLayout = new HorizontalLayout(managerComboBox, fromDate, toDate);
        chartControlDateLayout.setSpacing(true);
        HorizontalLayout chartControlStatusLayout = new HorizontalLayout(statusFirst, statusSecond);
        chartControlStatusLayout.setSpacing(true);
        VerticalLayout chartControlLayout = new VerticalLayout(chartControlDateLayout, chartControlStatusLayout);
        chartControlLayout.setSpacing(true);


        lineChartPanel = new Panel();
        lineChartPanel.addStyleName("borderless");
        lineChartPanel.setSizeUndefined();


        getStandardViewLayout().addContent(chartControlLayout);
//        getStandardViewLayout().addContent(allOrders);
        getStandardViewLayout().addContent(lineChartPanel);
        setContent(getStandardViewLayout());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        drawLineChart(DashboardView.getDateLineChart(getFilteredData()));
    }

    @Override
    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
        drawLineChart(DashboardView.getDateLineChart(getFilteredData()));
    }

    public Map<String, Map<Date, Integer>> getFilteredData() {
        Map<Date, Integer> firstLine = new HashMap<>();
        Map<Date, Integer> secondLine = new HashMap<>();


        EntityItem<User> entityItem = managerJpa.getItem(managerComboBox.getValue());
        if (entityItem == null) {
            return new HashMap<>();
        }
        User manager = entityItem.getEntity();
        DateTime fromDateValue = new DateTime(fromDate.getValue());
        DateTime toDateValue = new DateTime(toDate.getValue());
        Order.StatusType statusFirstValue = (Order.StatusType) statusFirst.getValue();
        Order.StatusType statusSecondValue = (Order.StatusType) statusSecond.getValue();

        Integer period = Days.daysBetween(fromDateValue, toDateValue).getDays() + 1;


        orderJpa.removeAllContainerFilters();

        // get orders for manager
        orderJpa.addContainerFilter(new Compare.Equal("manager", manager));


        Container.Filter fromDayFilter = null;
        Container.Filter toDayFilter = null;
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
