package es.alco.HCleaning.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Actuator {

	private Integer idActuator;
	private String name;
	private Integer idDevice;
	
	
	public Actuator(@JsonProperty("idActuator") Integer idActuator, @JsonProperty("Name") String name,
			@JsonProperty("idDevice") Integer idDevice) {
		super();
		this.idActuator = idActuator;
		this.name = name;
		this.idDevice = idDevice;
	}
	
	public Actuator() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Integer getIdActuator() {
		return idActuator;
	}
	public void setIdActuator(Integer idActuator) {
		this.idActuator = idActuator;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getIdDevice() {
		return idDevice;
	}
	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}
	@Override
	public String toString() {
		return "Actuator [idActuator=" + idActuator + ", name=" + name + ", idDevice=" + idDevice
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idActuator == null) ? 0 : idActuator.hashCode());
		result = prime * result + ((idDevice == null) ? 0 : idDevice.hashCode());
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
		Actuator other = (Actuator) obj;
		if (idActuator == null) {
			if (other.idActuator != null)
				return false;
		} else if (!idActuator.equals(other.idActuator))
			return false;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
}
