package jpabook.jpashop.api;


import jakarta.validation.Valid;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;


    /* 최악의 경우 --회원의 정보를 보고 싶은 건데 엔티티에 대한 모든 정보가 다 노출되는 문제가 생김.
     JsonIgnore 어노테이션으로 엔티티의 변수를 무시할 수 있지만 다른 api 호출에서도 똒같이 적용되는 문제가 생김.
     엔티티를 직접 반환하면 안됨. (결론)*/
    // 또한 array 의 형태로 반환하게 됨. -- 스펙이 굳어버리고 확장할 수 없음.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findmembers();
    }


    @GetMapping("/api/v2/members")
    public Result membersV2() {
        // 1. 엔티티 가져오기
        List<Member> findMembers = memberService.findmembers();
        //엔티티 -> DTO 로 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        // collect 를 이용하여 list 형식으로 반환 .  왜냐? 배열 형태로 나오기 때문.
        return new Result(collect);


    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    //requestbody 에서 json 으로 온 데이터를 member 객체로 반환해줌. -- v1 처럼 엔티티를 직접 파라미터로 받는 경우... api스펙
    //api 스펙도 바뀔 수 있음. -- 치명적 단점
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

    }

    // 엔티티 대신 DTO 를 매핑하는 경우
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName());

        memberService.join(member);
        return new CreateMemberResponse(member.getId());
    }

    /**
     * 수정 api
     */
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        // 업데이트의 경우는  아무 값도 반환하지 않는 것을 선호함. -- why?? 영속성 콘텍스트를 가져오는 것에 대한 단점??
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(),findMember.getName());

    }



    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor // 모든 파라미터를가진 생성자를 자동생성한다.
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    // request : 요청은 이름 하나만 받는 api 스펙에 맞는 dto를 따로 설계한다.
    @Data
    static class CreateMemberRequest {
        private String name;
    }

    //응답값
    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }


    }
}
