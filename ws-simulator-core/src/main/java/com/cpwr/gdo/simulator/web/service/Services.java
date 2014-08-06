package com.cpwr.gdo.simulator.web.service;

import static com.cpwr.gdo.simulator.web.utils.WebUtils.clearNonHiddenFieldValues;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.PageLink;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.extras.control.AutoCompleteTextField;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.extras.control.TableInlinePaginator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.model.Service;
import com.cpwr.gdo.simulator.service.SimulatorService;
import com.cpwr.gdo.simulator.web.page.BorderPage;
import com.cpwr.gdo.simulator.web.rule.Rules;

/**
 * The ServiceTable Page with search/edit service form.
 * 
 */
@Component
public class Services extends BorderPage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7810930683225459138L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(Services.class);

    /** The Constant OBJECT_ID. */
    private static final String OBJECT_ID = "serviceId";

    /** The form. */
    private Form form = new Form("form");

    /** The search form. */
    private Form searchForm = new Form("search-form");

    /** The table. */
    private Table table = new Table("table");

    /** The edit link. */
    private ActionLink editLink = new ActionLink("Edit", this, "onEditClick");

    /** The view rules link. */
    private PageLink viewRulesLink = new PageLink("ViewRules", Rules.class);

    /** The delete link. */
    private ActionLink deleteLink = new ActionLink("Delete", this, "onDeleteClick");

    /**
     * The service is marked as transient since the page is stateful and we
     * don't want to serialize the service along with the page.
     */
    @Autowired
    @Qualifier("simulatorService")
    private SimulatorService simulatorService;

    /**
     * Constructor. Instantiates a new service table page.
     */
    public Services() {

        addControl(searchForm);
        addControl(form);
        addControl(table);
        addControl(editLink);
        addControl(deleteLink);

        // Setup the search form

        FieldSet fields = new FieldSet("serviceSearch", getMessage("service.search"));

        final AutoCompleteTextField requestTypeField = new AutoCompleteTextField("typeOfRequest",
                getMessage("service.requestType")) {
            private static final long serialVersionUID = 7092103720705173823L;

            public List<String> getAutoCompleteList(final String requestType) {

                LOG.debug("getAutoCompleteList, requestType: {}", requestType);

                return simulatorService.findRequestTypes(trimToEmpty(requestType));
            }
        };

        requestTypeField.setMaxLength(500);
        requestTypeField.setFocus(true);
        requestTypeField.setWidth("300px");
        requestTypeField.setTitle(getMessage("service.requestType.title"));

        fields.add(requestTypeField);

        fields.add(new Submit("Search", this, "onSearchClick"));
        fields.add(new Submit("Clear", this, "onClearClick"));
        searchForm.add(fields);

        // Setup service form
        FieldSet fieldSet = new FieldSet("service");

        TextField nameField = new TextField("requestType", "Request Type", Boolean.TRUE);
        nameField.setMaxLength(500);
        nameField.setFocus(true);
        nameField.setWidth("300px");
        nameField.setTitle("Input request type, for example NGDOEGetVehicleInfo");
        fieldSet.add(nameField);

        TextArea descrField = new TextArea("description", "Service Description", Boolean.TRUE);
        descrField.setWidth("300px");
        descrField.setRows(10);
        fieldSet.add(descrField);
        form.add(fieldSet);

        form.add(new Submit("ok", "Save Service", this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));

        form.add(new HiddenField(OBJECT_ID, Long.class));
        form.add(new HiddenField(Table.PAGE, String.class));
        form.add(new HiddenField(Table.COLUMN, String.class));

        // Setup service table
        table.setClass(Table.CLASS_ITS);
        table.setPageSize(10);
        table.setShowBanner(true);
        table.setSortable(true);
        table.setPaginator(new TableInlinePaginator(table));
        table.setPaginatorAttachment(Table.PAGINATOR_INLINE);

        Column column = new Column("requestType");
        column.setWidth("140px;");
        table.addColumn(column);

        column = new Column("description");
        column.setAutolink(true);
        column.setWidth("230px;");
        table.addColumn(column);

        viewRulesLink.setImageSrc("/assets/images/table.png");
        viewRulesLink.setTitle("View rules");

        column = new Column("Rules");
        column.setTextAlign("center");
        AbstractLink[] rulesLinks = new AbstractLink[] { viewRulesLink };
        column.setDecorator(new LinkDecorator(table, rulesLinks, "requestType"));
        column.setSortable(false);
        table.addColumn(column);

        editLink.setImageSrc("/assets/images/table-edit.png");
        editLink.setTitle("Edit service");

        deleteLink.setName("delete_service");
        deleteLink.setImageSrc("/assets/images/table-delete.png");
        deleteLink.setTitle("Delete service record");
        deleteLink.setAttribute("onclick", "return window.confirm('Are you sure you want to delete this record?');");

        column = new Column("Action");
        column.setTextAlign("center");
        ActionLink[] links = new ActionLink[] { editLink, deleteLink };
        column.setDecorator(new LinkDecorator(table, links, "requestType"));
        column.setSortable(false);
        table.addColumn(column);

        table.setDataProvider(new DataProvider<Service>() {
            /**
			 * 
			 */
            private static final long serialVersionUID = -7456677346158746353L;

            public List<Service> getData() {
                List<Service> services;

                if (isNotBlank(requestTypeField.getValue())) {
                    services = simulatorService.findServicesByRequestType(trimToEmpty((requestTypeField.getValue())));
                } else {
                    services = simulatorService.getAllServices();
                }

                LOG.debug("Services, services amount: {}", services.size());

                return services;
            }
        });
    }

    /** Event Handlers --------------------------------------------------------- */

    /**
     * Handle the search button click event.
     * 
     * @return true
     */
    public boolean onSearchClick() {
        return true;
    }

    /**
     * Handle the clear button click event.
     * 
     * @return true
     */
    public boolean onClearClick() {
        /* Clear field values */
        searchForm.clearErrors();
        searchForm.clearValues();

        /* Clear table state */
        table.setPageNumber(0);
        table.setSortedColumn(null);

        return true;
    }

    /**
     * Handle the delete link click event.
     * 
     * @return true
     */
    public boolean onDeleteClick() {
        String requestType = deleteLink.getValue();

        LOG.debug("onDeleteClick, requestType: {}", requestType);

        simulatorService.deleteService(requestType);
        return true;
    }

    /**
     * On edit click.
     * 
     * @return true, if successful
     */
    public boolean onEditClick() {
        String requestType = editLink.getValue();

        LOG.debug("onEditClick, requestType: {}", requestType);

        if (isNotBlank(requestType)) {
            Service service = simulatorService.findServiceByRequestType(requestType);
            form.copyFrom(service);
        }

        return true;
    }

    /**
     * On save click.
     * 
     * @return true, if successful
     */
    public boolean onSaveClick() {
        List<String> validationErrors = new ArrayList<String>();

        LOG.debug("onSaveClick, validationErrors: {}", validationErrors);

        // if (form.isValid()) {

        Long serviceId = null;

        if (isNotBlank(form.getFieldValue(OBJECT_ID))) {
            serviceId = Long.parseLong(form.getFieldValue(OBJECT_ID));
        }

        Service service = null;

        if (serviceId != null) {
            service = simulatorService.findServiceById(serviceId);
        }

        if (service == null) {
            service = new Service();
        }

        form.copyTo(service);

        validationErrors.addAll(simulatorService.validateService(service));

        if (validationErrors.size() == 0) {
            simulatorService.createService(service);
        } else {
            StringBuffer msg = new StringBuffer(StringUtils.EMPTY);
            for (String errMsg : validationErrors) {
                msg.append(errMsg).append("\n");
            }

            LOG.debug("onSaveClick, msg: {}", msg.toString());

            form.setError(msg.toString());
        }

        // }

        if (validationErrors.size() == 0) {

            clearNonHiddenFieldValues(form);
            form.clearErrors();
            form.clearValues();

        }

        return true;
    }

    /**
     * On cancel click.
     * 
     * @return true, if successful
     */
    public boolean onCancelClick() {
        clearNonHiddenFieldValues(form);

        form.clearErrors();
        form.clearValues();

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.click.Page#onGet()
     */
    @Override
    public void onGet() {
        final Long serviceId = (Long) form.getField(OBJECT_ID).getValueObject();

        if (serviceId == null) {
            form.clearValues();
        }

        form.getField(Table.PAGE).setValue(Integer.toString(table.getPageNumber()));
        form.getField(Table.COLUMN).setValue(table.getSortedColumn());

        /*
         * clear requestType session attribute which is used as selected
         * WebService to retrieve rules
         */
        getContext().removeSessionAttribute("requestType");

    }

    /**
     * On post.
     * 
     * @see org.apache.click.Page#onPost()
     */
    @Override
    public void onPost() {
        final String pageNumber = form.getField(Table.PAGE).getValue();

        if (isNotBlank(pageNumber)) {
            table.setPageNumber(parseInt(pageNumber));
            table.setSortedColumn(form.getField(Table.COLUMN).getValue());
        }
    }

    @Override
    public String getHelpPageLink() {
        return "serviceHelp.htm";
    }
}
