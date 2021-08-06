package study.data_jpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.domain.Member;
import study.data_jpa.repository.MemberJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class JPATest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long countAfterDelete = memberJpaRepository.count();
        assertThat(countAfterDelete).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAge() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);


        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

        assertThat(result.get(0)).isEqualTo(m2);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByUsername() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);


        List<Member> result = memberJpaRepository.findByUsername("BBB");

        assertThat(result.get(0)).isEqualTo(m2);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByAgePaging() {

        memberJpaRepository.save(new Member("member1", 20));
        memberJpaRepository.save(new Member("member2", 20));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 20));
        memberJpaRepository.save(new Member("member5", 20));
        memberJpaRepository.save(new Member("member6", 20));

        int age = 20;
        int offset = 1;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByAgePaging(age, offset, limit);

        for (Member member : members)
            System.out.println("member = " + member);
    }

    @Test
    @DisplayName("벌크 업데이트")
    public void bulkUpdate() {
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 20));
        memberJpaRepository.save(new Member("member3", 30));
        memberJpaRepository.save(new Member("member4", 40));
        memberJpaRepository.save(new Member("member5", 50));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(4);
    }

}
