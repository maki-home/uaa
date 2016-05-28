package am.ik.home;

import am.ik.home.member.Member;
import am.ik.home.member.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MemberRepositoryTests {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testFindByEmail() {
        Member member = Member.builder()
                .email("foo@example.com")
                .familyName("Yamada")
                .givenName("Taro").build();
        entityManager.persist(member);
        Optional<Member> m = memberRepository.findByEmail("foo@example.com");
        assertThat(m.isPresent()).isTrue();
        assertThat(m.get()).isEqualTo(member);
    }


    @Test
    public void testFindByIds() {
        Member member1 = Member.builder()
                .email("foo@example.com")
                .familyName("Yamada")
                .givenName("Taro").build();
        Member member2 = Member.builder()
                .email("bar@example.com")
                .familyName("Yamada")
                .givenName("Hanako").build();
        entityManager.persist(member1);
        entityManager.persist(member2);
        List<Member> m = memberRepository.findByIds(Arrays.asList(member1.getMemberId(), member2.getMemberId()));
        assertThat(m).hasSize(2);
        assertThat(m.get(0)).isEqualTo(member2);
        assertThat(m.get(1)).isEqualTo(member1);
    }

    @Configuration
    static class Conf {
        @Bean
        ObjectPostProcessor postProcessor() {
            return Mockito.mock(ObjectPostProcessor.class);
        }
    }
}

