package jpabook.jpashop.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.exception.NotEnoughStockException;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceTest {


    @PersistenceContext
    EntityManager em;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 상품주문() throws Exception {
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000 ,10);
        int orderCount =2;

        Long orderId = orderService.order(member.getId(),item.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER,getOrder.getStatus(),"상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1,getOrder.getOrderItems().size(),"상품 종류 수가 정확해야 함.");
        Assertions.assertEquals(10000*2,getOrder.getTotalPrice(),"주문 가격은 가격 * 수량이다.");
        Assertions.assertEquals(8,item.getStockQuantity(),"주문 수량만큼 재고가 줄어야함.");

    }
    @Test
    void 재고수량초과() throws Exception {
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000 ,10);

        int orderCount= 11;

        Assertions.assertThrows(NotEnoughStockException.class,()->orderService.order(member.getId(),item.getId(), orderCount));
    }

    @Test
    void 주문취소() throws Exception {

        Member member = createMember();
        Item item = createBook("시골 JPA", 10000 ,10);
        int orderCount= 2;

        Long orderId = orderService.order(member.getId(),item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);
        Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus());
        Assertions.assertEquals(10, item.getStockQuantity());

    }


    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가" , "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(quantity);
        em.persist(book);
        return book;
    }

}
