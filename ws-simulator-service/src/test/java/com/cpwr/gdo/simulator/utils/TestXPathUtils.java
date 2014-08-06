package com.cpwr.gdo.simulator.utils;

import static com.cpwr.gdo.simulator.utils.XPathUtils.checkIfRuleIsApplicable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import com.cpwr.gdo.simulator.UnitTestBase;

/**
 * The Class TestXPathUtils.
 *
 */
public final class TestXPathUtils extends UnitTestBase {
    private static final Logger LOG = Logger.getLogger(TestXPathUtils.class.getName());
    /** The request payload. */
    private String requestPayload;

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        requestPayload = "<ns3:GetWarrantyRequest " + " xmlns:ns2=\"http://schema.gm.com/GIF/GSSM/GWM/Warranty/Impl\""
                + " xmlns=\"http://schema.gm.com/GIF/GSSM/GWM/Warranty/TransactionHeader\""
                + " xmlns:ns3=\"http://wsdl.gm.com/GIF/GSSM/GWM/Warranty/Impl\">"
                + "<TransactionHeader><Action>GET_WARRANTY</Action>" + "<MessageID>Dynamic</MessageID>"
                + "<SourceApplication>NGDOE</SourceApplication>" + "<TargetApplication>GWM</TargetApplication>"
                + "<TargetComponent>GWM warranty</TargetComponent>" + "</TransactionHeader>" + "<ns2:Warranty>"
                + "<ns2:VIN>1G4HD57M29U129307</ns2:VIN>" + "<ns2:Odometer MilageUOM=\"K\">10000</ns2:Odometer>"
                + "</ns2:Warranty></ns3:GetWarrantyRequest>";
    }

    /**
     * Test check if rule is applicable logical.
     */
    @Test
    public void testCheckIfRuleIsApplicableLogical() {
        /**
         * this expression works only with the full path:
         * /GetWarrantyRequest/Warranty it doesn't work with relative path like
         * //Warranty
         * */
        final String expression = "count(/GetWarrantyRequest/Warranty[VIN='1G4HD57M29U129307'])=1";

        boolean isApplicable = checkIfRuleIsApplicable(expression, requestPayload);
        assertTrue(isApplicable);

        final String expr = "count(/GetWarrantyRequest/Warranty[VIN='1G4HD57M29U129311'])=1";
        isApplicable = checkIfRuleIsApplicable(expr, requestPayload);

        assertFalse(isApplicable);

    }

    /**
     * Test check if rule is applicable value.
     */
    @Test
    public void testCheckIfRuleIsApplicableValue() {
        final String expression = "//Warranty/VIN";
        final String value = "1G4HD57M29U129307";

        boolean isApplicable = checkIfRuleIsApplicable(expression, value, requestPayload);
        assertTrue(isApplicable);

        final String val = "1G4HD57M29U129311";
        isApplicable = checkIfRuleIsApplicable(expression, val, requestPayload);

        assertFalse(isApplicable);
    }

    @Test
    public void testCheckIfRuleIsApplicableValue2() {
        requestPayload = "<ns3:VDUProgramEligibilityRequest xmlns:ns2=\"http://www.onstar.com/wsdl/vdu/Y2007/M08/V2\" xmlns:ns3=\"http://www.onstar.com/wsdl/vdu/VDUProfileProgramService/Y2007/M08/V2\">   <ns2:vin>1G4HD57M29U129307</ns2:vin>   <ns2:programName>OVD</ns2:programName></ns3:VDUProgramEligibilityRequest>";
        final String expression = "//VDUProgramEligibilityRequest/vin";
        final String value = "1G4HD57M29U129307";

        boolean isApplicable = checkIfRuleIsApplicable(expression, value, requestPayload);
        assertTrue(isApplicable);

        final String val = "1G4HD57M29U129311";
        isApplicable = checkIfRuleIsApplicable(expression, val, requestPayload);

        assertFalse(isApplicable);
    }

    @Override
    public Logger getLog() {
        return LOG;
    }

    @Test
    public void testCheckIfRuleIsApplicableLogical2() {
        /**
         * this expression works only with the full path:
         * /GetWarrantyRequest/Warranty it doesn't work with relative path like
         * //Warranty
         * */

        requestPayload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<ns3:getDrivingHistoryInfo xmlns:ns2=\"http://asd.onstar.com/partnerservices/ps\" xmlns:ns3=\"http://www.onstar.com/partnerservices/2010/04/v1\">\n"
                + "   <arg0>\n" + "       <ns2:context>\n"
                + "           <ns2:appSessionKey>3fc642f3-2328-3ad0-be4a-d423e4ea6771</ns2:appSessionKey>\n"
                + "           <ns2:vin>KL8CL6S02EC400130</ns2:vin>\n" + "       </ns2:context>\n"
                + "       <ns2:baseDate>2013-05-20-04:00</ns2:baseDate>\n" + "       <ns2:period>year</ns2:period>\n"
                + "   </arg0>\n" + "</ns3:getDrivingHistoryInfo>";

        final String expression = "/getDrivingHistoryInfo/arg0/context/vin[contains(., 'KL8CL6S02EC400130')] and /getDrivingHistoryInfo/arg0/period[contains(., 'year')]";
        LOG.info(requestPayload);
        boolean isApplicable = checkIfRuleIsApplicable(expression, requestPayload);
        assertTrue(isApplicable);

    }

    @Test
    public void testCheckIfRuleIsApplicableForTwoParams() {

        requestPayload = "<parameters><model>GM</model><vin>1977</vin></parameters>";

        final String expression = "/parameters[vin='1977'] or /parameters[model='GM1']";

        boolean isApplicable = checkIfRuleIsApplicable(expression, requestPayload);
        assertTrue(isApplicable);

    }
}
