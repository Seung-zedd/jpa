package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    private int orderPrice;
    private int count;

    /*생성 메서드*/
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        //todo: 주문상품의 수량이 증가된 만큼 재고수량은 감소해야 된다.
        item.removeStock(count);

        return orderItem;
    }

    /*비즈니스 로직*/
    //todo: 재고수량을 원상복귀
    public void cancel() {
        getItem().addStock(count);
    }

    /*조회 로직*/
    //todo: 주문가격 * 수량
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
