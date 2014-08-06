package com.cpwr.gdo.simulator.web.rule;

import static com.cpwr.gdo.simulator.web.utils.WebUtils.clearNonHiddenFieldValues;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;

import org.apache.click.control.AbstractLink;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.PageLink;
import org.apache.click.control.Submit;
import org.apache.click.control.Table;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.apache.click.dataprovider.DataProvider;
import org.apache.click.extras.control.LinkDecorator;
import org.apache.click.extras.control.TableInlinePaginator;
import org.apache.click.util.Bindable;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.model.Service;
import com.cpwr.gdo.simulator.service.SimulatorService;
import com.cpwr.gdo.simulator.web.page.BorderPage;
import com.cpwr.gdo.simulator.web.service.Services;

/**
 * The RulesTable Page with the table of rules for the service.F
 *
 */
@Component
public class Rules extends BorderPage {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6349172096645751325L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(Rules.class);

    /** The Constant OBJECT_ID. */
    private static final String OBJECT_ID = "ruleId";

    /** The rules table. */
    private Table rulesTable = new Table("table");

    /** The edit link. */
    private ActionLink editLink = new ActionLink("edit", getMessage("rule.edit.lnk"), this, "onEditClick");

    /** The response view as xml. */
    private PageLink responseViewAsXML = new PageLink("responseViewAsXML", ResponseXmlViewer.class);

    /** The back link. */
    private PageLink backLink = new PageLink("back", getMessage("rule.to.services.lnk"), Services.class);

    /** The delete link. */
    private ActionLink deleteLink = new ActionLink("delete", getMessage("rule.delete.lnk"), this, "onDeleteClick");

    /** The gmoc service. */
    @Autowired
    @Qualifier("simulatorService")
    private SimulatorService simulatorService;

    /** The request type. */
    @Bindable
    private String requestType;

    /* form to create new rule */
    /** The form. */
    private Form form = new Form("form");

    /** The control form. */
    private Form controlForm = new Form("control-form");

    // Constructor ------------------------------------------------------------

