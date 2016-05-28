package am.ik.home.member;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends Repository<Member, String> {
    Optional<Member> findOne(String memberId);

    Optional<Member> findByEmail(@Param("email") String email);

    @Query("SELECT x FROM Member x WHERE x.memberId IN (:ids) ORDER BY x.familyName, x.givenName")
    List<Member> findByIds(@Param("ids") List<String> ids);

    @RestResource(exported = false)
    Member save(Member member);

    long countByRoles(MemberRole role);

    @RestResource(exported = false)
    void delete(String memberId);
}
