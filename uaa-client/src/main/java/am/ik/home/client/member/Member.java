package am.ik.home.client.member;

import java.io.Serializable;
import java.util.List;

public class Member implements Serializable {
	private String memberId;
	private String givenName;
	private String familyName;
	private String email;
	private List<MemberRole> roles;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<MemberRole> getRoles() {
		return roles;
	}

	public void setRoles(List<MemberRole> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "Member{" + "memberId='" + memberId + '\'' + ", givenName='" + givenName
				+ '\'' + ", familyName='" + familyName + '\'' + ", email='" + email + '\''
				+ ", roles=" + roles + '}';
	}
}
