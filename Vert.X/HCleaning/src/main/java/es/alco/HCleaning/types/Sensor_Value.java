package es.alco.HCleaning.types;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensor_Value {

	private Integer idSensor_Value;
	private float value;
	private Integer idSensor;
	private Long timestamp;
	
	public Sensor_Value(@JsonProperty("idSensor_Value") Integer idSensor_Value, @JsonProperty("Value") float value, 
			@JsonProperty("Timestamp") Long timestamp, @JsonProperty("idSensor") Integer idSensor) {
		super();
		this.idSensor_Value = idSensor_Value;
		this.value = value;
		this.timestamp = timestamp;
		this.idSensor = idSensor;
	}

	public Sensor_Value() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public Integer getIdSensor_Value() {
		return idSensor_Value;
	}

	public void setIdSensor_Value(Integer idSensor_Value) {
		this.idSensor_Value = idSensor_Value;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Integer getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Sensor_Value [idSensor_Value=" + idSensor_Value + ", value=" + value + ", idSensor=" + idSensor
				+ ", timestamp=" + timestamp + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idSensor == null) ? 0 : idSensor.hashCode());
		result = prime * result + ((idSensor_Value == null) ? 0 : idSensor_Value.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + Float.floatToIntBits(value);
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
		Sensor_Value other = (Sensor_Value) obj;
		if (idSensor == null) {
			if (other.idSensor != null)
				return false;
		} else if (!idSensor.equals(other.idSensor))
			return false;
		if (idSensor_Value == null) {
			if (other.idSensor_Value != null)
				return false;
		} else if (!idSensor_Value.equals(other.idSensor_Value))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	

}