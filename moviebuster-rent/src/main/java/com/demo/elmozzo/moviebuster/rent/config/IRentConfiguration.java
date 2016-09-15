package com.demo.elmozzo.moviebuster.rent.config;

public interface IRentConfiguration {

	double getPremiumPrice();

	void setPremiumPrice(double premiumPrice);

	double getBasicPrice();

	void setBasicPrice(double basicPrice);

	int getRegularDays();

	void setRegularDays(int regularDays);

	int getOldDays();

	void setOldDays(int oldDays);

	int getPremiumBonus();

	void setPremiumBonus(int premiumBonus);

	int getBasicBonus();

	void setBasicBonus(int basicBonus);

}
