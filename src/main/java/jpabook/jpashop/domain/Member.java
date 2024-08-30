package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //validation 에서 사용하는 문법들을 익혀두자. -- 다음과 같이 엔티티는 db 를 위해 사용하고 api 스펙을 위한 별도의 dto를 설계하자.
    @NotEmpty
    private String name;

    @Embedded
    private Address address;


    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<Order>();
}
