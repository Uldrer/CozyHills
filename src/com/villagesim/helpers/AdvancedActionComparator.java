package com.villagesim.helpers;

import java.util.Comparator;

import com.villagesim.actions.AdvancedAction;

public class AdvancedActionComparator implements Comparator<AdvancedAction> {

	@Override
	public int compare(AdvancedAction o1, AdvancedAction o2) {
		
		return ((Integer)o1.getRank()).compareTo(o2.getRank());
	}
}
