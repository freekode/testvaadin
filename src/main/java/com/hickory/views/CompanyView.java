package com.hickory.views;

import com.hickory.models.Company;
import com.hickory.models.User;
import com.hickory.services.ContainerBuilder;
import com.hickory.views.forms.AbstractForm;
import com.hickory.views.forms.CompanyForm;
import com.hickory.views.services.FilterTableDecorator;
import com.hickory.views.services.ManagerConverter;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomTable;
import org.tepi.filtertable.FilterTable;

/**
 * @author Evgeny Frolov
 */
public class CompanyView extends CommonView implements Button.ClickListener, ItemClickEvent.ItemClickListener {
    private JPAContainer<Company> companyJpa;
    private JPAContainer<User> managerJpa;
    private FilterTable companyTable;
    private Button newBtn;


    public CompanyView() {
        setWidth("100%");

        newBtn = new Button("Новый", this);

        companyJpa = ContainerBuilder.getContainer(Company.class);
        managerJpa = ContainerBuilder.getContainer(User.class);

        companyTable = configureCompanyTable(buildCompanyTable(companyJpa));

        getStandardViewLayout().addTop(newBtn);
        getStandardViewLayout().addContent(companyTable);
        setContent(getStandardViewLayout());
    }


    public static FilterTable buildCompanyTable(Container container) {
        FilterTable table = new FilterTable();
        table.setContainerDataSource(container);
        table.setColumnCollapsingAllowed(true);
        table.setColumnReorderingAllowed(true);
        table.setSelectable(true);
        table.setFilterBarVisible(true);
        table.addStyleName("no-stripes");
        table.addStyleName("borderless");

        table.setVisibleColumns("shortcutLegalName", "orgEmail",
                "orgPhoneNumber", "inn", "kpp", "okpo");

        table.setColumnHeaders("Название", "Email",
                "Тел. номер.", "ИНН", "КПП", "ОКПО");

        table.setConverter("manager", new ManagerConverter());
        table.setFilterDecorator(new FilterTableDecorator());

        return table;
    }

    public FilterTable configureCompanyTable(FilterTable table) {
        table.addItemClickListener(this);
        table.addGeneratedColumn("genRemove", new CustomTable.ColumnGenerator() {
            @Override
            public Object generateCell(final CustomTable source, final Object itemId, Object columnId) {
                Button removeBtn = new Button(null, new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        source.getContainerDataSource().removeItem(itemId);
                        commit();
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
    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == companyTable && event.isDoubleClick()) {
            EntityItem<Company> item = companyJpa.getItem(event.getItemId());

            CompanyForm companyForm = new CompanyForm(item, managerJpa);
            companyForm.addSaveListener(new AbstractForm.ClickListener() {
                @Override
                public void saved(AbstractForm.ClickEvent event) {
                    commit();
                    setContent(getStandardViewLayout());
                }

                @Override
                public void cancelled(AbstractForm.ClickEvent event) {
                    setContent(getStandardViewLayout());
                }
            });

            setContent(companyForm);
        }
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton() == newBtn) {
            newButtonClick();
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    private void newButtonClick() {
        final EntityItem<Company> newCompany = companyJpa.createEntityItem(new Company());

        CompanyForm companyForm = new CompanyForm(newCompany, managerJpa);
        companyForm.addSaveListener(new AbstractForm.ClickListener() {
            @Override
            public void saved(AbstractForm.ClickEvent event) {
                companyJpa.addEntity(newCompany.getEntity());

                commit();
                setContent(getStandardViewLayout());
            }

            @Override
            public void cancelled(AbstractForm.ClickEvent event) {
                setContent(getStandardViewLayout());
            }
        });
        setContent(companyForm);
    }

    @Override
    public void commitContainers() {
        companyJpa.commit();
        managerJpa.commit();
    }

    @Override
    public void commitFailed() {
        companyJpa.refresh();
        managerJpa.refresh();
    }
}
