package gr.hua.pms.service;

import java.util.List;

import gr.hua.pms.model.Room;

public interface RoomService {
	public List<Room> findAll();

	public Room findRoomByRoomIdentifier(int roomIdentifier);
	
	public List<Room> createRooms();
}