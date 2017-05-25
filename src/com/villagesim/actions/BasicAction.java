package com.villagesim.actions;

// No index should be the same
public enum BasicAction implements ActionInterface{
	EAT("Eat", 0),
	DRINK("Drink", 1),
	SLEEP("Sleep", 2),
	SOCIALIZE("Socialize", 3),
	
	// Gathering, add more when needed
	GATHER_WATER("Gather water", 4),
	GATHER_FISH("Fish", 5),
	GATHER_NUTS("Gather nuts", 6),
	GATHER_BERRIES("Gather berries", 7),
	HUNT("Hunt", 8),
	GATHER_WOOD("Gather wood", 9),
	
	// Movement, add more when needed
	WALK_RANDOM("Walk_random", 10),
	RUN("Run", 11),
	SWIM("Swim", 12),
	CLIMB("Climb", 13),
	WALK_DIRECTION_WATER("Walk_water", 14),
	WALK_DIRECTION_WOOD("Walk_wood", 15),
	
	// Work, add more when needed
	TRANSPORT_GOODS("Transport goods", 16),
	CUT_TREES("Cut trees", 17),
	BUILD("Build", 18);
	
	private final String type;
	private int index;
	
	private BasicAction(final String type, final int index) {
        this.type = type;
    }


	@Override
	public String getActionType() {
		return type;
	}
	
	@Override
	public int getIndex() {
		return index;
	}
	
	private void setIndex(int index) {
		this.index = index;
	}
	
	static {
		int counter = 0;
		for(BasicAction action : BasicAction.values())
		{
			action.setIndex(counter);
			counter++;
		}
	}
	
	public static final int size = BasicAction.values().length;
}
