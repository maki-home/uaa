package am.ik.home.member;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MemberUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	public MemberUserDetailsService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		Optional<Member> member = username.contains("@")
				? memberRepository.findByEmail(username)
				: memberRepository.findOne(username);
		return member.map(MemberUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("not found"));
	}
}
