package com.hickory;

import com.hickory.models.*;
import com.hickory.models.interfaces.SerializableToJson;
import com.hickory.popups.LoginPopup;
import com.hickory.popups.ModalPopup;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.*;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Theme("valo")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI {
    public static final String PERSISTENCE_UNIT = "komruscrm";
    private ValoMenuLayout root = new ValoMenuLayout();
    private ComponentContainer viewDisplay = root.getContentContainer();
    private CssLayout menu = new CssLayout();
    private CssLayout menuItemsLayout = new CssLayout();
    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<>();
    private MenuBar.MenuItem userNameItem;
    private User.UserType utype;
    private MenuBar settings;

    @Override
    protected void init(VaadinRequest request) {

        Responsive.makeResponsive(this);

        getPage().setTitle("Komrus CRM");
        setContent(root);
        root.setWidth("100%");

        navigator = new Navigator(this, viewDisplay);
        navigator.setErrorView(ErrorView.class);
//        navigator.addView("login", LoginView.class);
        navigator.addView("dashboard", DashboardView.class);
        navigator.addView("graph", GraphView.class);
        navigator.addView("category", CategoryView.class);
        navigator.addView("client", ClientView.class);
        navigator.addView("item", ItemView.class);
        navigator.addView("order", OrderView.class);
        navigator.addView("supplier", SupplierView.class);
        navigator.addView("user", UserView.class);
        navigator.addView("store", StoreView.class);
        navigator.addView("purchase", PurchaseView.class);
        navigator.addView("company", CompanyView.class);
        navigator.addView("cart", CartView.class);
        navigator.addView("techdoc", TechDoc.class);
//        navigator.addView("variable", VariableView.class);
//        navigator.addView("report", ReportView.class);
//        navigator.addView("history", HistoryView.class);
//        navigator.addView("return", ReturnView.class);

        String f = Page.getCurrent().getUriFragment();
        if (f == null || f.equals("")) {
            navigator.navigateTo("dashboard");
//            setContent(new LoginView());
        }

        settings = new MenuBar();
        settings.addStyleName("user-menu");

        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
//                if (event.getViewName().equals("login")) {
//                    return true;
//                } else if (getSession().getAttribute("user") != null) {
//                    return true;
//                }
//
//                getNavigator().navigateTo("login");
//                return false;


                if (getSession().getAttribute("user") != null) {
                    return true;
                }

                LoginPopup loginPopup = new LoginPopup();
                loginPopup.addPopupListener(new ModalPopup.ClickListener() {
                    @Override
                    public void clicked(ModalPopup.ClickEvent event) {
//                        updateUserName(false);
                        buildMenu();
                        getNavigator().navigateTo("dashboard");
                    }
                });

                addWindow(loginPopup);

                return false;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                for (Component aMenuItemsLayout : menuItemsLayout) {
                    aMenuItemsLayout.removeStyleName("selected");
                }
                for (Map.Entry<String, String> item : menuItems.entrySet()) {
                    if (event.getViewName().equals(item.getKey())) {
                        for (Component c : menuItemsLayout) {
                            if (c.getCaption() != null && c.getCaption().startsWith(item.getValue())) {
                                c.addStyleName("selected");
                                break;
                            }
                        }
                        break;
                    }
                }

                menu.removeStyleName("valo-menu-visible");
            }
        });

        buildMenu();

    }

    public void buildMenu() {
        if (getSession().getAttribute("user") != null) {

            settings = new MenuBar();
            settings.addStyleName("user-menu");

            menu.setId("menu");
            if (getPage().getWebBrowser().isIE()
                    && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
                menu.setWidth("320px");
            }
            // Add items

            HorizontalLayout top = new HorizontalLayout();
            top.setWidth("250px");
            top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            top.addStyleName("valo-menu-title");
            menu.addComponent(top);

            Button showMenu = new Button("Menu", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (menu.getStyleName().contains("valo-menu-visible")) {
                        menu.removeStyleName("valo-menu-visible");
                    } else {
                        menu.addStyleName("valo-menu-visible");
                    }
                }
            });
            showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
            showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
            showMenu.addStyleName("valo-menu-toggle");
            showMenu.setIcon(FontAwesome.LIST);
            menu.addComponent(showMenu);
            Label title = new Label("<h3>Komrus <strong>CRM</strong></h3>",
                    ContentMode.HTML);
            title.setSizeUndefined();
            top.addComponent(title);
            top.setExpandRatio(title, 1);
            updateUserName(true);
            menu.addComponent(settings);

            menuItemsLayout.setPrimaryStyleName("valo-menuitems");
            menu.addComponent(menuItemsLayout);

            for (final Map.Entry<String, String> item : menuItems.entrySet()) {
                Button b = new Button(item.getValue(), new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        navigator.navigateTo(item.getKey());
                    }
                });
                b.setHtmlContentAllowed(true);
                b.setPrimaryStyleName("valo-menu-item");
                menuItemsLayout.addComponent(b);
            }

            root.addMenu(menu);
        }
    }

    public void updateUserName(boolean updateHeader) {
        String currentUsername = ((User) getSession().getAttribute("user")).getLogin();

        if (updateHeader) {
            userNameItem = settings.addItem(currentUsername, new ClassResource("/images/logo.png"), null);
            userNameItem.addSeparator();
            userNameItem.addItem("Выход", new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem item) {
                    getSession().close();
                    getPage().setLocation("/");
                }
            });
        }

        userNameItem.setText(currentUsername);
        utype = ((User) getSession().getAttribute("user")).getUserType();
        if (utype == User.UserType.ADMINISTRATOR) {
            menuItems.put("dashboard", "Сводка");
            menuItems.put("graph", "Графики");
            menuItems.put("techdoc", "Поиск по TecDoc");
            menuItems.put("item", "Номенклатура");
            menuItems.put("cart", "Новый заказ");
            menuItems.put("order", "Заказы");
            menuItems.put("client", "Клиенты");
            menuItems.put("supplier", "Поставщики");
            menuItems.put("purchase", "Закупки");
            menuItems.put("company", "Компании");
            menuItems.put("store", "Склад");
            menuItems.put("user", "Пользователи");
        } else if (utype == User.UserType.MANAGER) {
            menuItems.put("dashboard", "Сводка");
            menuItems.put("techdoc", "Поиск по TecDoc");
            menuItems.put("item", "Номенклатура");
            menuItems.put("cart", "Новый заказ");
            menuItems.put("order", "Заказы");
            menuItems.put("client", "Клиенты");
            menuItems.put("supplier", "Поставщики");
        } else if (utype == User.UserType.STOREKEEPER) {
            menuItems.put("dashboard", "Сводка");
            menuItems.put("order", "Заказы");
            menuItems.put("store", "Склад");
        } else if (utype == User.UserType.SUPERVISOR) {
            menuItems.put("dashboard", "Сводка");
            menuItems.put("graph", "Графики");
            menuItems.put("user", "Пользователи");
        }
    }

    @WebServlet(value = "/api/categories")
    public static class CategoryServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            JPAContainer<Category> categories = ContainerBuilder.getContainer(Category.class);
