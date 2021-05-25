package es.alco.HCleaning.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

	private Integer idUser;
	private String dni;
	private String name;
	private String address;
	private String email;
	private String tlf;
	private String password;
	
	public User(@JsonProperty("idUser") Integer idUser, @JsonProperty("DNI") String dni, 
			@JsonProperty("Name") String name, @JsonProperty("Address") String address,
			@JsonProperty("Email") String email, @JsonProperty("Tlf") String tlf, @JsonProperty("Password") String password) {
		super();
		this.idUser = idUser;
		this.dni = dni;
		this.name = name;
		this.address = address;
		this.email = email;
		this.tlf = tlf;
		this.password = password;
	}
	

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public Integer getIduser() {
		return idUser;
	}

	public void setIduser(Integer iduser) {
		this.idUser = iduser;
	}

	public String getDNI() {
		return dni;
	}

	public void setDNI(String dni) {
		this.dni = dni;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTlf() {
		return tlf;
	}

	public void setTlf(String tlf) {
		this.tlf = tlf;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	@Override
	public String toString() {
		return "UserImpl [iduser=" + idUser + ", dni=" + dni + ", name=" + name + ", address=" + address + ", email="
				+ email + ", tlf=" + tlf + ", password=" + password + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((dni == null) ? 0 : dni.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((idUser == null) ? 0 : idUser.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((tlf == null) ? 0 : tlf.hashCode());
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
		User other = (User) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (dni == null) {
			if (other.dni != null)
				return false;
		} else if (!dni.equals(other.dni))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (idUser == null) {
			if (other.idUser != null)
				return false;
		} else if (!idUser.equals(other.idUser))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (tlf == null) {
			if (other.tlf != null)
				return false;
		} else if (!tlf.equals(other.tlf))
			return false;
		return true;
	}

	

}
