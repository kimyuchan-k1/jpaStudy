package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // final 인 객체의 생성자 만 만든다. 생성자가 하나면 자동으로 Autowired 됨.
public class MemberService {
    // 데이터의 변경은 무조건 트랜잭션 안에서 수행해야함.


    private final MemberRepository memberRepository;


    /**
     * 회원가입
     */

    // 조인 즉 저장만 읽기가 아니게 된다. == 기본 transactional 을 선언했기 때문.
    @Transactional
    public Long join(Member member)  {

        validateDuplicateMember(member); //중복회원검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findmembers = memberRepository.findByName(member.getName());
        if(!findmembers.isEmpty()) {
            throw new IllegalStateException("Duplicate member found");
        }

    }

    //회원 전체 조회 -- 읽기 전용 임을 명시
    public List<Member> findmembers() {
        return memberRepository.findAll();
    }


    //단건조회
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }


    //변경 감지에 의한 수정 == jpa 가 tansactional 에 의한 commit 하기 전에 변경을 감지하여 update query를 날린다
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
