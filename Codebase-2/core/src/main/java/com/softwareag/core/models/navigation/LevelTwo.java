package com.softwareag.core.models.navigation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = false)
public class LevelTwo {
	
	@Setter @Getter
	private String levelTwoTitle;
	@Setter @Getter
	private String levelTwoDestination;
	
	@Setter @Getter
	private String levelTwoLinkOpenIn;
	

}
