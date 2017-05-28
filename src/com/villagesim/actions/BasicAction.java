package com.villagesim.actions;

// No index should be the same
public enum BasicAction implements ActionInterface{
	EAT("Eat"),
	DRINK("Drink"),
	SLEEP("Sleep"),
	/*
	SOCIALIZE("Socialize"),
	*/
	// Gathering, add more when needed
	GATHER_WATER("Gather water"),
	GATHER_FISH("Fish"),
	GATHER_NUTS("Gather nuts"),
	GATHER_BERRIES("Gather berries"),
	HUNT("Hunt"),
	GATHER_WOOD("Gather wood"),
	
	// Movement, add more when needed
	WALK_RANDOM("Walk_random"),
	RUN("Run"),
/*
	SWIM("Swim"),
	CLIMB("Climb"),*/
	WALK_DIRECTION_WATER("Walk_water"),
	WALK_DIRECTION_WOOD("Walk_wood"),
	WALK_DIRECTION_FISH("Walk_fish");
	
/*
	// Work, add more when needed
	TRANSPORT_GOODS("Transport goods"),
	CUT_TREES("Cut trees"),
	BUILD("Build");
*/
	
	private final String type;
	private int index;
	
	private BasicAction(final String type) {
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
