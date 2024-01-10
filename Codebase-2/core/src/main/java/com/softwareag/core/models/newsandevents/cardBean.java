package com.softwareag.core.models.newsandevents;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class cardBean {

	@Setter @Getter
	private String title;
	
	@Setter @Getter
	private String last_posted_at;
	
	@Setter @Getter
	private String url;
	
	@Setter @Getter
	private String start_date;
	
	
}
