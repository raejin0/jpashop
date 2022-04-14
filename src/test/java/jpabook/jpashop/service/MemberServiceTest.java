package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

	@Autowired MemberService memberService;
	@Autowired MemberRepository memberRepository;
	@Autowired EntityManager em; // dml(Insert) 확인하기 위함, 실제 DB에는 안들어감

	@Test
//	@Rollback(false) //DB에 값 넣기
	public void 회원가입() throws Exception {
	    // given
		Member member = new Member();
		member.setName("Kim");
	    
	    // when
		Long savedId = memberService.join(member);

		// then
		em.flush(); // dml(Insert) 확인하기 위함, 실제 DB에는 안들어감
		assertEquals(member, memberRepository.findOne(savedId));

	}
	
	@Test(expected = IllegalStateException.class)
	public void 중복_회원_예약() throws Exception {
	    // given
	    Member member1 = new Member();
	    member1.setName("kim");

        Member member2 = new Member();
	    member2.setName("kim");

	    // when
		memberService.join(member1);
		memberService.join(member2); //예외가 발생해야 한다!

	    // then
		fail("예외가 발생해야 한다. ");
	}
}