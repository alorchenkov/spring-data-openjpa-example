package com.cpwr.gdo.simulator.web.rule;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.click.Page;
import org.apache.click.control.Form;
import org.apache.click.control.PageLink;
import org.apache.click.util.Bindable;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.model.Rule;
import com.cpwr.gdo.simulator.service.SimulatorService;

/**
 * The ResponseXmlViewer page which displays response xml in a separate browser
 * window.
 *
 */
@Component
public class ResponseXmlViewer extends Page {

    /**
     * 
     */
    private static final long serialVersionUID = 1301387443979489459L;

    private static final Logger LOG = LoggerFactory.getLogger(ResponseXmlViewer.class);

    @Autowired
    @Qualifier("simulatorService")
    private SimulatorService simulatorService;

    /** The rule id. */
    @Bindable
    private Long ruleId;

    /** The request type. */
    @Bindable
    private String requestType;

    /** The back link. */
    private PageLink backLink = new PageLink("back", "<< Back to Rules", Rules.class);

    /** The control form. */
    private Form controlForm = new Form("control-form");

    /**
     * Instantiates a new response xml viewer.
     */
    public ResponseXmlViewer() {

        LOG.debug("ResponseXmlViewer requestType: {}", requestType);

        backLink.setParameter("requestType", requestType);
        controlForm.add(backLink);
        addControl(controlForm);
    }

    /**
     * Render the Java source file as "text/plain".
     * 
     * @see Page#onGet()
     */
    @Override
    public void onGet() {

        String payload = StringUtils.EMPTY;

        Rule rule = simulatorService.findRuleByRuleId(ruleId);

        LOG.debug("ResponseXmlViewer.onGet ruleId: {}", ruleId);

        if (rule != null) {
            payload = rule.getResponse();
        }

        final HttpServletResponse response = getContext().getResponse();

        response.setContentType("text/xml");
        response.setHeader("response", "no-cache");

        InputStream inputStream = null;

        try {
            inputStream = new ByteArrayInputStream(payload.getBytes("UTF-8"));

            PrintWriter writer = response.getWriter();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();

            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }

            /*
             * Set page path to null to signal to ClickServlet that rendering
             * has been completed
             */

            setPath(null);
        } catch (IOException ioe) {
            LOG.error("Error converting xml to string, ", ioe);
            throw new RuntimeException(ioe);

        } finally {
            ClickUtils.close(inputStream);
        }
    }
}