    /**
     * Instantiates a new rules table page.
     */
    public Rules() {
        controlForm.add(backLink);
        addControl(controlForm);

        addControl(form);
        addControl(rulesTable);
        addControl(editLink);
        addControl(deleteLink);

        // Setup rule form
        TextField requestTypeField = new TextField("service.requestType", getMessage("rule.service.requestType"),
                Boolean.TRUE);
        requestTypeField.setMaxLength(500);
        requestTypeField.setReadonly(true);
        requestTypeField.setWidth("500px");
        requestTypeField.setTitle(getMessage("rule.service.requestType.title"));
        form.add(requestTypeField);

        TextField nameField = new TextField("name", getMessage("rule.name"), Boolean.TRUE);
        nameField.setMaxLength(500);
        nameField.setFocus(true);
        nameField.setWidth("500px");
        nameField.setTitle(getMessage("rule.name.title"));
        form.add(nameField);

        TextField description = new TextField("description", getMessage("rule.description"), Boolean.TRUE);
        description.setMaxLength(500);
        description.setWidth("500px");
        description.setTitle(getMessage("rule.description.title"));
        form.add(description);

        TextField expr = new TextField("xpathRuleData.xpathExpression", getMessage("rule.xpath.expression"));
        expr.setMaxLength(500);
        expr.setWidth("500px");
        expr.setTitle(getMessage("rule.xpath.expression.title"));
        form.add(expr);

        TextField value = new TextField("xpathRuleData.xpathValue", getMessage("rule.xpath.value"));
        value.setMaxLength(500);
        value.setWidth("500px");
        value.setTitle(getMessage("rule.xpath.value.title"));
        form.add(value);

        /* response payload */
        TextArea responseArea = new TextArea("response", getMessage("rule.response"), Boolean.TRUE);
        responseArea.setWidth("500px");
        responseArea.setRows(20);
        responseArea.setTitle(getMessage("rule.response.title"));
        responseArea.setStyle("display", "none");

        form.add(responseArea);

        form.add(new HiddenField("formattedResponse", StringUtils.EMPTY));

        form.add(new Submit("ok", getMessage("rule.btn.save.rule"), this, "onSaveClick"));
        form.add(new Submit("cancel", this, "onCancelClick"));

        form.add(new HiddenField(OBJECT_ID, Long.class));
        form.add(new HiddenField(Table.PAGE, String.class));
        form.add(new HiddenField(Table.COLUMN, String.class));

        // Setup customers table
        rulesTable.setClass(Table.CLASS_ITS);
        rulesTable.setPageSize(10);
        rulesTable.setShowBanner(true);
        rulesTable.setSortable(true);
        rulesTable.setPaginator(new TableInlinePaginator(rulesTable));
        rulesTable.setPaginatorAttachment(Table.PAGINATOR_INLINE);
        rulesTable.setWidth("");

        Column column = new Column("service.requestType", getMessage("rule.service.requestType"));
        column.setTextAlign("left");
        column.setWidth("80px");
        rulesTable.addColumn(column);

        column = new Column("name", getMessage("rule.name"));
        column.setWidth("220px");
        rulesTable.addColumn(column);

        column = new Column("description", getMessage("rule.description"));
        column.setTextAlign("left");
        column.setWidth("100px");
        rulesTable.addColumn(column);

        column = new Column("xpathRuleData.xpathExpression", getMessage("rule.xpath.expression"));
        column.setWidth("140px");
        rulesTable.addColumn(column);

        column = new Column("xpathRuleData.xpathValue", getMessage("rule.xpath.value"));
        column.setWidth("140px");
        rulesTable.addColumn(column);

        column = new Column(getMessage("rule.response"));
        column.setTextAlign("center");

        responseViewAsXML.setImageSrc("/assets/images/controls.png");
        responseViewAsXML.setTitle(getMessage("rule.response"));
        responseViewAsXML.setAttribute("target", "_blank");

        AbstractLink[] responseLink = new AbstractLink[] { responseViewAsXML };

        column.setDecorator(new LinkDecorator(rulesTable, responseLink, OBJECT_ID));
        column.setSortable(false);
        rulesTable.addColumn(column);

        column = new Column("Action");
        column.setSortable(false);

        editLink.setImageSrc("/assets/images/table-edit.png");
        editLink.setTitle("Edit rules");

        deleteLink.setImageSrc("/assets/images/table-delete.png");
        deleteLink.setTitle("Delete rule");
        deleteLink.setName("delete_rule");
        deleteLink.setAttribute("onclick", "return window.confirm('" + getMessage("rule.confirm.delete.msg") + "');");

        ActionLink[] links = new ActionLink[] { editLink, deleteLink };
        column.setDecorator(new LinkDecorator(rulesTable, links, OBJECT_ID));
        rulesTable.addColumn(column);

        rulesTable.setDataProvider(new DataProvider<Rule>() {
            /**
			 * 
			 */
            private static final long serialVersionUID = -82221301745324321L;

            public List<Rule> getData() {
                return simulatorService.getRulesForService(requestType);
            }
        });
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * On delete click.
     * 
     * @return true, if successful
     */
    public boolean onDeleteClick() {
        final Long ruleId = deleteLink.getValueLong();

        LOG.debug("onDeleteClick, ruleId: {}", ruleId);

        simulatorService.deleteRuleById(ruleId);

        return true;
    }

    /**
     * Handle the delete link click event.
     * 
     * @return true
     */
    public boolean onViewResponseClick() {

        LOG.debug("onViewResponseClick ...");

        return true;
    }

    /**
     * On edit click.
     * 
     * @return true, if successful
     */
    public boolean onEditClick() {
        Long ruleId = editLink.getValueLong();
        LOG.debug("onEditClick, ruleId: {}", ruleId);

        if (ruleId != null) {
            Rule rule = simulatorService.findRuleByRuleId(ruleId);
            form.copyFrom(rule);
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

        Rule rule = null;
        // if (form.isValid()) {
        Long ruleId = (Long) form.getField(OBJECT_ID).getValueObject();

        LOG.debug("onSaveClick, ruleId: {}", ruleId);

        if (ruleId != null) {
            rule = simulatorService.findRuleByRuleId(ruleId);
        }

        if (rule == null) {
            processRequestType();

            rule = simulatorService.createNewEmptyRule();
            Service service = simulatorService.findServicesByRequestType(requestType).get(0);
            rule.setService(service);
        }

        form.copyTo(rule);

        validationErrors.addAll(simulatorService.validateRule(rule));

        if (validationErrors.size() == 0) {
            simulatorService.createRule(rule);
        } else {
            StringBuffer msg = new StringBuffer(EMPTY);
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
        LOG.debug("onCancelClick ...");

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
        LOG.debug("onGet requestType: {}", requestType);

        processRequestType();

        form.getField("service.requestType").setValue(requestType);

        form.getField(Table.PAGE).setValue(Integer.toString(rulesTable.getPageNumber()));
        form.getField(Table.COLUMN).setValue(rulesTable.getSortedColumn());
    }

    private void processRequestType() {
        if (isNotBlank(requestType)) {
            getContext().setSessionAttribute("requestType", requestType);
        } else {
            requestType = (String) getContext().getSessionAttribute("requestType");
        }
    }

    /**
     * On post.
     * 
     * @see org.apache.click.Page#onPost()
     */
    @Override
    public void onPost() {
        LOG.debug("onPost ...");

        if (isBlank(requestType)) {
            requestType = (String) getContext().getSessionAttribute("requestType");
        }

        form.getField("service.requestType").setValue(requestType);

        final String pageNumber = form.getField(Table.PAGE).getValue();

        if (isNotBlank(pageNumber)) {
            rulesTable.setPageNumber(parseInt(pageNumber));
            rulesTable.setSortedColumn(form.getField(Table.COLUMN).getValue());
        }
    }

    @Override
    public String getHelpPageLink() {
        return "rulesHelp.htm";
    }
}
