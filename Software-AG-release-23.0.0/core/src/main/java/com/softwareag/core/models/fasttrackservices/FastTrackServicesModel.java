package com.softwareag.core.models.fasttrackservices;

import com.adobe.cq.wcm.core.components.models.Component;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface FastTrackServicesModel extends Component {
    default String[] getFilter1tags() {
        return null;
    }

    default String[] getFilter2tags() {
        return null;
    }

    default String[] getFilter3tags() {
        return null;
    }

    default String getSourcePath() {
        return null;
    }


}
