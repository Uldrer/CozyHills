package com.villagesim.interfaces;

public interface Depletable {
	void addDepletedListener(DepletedListener listener);
	void fireDepletedEvent(boolean depleted);
}
