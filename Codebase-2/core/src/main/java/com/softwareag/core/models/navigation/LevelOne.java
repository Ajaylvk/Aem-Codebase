package com.softwareag.core.models.navigation;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class LevelOne {


	@Setter @Getter
	private String levelOneTitle;

	@Setter @Getter
	private String levelOneDestination;
	
	@Setter @Getter
	private String levelOneLinkOpenIn;

	@Setter @Getter
	private String enablePrimaryPromo;

	@Setter @Getter
	private String enableSecondaryPromo;

	@Setter @Getter
	private String primaryPromoHeading;

	@Setter @Getter
	private String primaryPromoCaption;

	@Setter @Getter
	private String primaryPromoDescription;

	@Setter @Getter
	private String primaryPromoLinkLabel;

	@Setter @Getter
	private String primaryPromoLinkDestination;
	
	@Setter @Getter
	private String primaryPromoLinkOpenIn;

	@Setter @Getter
	private String primaryPromoReference;
	
	@Setter @Getter
	private String altTextPrimary;

	@Setter @Getter
	private String secondaryPromoReference;
	
	@Setter @Getter
	private String altTextSecondary;

	@Setter @Getter
	private String secondaryPromoHeading;

	@Setter @Getter
	private String secondaryPromoCaption;

	@Setter @Getter
	private String secondaryPromoDescription;

	@Setter @Getter
	private String secondaryPromoLinkLabel;

	@Setter @Getter
	private String secondaryPromoLinkDestination;
	
	@Setter @Getter
	private String secondaryPromoLinkOpenIn;

	@Setter @Getter
	private List<LevelTwo> levelTwo;

	@Setter @Getter
	private int levelTwoListSize;
	
}
