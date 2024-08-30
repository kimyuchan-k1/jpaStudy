package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    //@Rollback(false) 를 하면 트랜잭션을 commit 하게 바꾼다. transactional 의 기본값은 rollback 임.

    @Test
    void 회원가입() {
        //given
        Member member = new Member();
        member.setName("kim");


        //when

        Long savedId = memberService.join(member);


        //then
        assertEquals(member,memberRepository.findOne(savedId));
    }

    @Test()
    void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");
        // assertThrows 로 예외 검증을하자.
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.join(member1);
            memberService.join(member2);
        });

    }
}