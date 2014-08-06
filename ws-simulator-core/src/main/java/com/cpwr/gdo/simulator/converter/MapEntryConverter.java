package com.cpwr.gdo.simulator.converter;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MapEntryConverter implements Converter {

    public boolean canConvert(final Class clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public void marshal(final Object value, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        Map<String, Object> map = (Map<String, Object>) value;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                writer.startNode(entry.getKey());
                context.convertAnother(entry.getValue());
                writer.endNode();
            } else {
                writer.startNode(entry.getKey());
                writer.setValue(entry.getValue().toString());
                writer.endNode();
            }
        }
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        Map<String, Object> map = new HashMap<String, Object>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.hasMoreChildren()) {
                Map<String, Object> childMap = (Map<String, Object>) context.convertAnother(
                        new HashMap<String, Object>(), Map.class);
                map.put(reader.getNodeName(), childMap);
            } else {
                map.put(reader.getNodeName(), reader.getValue());
            }

            reader.moveUp();
        }
        return map;
    }
}
