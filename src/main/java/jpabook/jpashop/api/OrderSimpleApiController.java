package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    // 다음과 같이 아예 다 가져오는 것으로 짠다면?
    // 엔티티 직접 노출 -- 양방향관계 발생 -> JsonIgnore -- 수많은 조인한 데이터들이 반환된다.

    @GetMapping("/api/v1/simple-orders")
    public List<Order> getSimpleOrders() {

        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // LAZY 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }



    //api 스펙에 맞추어 dto 를 개발하여 반환한다.
    @GetMapping("api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

    // 패치 조인으로 쿼리 1번 호출하는 메서드 생성.
    @GetMapping("api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(order -> new SimpleOrderDto(order))
                .collect(toList());
        return result;

    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private Address address;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }

    }
}
