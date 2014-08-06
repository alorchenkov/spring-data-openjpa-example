package com.cpwr.gdo.simulator.converter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Component
public class RequestConverter implements
        org.springframework.core.convert.converter.Converter<Map<String, Object>, byte[]> {
    private static final Logger LOG = LoggerFactory.getLogger(RequestConverter.class);

    @Override
    public byte[] convert(final Map<String, Object> parameters) {
        final XStream xstream = new XStream(new DomDriver());
        xstream.alias("parameters", Map.class);
        xstream.registerConverter(new MapEntryConverter());

        final String xml = xstream.toXML(parameters);
        final byte[] request = xml.getBytes();

        LOG.info("parameters: \n {}", xml);

        return request;
    }
}
