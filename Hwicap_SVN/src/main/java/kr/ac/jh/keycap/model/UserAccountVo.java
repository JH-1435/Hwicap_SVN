package kr.ac.jh.keycap.model;

/** User, Admin 공통 부모클래스 */
public class UserAccountVo {
	private String accountId;  // 공통된 ID 필드

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
}
