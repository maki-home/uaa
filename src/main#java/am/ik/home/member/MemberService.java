package am.ik.home.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN') or #member.memberId == principal.member.memberId")
    public Member save(@P("member") Member member, String rawPassword) {
        member.setPassword(passwordEncoder.encode(rawPassword));
        memberRepository.save(member);
        if (memberRepository.countByRoles(MemberRole.ADMIN) == 0) {
            throw new IllegalStateException("At least one ADMIN required!");
        }
        return member;
    }
}
