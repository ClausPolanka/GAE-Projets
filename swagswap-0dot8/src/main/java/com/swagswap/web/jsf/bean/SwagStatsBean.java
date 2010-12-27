package com.swagswap.web.jsf.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import com.swagswap.domain.SwagStats;
import com.swagswap.service.SwagStatsService;

@ManagedBean(name = "swagStatsBean")
@RequestScoped
public class SwagStatsBean {

	private static final Logger log = Logger.getLogger(SwagStatsBean.class);

	@ManagedProperty(value = "#{swagStatsService}")
	SwagStatsService swagStatsService;

	public void setSwagStatsService(SwagStatsService swagStatsService) {
		this.swagStatsService = swagStatsService;
	}

	private SwagStats swagStats;

	public SwagStats getSwagStats() {
		// Check if stats already available for this request
		if (swagStats == null) {
			swagStats = swagStatsService.getSwagStats();
		}
		return swagStats;
	}

	public void setSwagStats(SwagStats swagStats) {
		this.swagStats = swagStats;
	}
}
