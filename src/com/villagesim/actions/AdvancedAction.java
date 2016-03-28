package com.villagesim.actions;

public enum AdvancedAction implements ActionInterface{
	
	// Gathering, add more when needed
	WATER("Gather water", BasicAction.GATHER, 0),
	FISH("Fish", BasicAction.GATHER, 1),
	NUTS("Gather nuts", BasicAction.GATHER, 2),
	BERRIES("Gather berries", BasicAction.GATHER, 3),
	HUNT("Hunt", BasicAction.GATHER, 4),
	WOOD("Gather wood", BasicAction.GATHER, 5),
	
	// Movement, add more when needed
	WALK("Walk", BasicAction.MOVE, 0),
	RUN("Run", BasicAction.MOVE, 1),
	SWIM("Swim", BasicAction.MOVE, 2),
	CLIMB("Climb", BasicAction.MOVE, 3),
	
	// Work, add more when needed
	TRANSPORT_GOODS("Transport goods", BasicAction.WORK, 0),
	CUT_TREES("Cut trees", BasicAction.WORK, 1),
	BUILD("Build", BasicAction.WORK, 2);
	

	private final String info;
    private final BasicAction basicAction;
    private final int rank;
	
	private AdvancedAction(String info, BasicAction basicAction, int rank) {
        this.info = info;
        this.basicAction = basicAction;
        this.rank = rank;
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
	
	public int getRank()
	{
		return rank;
	}
	
}
