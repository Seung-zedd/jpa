package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //todo: 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장(OrderItem과 Delivery에 CASCADE가 걸려서 영속성 전이)
        //! Order가 단일소유자이고, 라이프싸이클이 동일하므로 CASCADE를 사용
        //! 비즈니스 요구사항에 따른 다른 엔티티가 참조를 하면 CASCADE를 사용하면 안됨!
        orderRepository.save(order);

        return order.getId();
    }

    //todo: 주문취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel(); // 변경된 사항을 JPA가 더티체킹(알아서 업데이트쿼리 날림)
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        // 조회는 리포지토리로 위임
        return orderRepository.findAllByString(orderSearch);
    }

}
