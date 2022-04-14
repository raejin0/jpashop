package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.service.ItemService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.ui.Model;

@Getter @Setter
public class BookForm {

	private Long id;
	private String name;
	private int price;
	private int stockQuantity;
	private String author;
	private String isbn;
}
