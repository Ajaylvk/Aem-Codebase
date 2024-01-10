package com.softwareag.core.models.newsandevents;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NewsAndEventsResponse {
	
	@Setter @Getter
	private List<cardBean> cardBean;
	@Setter @Getter
	private String cardType;
	@Setter @Getter
	private String error_type;
	@Setter @Getter
	private String status;
	@Setter @Getter
	private int size;

}
