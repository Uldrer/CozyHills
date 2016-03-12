package com.villagesim.actions;

public enum AdvancedAction implements ActionInterface{
	
	// Food gathering, add more when needed
	FISH("Fish", BasicAction.GATHER_FOOD),
	BERRIES("Gather berries", BasicAction.GATHER_FOOD),
	HUNT("Hunt", BasicAction.GATHER_FOOD),
	NUTS("Gather nuts", BasicAction.GATHER_FOOD),
	
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
