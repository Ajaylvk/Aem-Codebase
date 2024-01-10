package com.softwareag.core.models.secondarynav;

import java.util.List;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = { Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public interface SecondaryNavModel {

	@Inject
	String getBackgroundColor();
	
	@Inject
	String getAlignment();
	
	@Inject
	String getNavTitle();
	
	@Inject
	String getNavLink();
	
	@Inject
	List<PrimaryNav> getPrimaryNavList(); // the name `getPrimaryNavList` corresponds to the multifield name="./primaryNavList"

	/**
	 * PrimaryNav model has label, labelLink and a list of ChildNavLists
	 */
	@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	interface PrimaryNav {
		
		@Inject
		String getPrimaryNavLabel();
		
		@Inject
		String getPrimaryNavLink();

		@Inject
		List<ChildNav> getChildNavList(); // the name `getChildNavList` corresponds to the multifield
											// name="./childNavList"
	}

	/**
	 * ChildNav model has label and labelLink
	 */
	@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
	interface ChildNav {

		@Inject
		String getChildNavLabel();
		
		@Inject
		String getChildNavLink();

		
	}
}