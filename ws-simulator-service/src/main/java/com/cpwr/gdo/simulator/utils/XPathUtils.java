package com.cpwr.gdo.simulator.utils;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.remove;
import static org.apache.commons.lang.StringUtils.substringBetween;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * The class contains methods to create document, to evaluate xpath expression,
 * etc.
 */
public final class XPathUtils {
    private static final Logger LOG = LoggerFactory.getLogger(XPathUtils.class);
    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    /**
     * Instantiates a new XPathUtils.
     */
    private XPathUtils() {
    }

    /**
     * Check if rule is applicable.
     * 
     * @param expression
     *            XPath expression
     * @param requestPayload
     *            the request payload file
     * @return true, if successful
     */
    public static boolean checkIfRuleIsApplicable(final String expression, final String requestPayload) {
        Boolean isRuleApplicable = false;

        try {
            Document xmlDocument = createXmlDocument(processRequestPayload(requestPayload));
            // Compile the expression to get a XPathExpression object.
            XPathExpression xPathExpression = XPATH.compile(expression);

            isRuleApplicable = (Boolean) xPathExpression.evaluate(xmlDocument, XPathConstants.BOOLEAN);
        } catch (XPathExpressionException xee) {
            LOG.error("Expression is not compiled, {}", expression, xee);
        }

        LOG.debug("isRuleApplicable {}", isRuleApplicable);

        return isRuleApplicable;
    }

    /**
     * Check if rule is applicable.
     * 
     * @param expression
     *            the expression
     * @param value
     *            the value
     * @param requestPayload
     *            the request payload
     * @return true, if successful
     */
    public static boolean checkIfRuleIsApplicable(final String expression, final String value,
            final String requestPayload) {

        LOG.debug("expression: {}, value: {}", expression, value);

        Boolean isRuleApplicable = false;

        try {
            final Document xmlDocument = createXmlDocument(processRequestPayload(requestPayload));
            // Compile the expression to get a XPathExpression object.
            final XPathExpression xPathExpression = XPATH.compile(expression);

            final String valueFromPayload = (String) xPathExpression.evaluate(xmlDocument, XPathConstants.STRING);

            LOG.debug("valueFromPayload: {}", valueFromPayload);

            if (equalsIgnoreCase(trimToEmpty(value), trimToEmpty(valueFromPayload))) {
                isRuleApplicable = true;
            }

        } catch (XPathExpressionException xee) {
            LOG.error("Expression is not compiled, {}", expression, xee);
        }

        LOG.debug("isRuleApplicable: {}", isRuleApplicable);

        return isRuleApplicable;
    }

    /**
     * Creates the xml document.
     * 
     * @param requestPayload
     *            the request payload
     * @return the document
     */
    private static Document createXmlDocument(final String requestPayload) {
        Document xmlDocument = null;

        try {

            final StreamSource ss = new StreamSource();
            ss.setInputStream(new ByteArrayInputStream(requestPayload.getBytes("UTF-8")));
            xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ss.getInputStream());

        } catch (Exception ex) {
            LOG.error("xmlDocument is null", ex);
        }

        return xmlDocument;
    }

    private static String processRequestPayload(final String requestPayload) {
        final String defaultNamespace = "xmlns=\"" + substringBetween(requestPayload, "xmlns=\"", "\"") + "\"";
        final String result = remove(requestPayload, defaultNamespace);

        return result;
    }
}
