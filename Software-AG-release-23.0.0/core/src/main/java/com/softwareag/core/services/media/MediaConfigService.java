package com.softwareag.core.services.media;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Service to allow central configuration of media parameters
 */
@Designate(ocd = MediaConfigService.Config.class)
@Component(service = MediaConfigService.class, immediate = true)
public class MediaConfigService {

    @ObjectClassDefinition(name = "Software AG Media Configurations",
                           description = "Configuration of edia parameters")
    public @interface Config {

        @AttributeDefinition(name = "Dynamic Media Image Preset Breakpoints",
                             description = "comma separated list of Dynamic Media image breakpoints (pixels)")
        String dm_breakpoints() default "";
    }

    private String dmBreakpoints;

    @Activate
    protected void activate(final Config config) {
        dmBreakpoints = config.dm_breakpoints();
    }

    /**
     * provides the limit for the the Event Overview Items query
     *
     * @return Dynamic Media breakpoints
     */
    public String getDmBreakpoints() {
        return dmBreakpoints;
    }

}
