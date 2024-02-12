package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.service.UpdateItemDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter // 테스트코드 이슈
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends Item {

    private String author;
    private String isbn;

    // 엔티티와 관련된 로직은 여기서 처리
    public void updateBook(UpdateItemDto itemDto) {
        super.updateItem(itemDto); // Item 공통 속성 업데이트
        this.author = itemDto.getAuthor();
        this.isbn = itemDto.getIsbn();
    }
}
