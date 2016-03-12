package com.villagesim.actions;

public enum AdvancedAction implements ActionInterface{
	
	// Gathering, add more when needed
	WATER("Gather water", BasicAction.GATHER),
	FISH("Fish", BasicAction.GATHER),
	BERRIES("Gather berries", BasicAction.GATHER),
	HUNT("Hunt", BasicAction.GATHER),
	NUTS("Gather nuts", BasicAction.GATHER),
	WOOD("Gather wood", BasicAction.GATHER),
	
	// Movement, add more when needed
	SWIM("Swim", BasicAction.MOVE),
	RUN("Run", BasicAction.MOVE),
	WALK("Walk", BasicAction.MOVE),
	CLIMB("Climb", BasicAction.MOVE),
	
	// Work, add more when needed
	TRANSPORT_GOODS("Transport goods", BasicAction.WORK),
	BUILD("Build", BasicAction.WORK),
	CUT_TREES("Cut trees", BasicAction.WORK);

	private final String info;
    private final BasicAction basicAction;
	
	private AdvancedAction(String info, BasicAction basicAction) {
        this.info = info;
        this.basicAction = basicAction;
    }
	
	@Override
	public String getActionType() {
		return basicAction.getActionType();
	}

	@Override
	public int getIndex() {
		return basicAction.getIndex();
	}
	
	public String getInfo()
	{
		return info;
	}
	
}
