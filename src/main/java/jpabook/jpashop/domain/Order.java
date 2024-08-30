package jpabook.jpashop.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자의 옵션을 protected 로생성한다. 하는이유는? 다른 누군가가 생성하는 것을 막는다.
public class Order {


    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();



    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    // === 연관관계 메서드 ====
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }


    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
    }


    // 주문은 여러 관계가 얽히고 얽혀있음. == 그래서 생성메서드를 만들어주는 것이 좋음.
    // 생성 메서드 !!!!! -- 주문 생성시 사용된다.
    public static Order createOrder(Member member, Delivery delivery,OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /** 비지니스 로직 */

    // 주문 취소
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("Delivery has already been cancelled");
        }

        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    // 조회 로직
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }

        return totalPrice;
    }

}
