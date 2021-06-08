package es.alco.HCleaning.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {

	private Integer idDevice;
	private String ip;
	private String name;
	private Integer idRoom;
	
	
	public Device(@JsonProperty("idDevice") Integer idDevice, @JsonProperty("IP") String ip, 
			@JsonProperty("Name") String name, @JsonProperty("idRoom") Integer idRoom){
		super();
		this.idDevice = idDevice;
		this.ip = ip;
		this.name = name;
		this.idRoom = idRoom;
	}


	public Device() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Integer getIdDevice() {
		return idDevice;
	}


	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Integer getIdRoom() {
		return idRoom;
	}


	public void setIdRoom(Integer idRoom) {
		this.idRoom = idRoom;
	}


	@Override
	public String toString() {
		return "Device [idDevice=" + idDevice + ", ip=" + ip + ", name=" + name + ", idRoom=" + idRoom + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDevice == null) ? 0 : idDevice.hashCode());
		result = prime * result + ((idRoom == null) ? 0 : idRoom.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Device other = (Device) obj;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (idRoom == null) {
			if (other.idRoom != null)
				return false;
		} else if (!idRoom.equals(other.idRoom))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
}
