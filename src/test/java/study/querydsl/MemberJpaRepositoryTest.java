package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;
import study.querydsl.repository.MemberJpaRepository;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

	@Autowired
	EntityManager em;
	
	@Autowired
	MemberJpaRepository memberJpaRepository;
	
	@BeforeEach
	public void before() {
		Team teamA = new Team("오");
		Team teamB = new Team("위");
		em.persist(teamA);
		em.persist(teamB);
		
		Member member1 = new Member("여몽", 40, teamA);
		Member member2 = new Member("육손", 20, teamA);
		Member member3 = new Member("장합", 42, teamB);
		Member member4 = new Member("학소", 22, teamB);
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}
	
	@Test
	public void basicTest() {
		Member member = new Member("하후돈", 30);
		memberJpaRepository.save(member);
		
		Member findMember = memberJpaRepository
						.findById(member.getId())
						.orElse(new Member("재야장수"));
		
		assertThat(findMember).isEqualTo(member);
		
		List<Member> result1 = memberJpaRepository.findAllQuerydsl();
		assertThat(result1).containsExactly(member);
		
		List<Member> result2 = memberJpaRepository.findByUsernameQuerydsl("하후돈");
		assertThat(result2).containsExactly(member);
	}
	
	@Test
	public void searchTest() {
		
		MemberSearchCondition condition = new MemberSearchCondition();
		condition.setAgeGoe(21);
		condition.setAgeLoe(25);
		condition.setTeamName("위");
		// condition이 없으면 findAll쿼리가 실행된다.
		List<MemberTeamDto> result = memberJpaRepository.searchByBuilder(condition);
		
		assertThat(result).extracting("username").containsExactly("학소");
		
		List<MemberTeamDto> result2 = memberJpaRepository.search(condition);
		assertThat(result2).extracting("username").containsExactly("학소");
	}
}
