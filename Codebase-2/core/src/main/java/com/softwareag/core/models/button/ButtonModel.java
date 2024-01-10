package com.softwareag.core.models.button;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.softwareag.core.models.download.AssetModel;
import com.softwareag.core.models.download.DownloadModel;
import com.softwareag.core.models.download.DownloadModel.DownloadType;
import com.softwareag.core.util.LinkUtil;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ButtonModel implements ButtonModelImpl {
	
	@Inject
	private String link;
	
	private String openIn;
	
	@PostConstruct
	private void init()
	{
		initAssetModel();
		openIn = LinkUtil.linkOpenIn(link);
	}
	
	@Override
	public String getLink() {
		
		if (assetModel != null) {
			openIn = "_self";
            return assetModel.getUrl();            
        }
		else {
			if (StringUtils.isNotBlank(utmParameter)) {
	            if (!utmParameter.contains("?")) {
	            	return LinkUtil.getLink(link) + "?" + utmParameter;
	            } else {
	            	return LinkUtil.getLink(link) + utmParameter;
	            }
	        }
		}
		return LinkUtil.getLink(link);
	}

	public String getOpenIn() {
		return openIn;
	}
	
	private AssetModel assetModel;
	
	
	
	@SlingObject
    private ResourceResolver resourceResolver;
	
	@Inject
	private boolean inline;
	
	@Inject
	private String utmParameter;
	
	@ScriptVariable
    private ValueMap properties;

    @ValueMapValue
    private String fileReference;
    
    public static final String CONTENT = "/content";
    public static final String CONTENT_DAM = CONTENT + "/dam";
	
	private void initAssetModel() {
        if (link == null) {
            return;
        }

        final String rawHref = link;

        if (StringUtils.startsWith(this.link, CONTENT_DAM)) {
            this.assetModel = buildAssetModel(rawHref);
        } 
        if (assetModel != null && StringUtils.isNotBlank(utmParameter)) {
            if (!utmParameter.contains("?")) {
                assetModel.setUrl(assetModel.getUrl() + "?" + utmParameter);
            } else {
                assetModel.setUrl(assetModel.getUrl() + utmParameter);
            }
        }
    }
	
	private AssetModel buildAssetModel(final String damPath) {
        if (StringUtils.isBlank(damPath)) {
            return null;
        }

        final Resource assetResource = resourceResolver.getResource(damPath);
        if (assetResource == null) {
            return null;
        }

        final AssetModel model = assetResource.adaptTo(AssetModel.class);
        final DownloadType downloadType = inline ? DownloadType.INLINE : DownloadType.DOWNLOAD;
        DownloadModel.assignDownloadUrl(model, DownloadModel.getAssetLastModified(properties, model), downloadType);
        return model;
    }

}
