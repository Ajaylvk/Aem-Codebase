package com.softwareag.core.models.embed;

import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Designate(ocd = SagUrlProcessor.Config.class)
@Component(service = UrlProcessor.class, immediate = true)
public class SagUrlProcessor implements UrlProcessor {

    protected static final String NAME = "genericUrl";

    protected static final String URL_ID = "originalUrl";

    private final List<Pattern> allowedUrlPatterns = new ArrayList<>();

    @Activate
    public void activate(final Config config) {
        if (ArrayUtils.isNotEmpty(config.allowedUrlPatterns())) {
            for (String allowedUrlPattern : config.allowedUrlPatterns()) {
                final Pattern pattern = Pattern.compile(allowedUrlPattern, Pattern.CASE_INSENSITIVE);
                allowedUrlPatterns.add(pattern);
            }
        } else {
            allowedUrlPatterns.clear();
        }
    }

    @Override
    public Result process(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        for (Pattern pattern : allowedUrlPatterns) {
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put(URL_ID, url);
                return new SagUrlProcessorResultImpl(NAME, resultMap);
            }
        }

        return null;
    }

    @ObjectClassDefinition(name = "Software AG Allowed Url Patterns for embedding", description = "List of all maintainable url regex patterns.")
    public @interface Config {

        @AttributeDefinition(name = "AllowedUrlPatterns",
                             description = "Allowed url regex patterns",
                             type = AttributeType.STRING)
        String[] allowedUrlPatterns() default {};
    }
}