//            categories.refresh();
            JSONArray categoryArr = new JSONArray();

            for (Object itemId : categories.getItemIds()) {
                SerializableToJson category = categories.getItem(itemId).getEntity();
                categoryArr.put(category.toJson());
            }

            resp.setContentType("text/html; charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.println(categoryArr.toString());
        }
    }

    @WebServlet(value = "/api/item")
    public static class ItemServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String code = req.getParameter("code");

            JPAContainer<Item> items = ContainerBuilder.getContainer(Item.class);
            JSONArray itemArr = new JSONArray();
            HashSet<SerializableToJson> itemSet = new HashSet<>();

            if (!code.toString().equals("0")) {
                items.addContainerFilter(new SimpleStringFilter("factoryCode", code, true, true));
                for (Object itemId : items.getItemIds()) {
                    SerializableToJson item = items.getItem(itemId).getEntity();
                    itemSet.add(item);
                    JPAContainer<Item> analogItems = ContainerBuilder.getContainer(Item.class);
                    analogItems.addContainerFilter(new SimpleStringFilter("originalCode", items.getItem(itemId).getEntity().getOriginalCode(), true, true));
                    for (Object itemId2 : analogItems.getItemIds()) {
                        SerializableToJson analogItem = analogItems.getItem(itemId2).getEntity();
                        itemSet.add(analogItem);
                    }
                }

                items.removeAllContainerFilters();
                items.addContainerFilter(new SimpleStringFilter("originalCode", code, true, true));
                for (Object itemId : items.getItemIds()) {
                    SerializableToJson item = items.getItem(itemId).getEntity();
                    itemSet.add(item);
                }

                for (SerializableToJson item : itemSet)
                    itemArr.put(item.toJson());
            } else {
                items.refresh();
                int i = 0;
                for (Object itemId : items.getItemIds()) {
                    if (i < 10) {
                        i++;
                        SerializableToJson item = items.getItem(itemId).getEntity();
                        itemArr.put(item.toJson());
                    }
                }
            }

            resp.setContentType("text/html; charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.println(itemArr.toString());
        }
    }

    @WebServlet(value = "/api/client")
    public static class ClientServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html; charset=utf-8");
            PrintWriter out = resp.getWriter();
            String login = req.getParameter("login");

            JPAContainer<Client> clients = ContainerBuilder.getContainer(Client.class);
