package jpabook.jpashop.domain.item;


import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {


    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //비즈니스 로직   == 안에 가져있기 때문에 응집력 때문에
    // 비지니스 로직을 엔티티안에 직접 구현한다.-- 굳이 밖에서 setter 를이용해서 로직을 구현할 필요는 없음.

    /**
     * 재고 증가
     * @param quantity
     *
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고 수량 감소
     */

    public void removeStock(int quantity) {
        // 만약 스톡 수량 감소 시켰을 때 음의 값이라면? 예외가 발생한다.
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
