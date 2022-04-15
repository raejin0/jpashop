package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Delivery {

	@Id @GeneratedValue
	@Column(name="delivery_id")
	private Long id;

	@JsonIgnore
	@OneToOne(mappedBy="delivery", fetch = LAZY)
	private Order order;

	@Embedded
	private Address address;

	@Enumerated(EnumType.STRING) // Default: ORDI NAL(숫자) ==> 중간에 다른 상태가 들어가면 기존 숫자가 밀리면서 꼬임(망함), 반드시 STRING으로 할 것
  	private DeliveryStatus status; // READY, COMP
}
