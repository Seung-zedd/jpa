package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Controller + ResponseBody(JSON 객체 바로 반환)
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    // 엔티티를 Dto로 변경
    @GetMapping("api/v2/members")
    public Result memberV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList()); // List<Member> -> List<MemberDto>로 변경

        // collect는 리스트라 배열 타입으로 나가기 때문에 Result로 감쌈
        //? List<MemberDto>를 JSON 객체로 감싸기 때문에 Result를 래퍼 클래스라고 함
        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("api/v1/members")
    //? @RequestBody: JSON 데이터를 멤버 필드에 모두 매핑
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("api/v2/members")
    //todo: 멤버 엔티티를 @RequestBody로 넘기는 대신, Dto(name)을 넘김
    //api 스펙이 안바뀜, if 필드명이 변경되더라도 컴파일 에러가 나서 파악 가능
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.getName()); // 외부에서 입력한 이름값을 멤버 엔티티에 세팅

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PatchMapping("api/v2/members/{id}")
    //? 등록이랑 수정 api는 다르기 때문에 별도의 객체를 만들었음
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        // 이름을 수정
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }


    @Data // Getter, Setter, toString, HashCode 등...
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
