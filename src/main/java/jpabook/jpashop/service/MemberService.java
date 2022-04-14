package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //메모리를 적게 할당하여 부하를 최소화 할 수 있음
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 회원가입
	 */
	@Transactional
	public Long join(Member member) {
		validateDulplicateMember(member); //중복 회원 검증
		memberRepository.save(member);
		return member.getId();
	}

	private void validateDulplicateMember(Member member) {
		//EXCEPTION
		List<Member> findMembers = memberRepository.findByName(member.getName());
		if (!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}

	}

	//회원 전체 조회
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}
	public Member findOne(Long id) {
		return memberRepository.findOne(id);
	}


	/**
	 * return 으로 member를 넘길 경우
	 * command와 query가 같이 있는 꼴이 된다. -> return 시 Member를 다시 조회하는 꼴이 된다.
	 * command와 query를 철저히 분리하자!! -> 유지보수성 증대
	 *
	 */
	@Transactional
	public void update(Long id, String name) {
		Member member = memberRepository.findOne(id);
		member.setName(name);
	}
}
