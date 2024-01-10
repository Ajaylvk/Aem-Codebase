package com.softwareag.core.services.virusscan;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Software AG Virus Scan Service", description = "Virus Scan Service Configuration for the use of ClamAV.")
public @interface VirusScanServiceConfig {

    @AttributeDefinition(name = "ClamAV Host", description = "Host of ClamAV server", defaultValue = "")
    String getClamAvHost();

    @AttributeDefinition(name = "ClamAV Port", description = "Port of ClamAV server", defaultValue = "80")
    int getClamAvPort();

    @AttributeDefinition(name = "ClamAV Timeout", description = "Timeout (in milliseconds) when calling ClamAV", defaultValue = "3000")
    int getClamAvTimeout();
}
