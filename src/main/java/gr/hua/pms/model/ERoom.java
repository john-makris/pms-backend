package gr.hua.pms.model;

public enum ERoom {
	_1_1(1.1),
	_2_1(2.1),
	_2_2(2.2),
	_3_1(3.1),
	_3_2(3.2),
	_4_1(4.1);
	
	private final double roomId;

	ERoom(double roomId) {
		this.roomId = roomId;
	}

	public double getRoomId() {
		return roomId;
	}
}
