package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

	@Id @GeneratedValue
	@Column(name="member_id")
	private Long id;

	@NotEmpty
	private String name;

	@Embedded  // 클래스 선언 쪽에 Embeddable이나 사용하는 곳에 @Embedded 둘 중 하나만 해줘도 무방하다.
	private Address address;

	@JsonIgnore
	@OneToMany(mappedBy = "member")
	private List<Order> orders = new ArrayList<>();



}
