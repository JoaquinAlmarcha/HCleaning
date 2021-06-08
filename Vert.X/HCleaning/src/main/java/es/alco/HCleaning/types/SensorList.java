package es.alco.HCleaning.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SensorList {

	private List<Sensor> sensorList;

	public SensorList() {
		super();
	}

	public SensorList(Collection<Sensor> sensorList) {
		super();
		this.sensorList = new ArrayList<Sensor>(sensorList);
	}
	
	public SensorList(List<Sensor> sensorList) {
		super();
		this.sensorList = new ArrayList<Sensor>(sensorList);
	}

	public List<Sensor> getSensorList() {
		return sensorList;
	}

	public void setSensorList(List<Sensor> sensorList) {
		this.sensorList = sensorList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sensorList == null) ? 0 : sensorList.hashCode());
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
		SensorList other = (SensorList) obj;
		if (sensorList == null) {
			if (other.sensorList != null)
				return false;
		} else if (!sensorList.equals(other.sensorList))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserEntityListWrapper [userList=" + sensorList + "]";
	}
}
