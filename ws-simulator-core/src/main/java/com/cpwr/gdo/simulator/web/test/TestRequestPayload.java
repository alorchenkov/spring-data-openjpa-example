package com.cpwr.gdo.simulator.web.test;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

import java.util.List;

import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.Submit;
import org.apache.click.control.TextArea;
import org.apache.click.extras.control.AutoCompleteTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.service.SimulatorService;
import com.cpwr.gdo.simulator.web.page.BorderPage;

@Component
public class TestRequestPayload extends BorderPage {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(TestRequestPayload.class);

    /** The form. */
    private Form form = new Form("request-form");

    /** The simulator service. */
    @Autowired
    @Qualifier("simulatorService")
    private SimulatorService simulatorService;

    // Constructor -----------------------------------------------------------

    /**
     * Instantiates a new test request payload.
     */
    public TestRequestPayload() {
        addControl(form);

        // 1. Setup request form
        // final FieldSet fieldSet = new FieldSet("rule");

        final AutoCompleteTextField requestTypeField = new AutoCompleteTextField("requestType", "Service requestType",
                Boolean.TRUE) {
            private static final long serialVersionUID = 7092103720705173823L;

            public List<String> getAutoCompleteList(final String requestType) {

                LOG.debug("getAutoCompleteList, requestType: {}", requestType);

                return simulatorService.findRequestTypes(trimToEmpty(requestType));
            }
        };

        requestTypeField.setMaxLength(500);
        requestTypeField.setFocus(true);
        requestTypeField.setWidth("300px");
        requestTypeField.setTitle("Input request type, for example NGDOEGetVehicleInfo");
        // fieldSet.add(requestTypeField);

        /* response payload */
        final TextArea requestArea = new TextArea("request", "Request", Boolean.TRUE);
        requestArea.setWidth("500px");
        requestArea.setRows(20);
        requestArea.setStyle("display", "none");
        // fieldSet.add(requestArea);

        form.add(requestTypeField);
        form.add(requestArea);

        form.add(new Submit("find", "  OK  ", this, "onFindRuleClick"));
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
    public boolean onFindRuleClick() {
        String requestType = (String) form.getField("requestType").getValueObject();
        String requestPayload = (String) form.getField("request").getValueObject();

        LOG.debug("onFindRuleClick, requestType: {}", requestType);

        if (isNotBlank(requestType)) {
            Rule rule = simulatorService.getApplicableRule(requestType, requestPayload);

            if (rule != null) {
                addModel("applicableRule", rule);
            } else {
                addModel("message", "Rule not found");
            }
        }

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
