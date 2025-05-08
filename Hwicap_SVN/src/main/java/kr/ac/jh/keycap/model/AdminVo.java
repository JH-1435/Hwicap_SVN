package kr.ac.jh.keycap.model;

public class AdminVo extends UserAccountVo {
	String adminId;
	String adminPw;
	String adminName;
	int adminState;
	
	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
		setAccountId(adminId);  // adminId를 accountId로 설정
	}

	public String getAdminPw() {
		return adminPw;
	}

	public void setAdminPw(String adminPw) {
		this.adminPw = adminPw;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public int getAdminState() {
		return adminState;
	}

	public void setAdminState(int adminState) {
		this.adminState = adminState;
	}
	
}
