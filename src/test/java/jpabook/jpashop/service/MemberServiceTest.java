package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // 기본값이 롤백이라 커밋을 하지않고 1차 캐시의 값을 조회
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Rollback(false)
    public void 회원가입() throws Exception
    {
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        //? 동일한 트랜잭션에서의 영속성 컨텍스트는 동일성을 보장하기 때문에 id와 멤버 엔티티가 같은 값을 가진다.
        assertThat(memberRepository.findOne(savedId)).isEqualTo(member);
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception
    {
        //given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2); // validate가 터져야함

        //then
        Assertions.fail("예외가 발생해야 한다.");
    }
}