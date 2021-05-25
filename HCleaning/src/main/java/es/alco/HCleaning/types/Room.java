package es.alco.HCleaning.types;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Room {

	private Integer idRoom;
	private Long LastAccess;
	
	public Room(@JsonProperty("idRoom") Integer idRoom, @JsonProperty("LastAccess") Long lastAccess) {
		super();
		this.idRoom = idRoom;
		this.LastAccess = lastAccess;
	}
	
	public Room() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getIdRoom() {
		return idRoom;
	}

	public void setIdRoom(Integer idRoom) {
		this.idRoom = idRoom;
	}

	public Long getLastAccess() {
		return LastAccess;
	}

	public void setLastAccess(Long lastAccess) {
		LastAccess = lastAccess;
	}

	@Override
	public String toString() {
		return "Room [idRoom=" + idRoom + ", LastAccess=" + LastAccess + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((LastAccess == null) ? 0 : LastAccess.hashCode());
		result = prime * result + ((idRoom == null) ? 0 : idRoom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (LastAccess == null) {
			if (other.LastAccess != null)
				return false;
		} else if (!LastAccess.equals(other.LastAccess))
			return false;
		if (idRoom == null) {
			if (other.idRoom != null)
				return false;
		} else if (!idRoom.equals(other.idRoom))
			return false;
		return true;
	}

	
	
	
}
