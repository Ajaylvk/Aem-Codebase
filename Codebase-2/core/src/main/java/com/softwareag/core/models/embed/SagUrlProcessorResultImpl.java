package com.softwareag.core.models.embed;

import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.Map;

public class SagUrlProcessorResultImpl implements UrlProcessor.Result {
    private final String processor;
    private final Map<String, Object> options;

    public SagUrlProcessorResultImpl(final String processor, final Map<String, Object> options) {
        this.processor = processor;
        this.options = options;
    }

    @Override
    public String getProcessor() {
        return processor;
    }

    @Override
    public Map<String, Object> getOptions() {
        if (MapUtils.isEmpty(options)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(options);
    }

}
