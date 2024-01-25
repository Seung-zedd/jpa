package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.service.UpdateItemDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item{

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //todo: form -> Dto -> Entity
    public void updateItem(UpdateItemDto itemDto) {
        //! itemDto로 현재 엔티티의 id값을 변경하면 JPA 데이터 무결성에 어긋남
        this.name = itemDto.getName();
        this.price = itemDto.getPrice();
        this.stockQuantity = itemDto.getStockQuantity();
    }


    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    /*비즈니스 로직*/
    //todo: stock 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    //todo: stock 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
