package com.cpwr.gdo.simulator.web.test;

import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.service.SimulatorService;
import com.cpwr.gdo.simulator.web.page.BorderPage;

/**
 * The Class TestXpathExpression.
 *
 */
@Component
public class TestXpathExpression extends BorderPage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(TestXpathExpression.class);

    /** The form. */
    private Form form = new Form("xpath-form");

    /** The simulator service. */
    @Autowired
    @Qualifier("simulatorService")
    private SimulatorService simulatorService;

    // Constructor -----------------------------------------------------------

    /**
     * Instantiates a new test request payload.
     */
    public TestXpathExpression() {
        addControl(form);

        /* 1. Setup xpath request form */
        TextField expr = new TextField("xpathExpression", "XPath Expression");
        expr.setMaxLength(500);
        expr.setWidth("500px");
        expr.setTitle("Xpath expression, for example //Warranty/VIN");
        form.add(expr);

        TextField value = new TextField("xpathValue", "XPath Value");
        value.setMaxLength(500);
        value.setWidth("500px");
        value.setTitle("Xpath value, for example VIN, 12345678RTY1234");
        form.add(value);

        /* response payload */
        final TextArea requestArea = new TextArea("request", "Request", Boolean.TRUE);
        requestArea.setWidth("500px");
        requestArea.setRows(20);
        requestArea.setStyle("display", "none");

        form.add(requestArea);

        form.add(new Submit("check", "  OK  ", this, "onCheckXpathClick"));
        form.add(new Submit("clear", this, "onClearClick"));
    }

    @Override
    public String getHelpPageLink() {
        return "";
    }

    // Event Handlers ---------------------------------------------------------

    /**
     * When page is first displayed on the GET request.
     * 
     * @see Page#onGet()
     */
    @Override
    public void onGet() {
        // TODO
    }

    /**
     * On ok click.
     * 
     * @return true, if successful
     */
    public boolean onCheckXpathClick() {
        final String xpathExpression = (String) form.getField("xpathExpression").getValueObject();
        final String xpathValue = (String) form.getField("xpathValue").getValueObject();
        final String requestPayload = (String) form.getField("request").getValueObject();

        LOG.debug("onCheckXpathClick, xpathExpression:[{}]  :: xpathValue: [{}]", xpathExpression, xpathValue);

        final Boolean isXpathChecked = simulatorService.checkXpathExpression(xpathExpression, xpathValue,
                requestPayload);

        addModel("isXpathChecked", isXpathChecked);
        addModel("xpathQueryResult", true);

        return true;
    }

    /**
     * On cancel click.
     * 
     * @return true, if successful
     */
    public boolean onClearClick() {
        // setRedirectPath();
        // clearNonHiddenFieldValues(form);

        form.clearErrors();
        form.clearValues();

        return false;
    }

}
