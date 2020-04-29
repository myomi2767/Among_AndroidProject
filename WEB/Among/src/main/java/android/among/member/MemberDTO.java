package android.among.member;

public class MemberDTO {
	private String userID;
    private String password;
    private String name;
    private String phone;
    private String birth;
    private String gender;
    
    public MemberDTO() {
    	
    }
    
	public MemberDTO(String userID, String password) {
		super();
		this.userID = userID;
		this.password = password;
	}
	
	public MemberDTO(String userID, String password, String name, String phone, String birth, String gender) {
		super();
		this.userID = userID;
		this.password = password;
		this.name = name;
		this.phone = phone;
		this.birth = birth;
		this.gender = gender;
	}
	
	@Override
	public String toString() {
		return "MemberDTO [userID=" + userID + ", password=" + password + ", name=" + name + ", phone=" + phone
				+ ", birth=" + birth + ", gender=" + gender + "]";
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
}
