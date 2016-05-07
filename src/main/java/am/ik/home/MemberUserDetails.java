package am.ik.home;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class MemberUserDetails extends User {
    private final Member member;

    public MemberUserDetails(Member member) {
        super(member.getEmail(), member.getPassword(), AuthorityUtils
                .createAuthorityList("ROLE_USER"));
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
