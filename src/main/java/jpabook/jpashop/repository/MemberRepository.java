package jpabook.jpashop.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {


    // 엔티티 매니저 주입
    private final EntityManager em;

    // db 에서 필요한 정보들으 뽑는 기능들을 구현

    public void save(Member member) {
        em.persist(member);

    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        // 전체 코드 반환 -- jql 사용 -- jql 은 엔티티를 대상으로 진행함. -> sql 로 바꿔서 실행
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }


    // parameter 바인딩은 다음과 같이 한다. :param   그리고  .setParameter("param",param) 을 해주어야함.
    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m  where m.name = :name",Member.class)
                .setParameter("name", name)
                .getResultList();
    }



}
