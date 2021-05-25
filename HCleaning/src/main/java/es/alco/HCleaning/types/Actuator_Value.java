package es.alco.HCleaning.types;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Actuator_Value {

	private Integer idActuator_Value;
	private Integer mode;
	private String type;
	private Long timestamp;
	private Integer idActuator;
	
	
	public Actuator_Value(@JsonProperty("idActuator_Value") Integer idActuator_Value, @JsonProperty("Mode") Integer mode, 
			@JsonProperty("Type") String type, @JsonProperty("Timestamp") Long timestamp, @JsonProperty("idActuator") Integer idActuator) {
		super();
		this.idActuator_Value = idActuator_Value;
		this.mode = mode;
		this.type = type;
		this.timestamp = timestamp;
		this.idActuator = idActuator;
	}


	public Actuator_Value() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Integer getIdActuator_Value() {
		return idActuator_Value;
	}


	public void setIdActuator_Value(Integer idActuator_Value) {
		this.idActuator_Value = idActuator_Value;
	}


	public Integer getMode() {
		return mode;
	}


	public void setMode(Integer mode) {
		this.mode = mode;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}


	public Integer getIdActuator() {
		return idActuator;
	}


	public void setIdActuator(Integer idActuator) {
		this.idActuator = idActuator;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idActuator == null) ? 0 : idActuator.hashCode());
		result = prime * result + ((idActuator_Value == null) ? 0 : idActuator_Value.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Actuator_Value other = (Actuator_Value) obj;
		if (idActuator == null) {
			if (other.idActuator != null)
				return false;
		} else if (!idActuator.equals(other.idActuator))
			return false;
		if (idActuator_Value == null) {
			if (other.idActuator_Value != null)
				return false;
		} else if (!idActuator_Value.equals(other.idActuator_Value))
			return false;
		if (mode == null) {
			if (other.mode != null)
				return false;
		} else if (!mode.equals(other.mode))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


	
	
	
}
