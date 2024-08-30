package jpabook.jpashop.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        // 아이템을 저장할건데. == 아이템의 id 가 비었을 때는 저장, 비지 않으면 == 중복이면 합병
        if(item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    //getResultList 는 가져온 값을 list 로 반환한다.
    public List<Item> findAll() {
        return em.createQuery("select i from Item i",Item.class).getResultList();
    }
}