//            clients.refresh();

            System.out.println(clients);
            clients.addContainerFilter(new Compare.Equal("login", login));

            Client client = null;
            for (Object itemId : clients.getItemIds()) {
                client = clients.getItem(itemId).getEntity();
            }
            if (client == null) {
                out.println("not found");
                return;
            }

            out.println(client.toJson());
        }
    }

    @WebServlet(value = "/api/order/create")
    public static class OrderServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("application/json; charset=utf-8");
            PrintWriter out = resp.getWriter();
            BufferedReader reader = req.getReader();


            StringBuilder inputJson = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                inputJson.append(line);
            }


            try {
                JSONObject jsonObject = new JSONObject(inputJson.toString());


                Integer addressId = null;
                if (jsonObject.has("id_address")) {
                    addressId = new Integer(jsonObject.get("id_address").toString());
                }
                Integer clientId = new Integer(jsonObject.get("id_login").toString());
                JSONArray jsonItemArr = jsonObject.getJSONArray("items");


                JPAContainer<Client> clientJpa = ContainerBuilder.getContainer(Client.class);
                JPAContainer<Order> orderJpa = ContainerBuilder.getContainer(Order.class);
                JPAContainer<Item> itemJpa = ContainerBuilder.getContainer(Item.class);


                EntityItem<Client> clientEntity = clientJpa.getItem(clientId);
                if (clientEntity == null) {
                    throw new Exception("client not found");
                }
                Client client = clientEntity.getEntity();


                Address address = null;
                Order.DeliveryType deliveryType = Order.DeliveryType.DELIVERY;
                if (addressId == null || addressId == 0) {
                    deliveryType = Order.DeliveryType.PICKUP;
                } else {
                    for (Address clientAddress : client.getAddresses()) {
                        if (Objects.equals(clientAddress.getId(), addressId)) {
                            address = clientAddress;
                            break;
                        }
                    }
                    if (address == null) {
                        throw new Exception("address not found");
                    }
                }


                Order newOrder = orderJpa.createEntityItem(new Order()).getEntity();

                HashSet<OrderItem> orderItems = new HashSet<>();
                for (int i = 0; i < jsonItemArr.length(); i++) {
                    JSONObject itemJsonObj = (JSONObject) jsonItemArr.get(i);

                    EntityItem<Item> itemEntity = itemJpa.getItem(itemJsonObj.getInt("id"));
                    if (itemEntity == null) {
                        continue;
                    }

                    Item item = itemEntity.getEntity();
                    Integer amount = itemJsonObj.getInt("amount");

                    OrderItem orderItem = new OrderItem();
                    orderItem.setItem(item);
                    orderItem.setAmount(amount);
                    orderItem.setOrder(newOrder);

                    orderItems.add(orderItem);
                }

                newOrder.setClient(client);
                newOrder.setOrderItems(orderItems);
                newOrder.setAddress(address);
                newOrder.setDelivery(deliveryType);

                orderJpa.addEntity(newOrder);
                orderJpa.commit();

                EntityItem<Order> thisOrder = orderJpa.getItem(orderJpa.lastItemId());
                thisOrder.getItemProperty("number").setValue(orderJpa.lastItemId().toString());
                orderJpa.commit();


                out.println(thisOrder.getEntity().toJson());
            } catch (Exception e) {
                e.printStackTrace();
                out.println("{\"error\": \"" + e.getMessage() + "\"}");
            }
        }
    }

    @WebServlet(value = "/api/order/byclient")
    public static class OrderByClientServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("application/json; charset=utf-8");
            PrintWriter out = resp.getWriter();
            BufferedReader reader = req.getReader();


            StringBuilder inputJson = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                inputJson.append(line);
            }


            try {
                JSONObject jsonObject = new JSONObject(inputJson.toString());

                String clientId = jsonObject.get("id_login").toString();


                JPAContainer<Client> clientJpa = ContainerBuilder.getContainer(Client.class);
                JPAContainer<Order> orderJpa = ContainerBuilder.getContainer(Order.class);
                clientJpa.addContainerFilter(new Compare.Equal("id", new Integer(clientId)));
                if (!clientJpa.getItemIds().iterator().hasNext()) {
                    throw new Exception("user not found");
                }
                Client client = clientJpa.getItem(clientJpa.getItemIds().iterator().next()).getEntity();
                orderJpa.addContainerFilter(new Compare.Equal("client", client));


                JSONArray orderArr = new JSONArray();
                for (Object itemId : orderJpa.getItemIds()) {
                    Order order = orderJpa.getItem(itemId).getEntity();

                    JSONObject jsonOrder = new JSONObject()
                            .put("number", order.getNumber())
                            .put("createDate", order.getCreateDate())
                            .put("status", order.getStatus().getValue());

                    orderArr.put(jsonOrder);
                }


                out.println(orderArr.toString());
            } catch (Exception e) {
                e.printStackTrace();
                out.println("{\"error\": \"" + e.getMessage() + "\"}");
            }
        }
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "com.hickory.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
}
