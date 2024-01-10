package com.softwareag.core.models.article;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.softwareag.core.util.LinkUtil;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleEmphasisModel {

	 @ValueMapValue
	 private String emphasisText;
	 
	 @ValueMapValue
	 private String enableLinks;
	 
	 @ValueMapValue
	 private String ctaLabel;
	 
	 @ValueMapValue
	 private String ctaLink;
	 
	 @ValueMapValue
	 private String fileReference;
	 
	 @Setter @Getter
	 private String ctaDestinationLink;
	 
	@Setter @Getter
	private String linkOpenIn;
	
	
	@PostConstruct
	private void init() {
		ctaDestinationLink = LinkUtil.getLink(ctaLink);
		linkOpenIn = LinkUtil.linkOpenIn(ctaLink);
	}
	 
}
