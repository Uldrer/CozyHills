package com.villagesim.actions;

// No index should be the same
public enum BasicAction implements ActionInterface{
	EAT("Eat", 0),
	DRINK("Drink", 1),
	GATHER("Gather", 2),
	MOVE("Move", 3),
	WORK("Work", 4),
	SLEEP("Sleep", 5),
	SOCIALIZE("Socialize", 6);
	
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
	
	public static final int size = BasicAction.values().length;
}
