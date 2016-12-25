package am.ik.home.member;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.stream.Collectors;

public class MemberUserDetails extends User {
    private final Member member;

    public MemberUserDetails(Member member) {
        super(member.getEmail(), member.getPassword(),
                member.getRoles().stream()
                        .map(r -> "ROLE_" + r.name().toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
