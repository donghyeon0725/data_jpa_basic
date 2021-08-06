package study.data_jpa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.domain.Member;
import study.data_jpa.domain.Team;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.repository.MemberRepository;
import study.data_jpa.repository.TeamRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class DataJPATest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long countAfterDelete = memberRepository.count();
        assertThat(countAfterDelete).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAge() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("BBB", 15);

        assertThat(result.get(0)).isEqualTo(m2);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findByUsername() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> result = memberRepository.findByUsername("BBB");

        assertThat(result.get(0)).isEqualTo(m2);
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    public void findUser() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(m1);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);


        List<String> result = memberRepository.findUsernameList();

        for (String s : result)
            System.out.println("s = " + s);
    }

    @Test
    public void findMemberdto() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<MemberDto> result = memberRepository.findMemberDto();

        for (MemberDto memberDto : result)
            System.out.println("memberDto = " + memberDto);
    }

    @Test
    public void findMemberByName() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member2", 20);
        Member member3 = new Member("member3", 30);
        Member member4 = new Member("member4", 40);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> result = memberRepository.findByNames(Arrays.asList("member1", "member2"));

        for (Member member : result)
            System.out.println("member = " + member);
    }


    @Test
    public void findTeamByName() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);


        List<Team> result1 = teamRepository.findTeamsByName("teamA");
        Team result2 = teamRepository.findTeamByName("teamA");
        Team result3 = teamRepository.findOptTeamByName("teamA").get();


        assertEquals(result1.get(0), teamA);
        assertEquals(result2, teamA);
        assertEquals(result3, teamA);
    }


    @Test
    @DisplayName("spring data 페이징 구현")
    public void findByAge() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC, "username") );
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
    }


    @Test
    @DisplayName("spring data Slice 구현")
    public void findSliceByAge() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC, "username") );
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
    }

    @Test
    @DisplayName("spring data count 쿼리 분리")
    public void findCountQueryByAge() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC, "username") );
        Page<Member> page = memberRepository.findPageWithQueryByAge(age, pageRequest);
        List<Member> content = page.getContent();
        long count = page.getTotalElements();
    }

    @Test
    @DisplayName("벌크 업데이트")
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> members = memberRepository.findByUsername("member5");
        System.out.println(members.get(0));
        assertThat(resultCount).isEqualTo(4);
    }


    @Test
    @DisplayName("QueryGraph")
    public void QueryGraph() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAtOnceAll();

        for (Member member : members)
            System.out.println("member = " + member);
    }

    @Test
    @DisplayName("EntityGraph")
    public void EntityGraph() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members)
            System.out.println("member = " + member);
    }



    @Test
    @DisplayName("EntityGraph")
    public void EntityGraph2() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findWithEntityGraphAll();

        for (Member member : members)
            System.out.println("member = " + member);
    }


    @Test
    @DisplayName("EntityGraph")
    public void EntityGraph3() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 20));
        memberRepository.save(new Member("member3", 30));
        memberRepository.save(new Member("member4", 40));
        memberRepository.save(new Member("member5", 50));

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findWithEntityGraphByUsername("member1");

        for (Member member : members)
            System.out.println("member = " + member);
    }


    @Test
    @DisplayName("jpa hint")
    public void jpaHint() {
        memberRepository.save(new Member("member1", 10));

        em.flush();
        em.clear();

        Member member = memberRepository.findReadOnyByUsername("member1");
        member.setUsername("test");
    }
}
