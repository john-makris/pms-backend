package gr.hua.pms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gr.hua.pms.model.ERoom;
import gr.hua.pms.model.Room;
import gr.hua.pms.repository.RoomRepository;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    @Autowired
	RoomRepository roomRepository;
	
	@Override
	public List<Room> findAll() {
		try {
			List<Room> roomList = new ArrayList<Room>();
			roomList = roomRepository.findAll();
			if (roomList.isEmpty()) {
				roomList = createRooms();
			}
			System.out.println("ROOMS: "+roomList);
			return roomList;
		} catch(Exception ex) {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public Room findRoomByRoomIdentifier(int roomIdentifier) {
		return roomRepository.findByRoomIdentifier(roomIdentifier).
				orElseThrow(() -> new RuntimeException("Error: Room not found"));
	}

	@Override
	public List<Room> createRooms() {
		List<Room> roomList = new ArrayList<Room>();
		roomList.add(new Room(ERoom._1_1.getRoomIdentifier()));
		roomList.add(new Room(ERoom._2_1.getRoomIdentifier()));
		roomList.add(new Room(ERoom._2_2.getRoomIdentifier()));
		roomList.add(new Room(ERoom._3_1.getRoomIdentifier()));
		roomList.add(new Room(ERoom._3_2.getRoomIdentifier()));
		roomList.add(new Room(ERoom._4_1.getRoomIdentifier()));
		roomRepository.saveAll(roomList);
		return roomList;
	}

}
