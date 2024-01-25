package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "ORDERS")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "MEBMER_ID")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY ,cascade = CascadeType.ALL)
    @JoinColumn(name = "DELIVERY_ID", unique = true)
    private Delivery delivery;
    private LocalDateTime orderDate; // 주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    //todo: 연관관계 메서드(양방향)
    public void setMember(Member member) {
        this.member = member; // Order -> member
        member.getOrders().add(this); // member -> order
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); // 컬렉션 추가
        orderItem.setOrder(this); // orderItem -> Order
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery; // order -> delivery
        delivery.setOrder(this); // delivery -> order
    }

    /*생성 메서드(정적 팩토리 메서드라고도 함)*/
    //? 파라미터 생성자보다 더 많은 정보를 전달(주문상태, 주문날짜 등)
    //? new Order()로 인스턴스 생성 없이 바로 메서드 호출 가능
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /*비즈니스 로직*/
    //todo: 주문 취소
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /*조회 로직*/
    //todo: 전체 주문 가격 조회
    public int getTotalPrice() {
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();


        // 컬렉션을 순회하면서 OrderPrice의 총합을 리턴
        /*return orderItems.stream().mapToInt(OrderItem::getOrderPrice).sum();*/
    }
}
