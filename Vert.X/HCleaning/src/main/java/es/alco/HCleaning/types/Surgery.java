package es.alco.HCleaning.types;



import com.fasterxml.jackson.annotation.JsonProperty;

public class Surgery {

	private Integer idSurgery;
	private Long timestampStart;
	private Long timestampEnd;
	private Integer idRoom;
	
	public Surgery(@JsonProperty("idSurgery") Integer idSurgery,@JsonProperty("TimestampStart") Long timestampStart,
			@JsonProperty("TimestampEnd") Long timestampEnd, @JsonProperty("idRoom") Integer idRoom) {
		super();
		this.idSurgery = idSurgery;
		this.timestampStart = timestampStart;
		this.timestampEnd = timestampEnd;
		this.idRoom = idRoom;
	}


	public Surgery() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Integer getIdSurgery() {
		return idSurgery;
	}


	public void setIdSurgery(Integer idSurgery) {
		this.idSurgery = idSurgery;
	}


	public Long getTimestampStart() {
		return timestampStart;
	}


	public void setTimestampStart(Long timestampStart) {
		this.timestampStart = timestampStart;
	}


	public Long getTimestampEnd() {
		return timestampEnd;
	}


	public void setTimestampEnd(Long timestampEnd) {
		this.timestampEnd = timestampEnd;
	}


	public Integer getIdRoom() {
		return idRoom;
	}


	public void setIdRoom(Integer idRoom) {
		this.idRoom = idRoom;
	}


	@Override
	public String toString() {
		return "Surgery [idSurgery=" + idSurgery + ", timestampStart=" + timestampStart + ", timestampEnd="
				+ timestampEnd + ", idRoom=" + idRoom + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idRoom == null) ? 0 : idRoom.hashCode());
		result = prime * result + ((idSurgery == null) ? 0 : idSurgery.hashCode());
		result = prime * result + ((timestampEnd == null) ? 0 : timestampEnd.hashCode());
		result = prime * result + ((timestampStart == null) ? 0 : timestampStart.hashCode());
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
		Surgery other = (Surgery) obj;
		if (idRoom == null) {
			if (other.idRoom != null)
				return false;
		} else if (!idRoom.equals(other.idRoom))
			return false;
		if (idSurgery == null) {
			if (other.idSurgery != null)
				return false;
		} else if (!idSurgery.equals(other.idSurgery))
			return false;
		if (timestampEnd == null) {
			if (other.timestampEnd != null)
				return false;
		} else if (!timestampEnd.equals(other.timestampEnd))
			return false;
		if (timestampStart == null) {
			if (other.timestampStart != null)
				return false;
		} else if (!timestampStart.equals(other.timestampStart))
			return false;
		return true;
	}

	



	
	
	
}
