package jpabook.jpashop.domain;


import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {
    private String street;
    private String city;
    private String zipcode;

    // 기본 생성은 막기
    protected Address() {

    }
    // 초기화 하게 하기
    public Address(String street, String city, String zipcode) {
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
    }
}
