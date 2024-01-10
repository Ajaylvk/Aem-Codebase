package com.softwareag.core.models.media;

import com.softwareag.core.services.media.MediaConfigService;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.regex.Pattern;


@Model(adaptables = Resource.class)
public class MediaConfigModel {

    private static final Pattern BREAKPOINTS_PATTERN = Pattern.compile("^[0-9,]+$");

    @Inject
    MediaConfigService mediaConfigService;

    private String breakpoints;

    @ValueMapValue
    @Optional
    private String disableBreakpoints;

    @PostConstruct
    private void initModel() {
        if (mediaConfigService != null) {
            String breakpointsConfig = mediaConfigService.getDmBreakpoints();
            if (breakpointsConfig != null && breakpointsConfig.matches(String.valueOf(BREAKPOINTS_PATTERN))) {
                breakpoints = breakpointsConfig;
            }
        }
    }

    public String getBreakpoints() {
        return breakpoints;
    }

    public String getDisableBreakpoints() {
        return disableBreakpoints;
    }
}
