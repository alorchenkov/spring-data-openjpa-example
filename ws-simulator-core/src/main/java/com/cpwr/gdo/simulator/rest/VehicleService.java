package com.cpwr.gdo.simulator.rest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cpwr.gdo.simulator.converter.RequestConverter;
import com.cpwr.gdo.simulator.service.SimulatorService;

@Controller
@RequestMapping("api")
public class VehicleService {
    private static final Logger LOG = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    private SimulatorService simulatorService;

    @Autowired
    private RequestConverter requestConverter;

    @RequestMapping("vehicle/{vin}")
    @ResponseBody
    public String getVehicleByVin(@PathVariable String vin) {
        LOG.debug("getByVin params: vin={}", vin);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("vin", vin);

        final byte[] request = requestConverter.convert(parameters);

        final String responsePayload = simulatorService.getResponsePayload(VehicleService.class.getSimpleName(),
                request);

        LOG.debug("payload: {}", responsePayload);

        return responsePayload;
    }

}
