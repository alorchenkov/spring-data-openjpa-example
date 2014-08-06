package com.cpwr.gdo.simulator.web.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.util.ContainerUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public final class WebUtils {
    private static final Logger LOG = LoggerFactory.getLogger(WebUtils.class);

    private WebUtils() {

    }

    public static void clearNonHiddenFieldValues(final Form form) {
        checkArgument(form != null, "form must not be null: %s", form);

        LOG.debug("clearNonHiddenFieldValues in from: {}", form.getName());

        final List<Field> fields = ContainerUtils.getInputFields(form);

        for (Field field : fields) {
            if (!(field instanceof HiddenField)) {
                field.setValue(StringUtils.EMPTY);
            }
        }
    }
}
