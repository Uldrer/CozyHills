package com.villagesim.actions;

// No index should be the same
public enum BasicAction implements ActionInterface{
	EAT("Eat", 1),
	DRINK("Drink", 2),
	GATHER("Gather", 3),
	MOVE("Move", 4),
	WORK("Work", 5),
	SLEEP("Sleep", 6),
	SOCIALIZE("Socialize", 7);
	
	private final String type;
	private final int index;
	
	private BasicAction(final String type, final int index) {
        this.type = type;
        this.index = index;
    }


	@Override
	public String getActionType() {
		return type;
	}
	
	@Override
	public int getIndex() {
		return index;
	}
}
