package es.alco.HCleaning.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensor {
	
	private Integer idSensor;
	private String name;
	private Integer idDevice;
	
	public Sensor(@JsonProperty("idSensor") Integer idSensor, @JsonProperty("Name") String name,
			@JsonProperty("idDevice") Integer idDevice) {
		super();
		this.idSensor = idSensor;
		this.name = name;
		this.idDevice = idDevice;
	}


	public Sensor() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Integer getIdSensor() {
		return idSensor;
	}


	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
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
		return "SensorImpl [idSensor=" + idSensor + ", name=" + name + ", idDevice=" + idDevice
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDevice == null) ? 0 : idDevice.hashCode());
		result = prime * result + ((idSensor == null) ? 0 : idSensor.hashCode());
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
		Sensor other = (Sensor) obj;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (idSensor == null) {
			if (other.idSensor != null)
				return false;
		} else if (!idSensor.equals(other.idSensor))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	
	
	
	
}
