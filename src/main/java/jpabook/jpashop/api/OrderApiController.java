package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

	private final OrderRepository orderRepository;
	private final OrderQueryRepository orderQueryRepository;

	// Direct exposure of entity
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAllByString(new OrderSearch());
		for (Order order : all) {
			order.getMember().getName();
			order.getDelivery().getAddress();
			List<OrderItem> orderItems = order.getOrderItems();
			orderItems.stream().forEach(o -> o.getItem().getName());
		}
		return all;
	}

	// Convert entity into dto
	@GetMapping("/api/v2/orders")
	public List<OrderDto> ordersV2() {
		List<Order> orders = orderRepository.findAllByString(new OrderSearch());
		List<OrderDto> result = orders.stream()
				.map(o -> new OrderDto(o))//  변환하려면 map을 쓴다.
				.collect(Collectors.toList());

		return result;
	}

	/*
	* Optimization using fetch join
	* NOTE
	*  Unnecessary data are selected as a result of query
	*   -> Paging is not possible
	*   -> It might cause bad performance
	* */
	@GetMapping("/api/v3/orders")
	public List<OrderDto> ordersV3() {
		List<Order> orders = orderRepository.findAllWithItem();

		for (Order order : orders) {
			System.out.println("order ref = " + order + order.getId());
		}

		List<OrderDto> result = orders.stream()
				.map(o -> new OrderDto(o)) // 변환하려면 map을 쓴다.
				.collect(Collectors.toList());

		return result;
	}

	/*
	* v3에 비해 쿼리는 여러번 나가지만 불필요한 중복 데이터가 조회되지 않는다.
	* */
	// @BatchSize(size=1000) // 개별 선언 가능
	@GetMapping("/api/v3.1/orders")
	public List<OrderDto> ordersV3_page(
			@RequestParam(value = "offset", defaultValue = "0") int offset,
			@RequestParam(value = "limit", defaultValue = "100") int limit) {

		List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

		List<OrderDto> result = orders.stream()
				.map(o -> new OrderDto(o)) // 변환
				.collect(Collectors.toList());

		return result;
	}

	/*
	* jpa to dto directly
	* 1 + N 문제 발생
	* 매번 쿼리 나가서 성능 떨어짐
	* */
	@GetMapping("/api/v4/orders")
	public List<OrderQueryDto> ordersV4() {
		return orderQueryRepository.findOrderQueryDtos();
	}

	/*
	* collection optimization for jpa to dto directly
	* in query 사용으로 한번에 데이터 가져온 후 메모리에서 값 매칭
	* */
	@GetMapping("/api/v5/orders")
	public List<OrderQueryDto> ordersV5() {
		return orderQueryRepository.findAllByDto_optimization();
	}

	/*
	* flat optimization for jpa to dto directly
	*  Unnecessary data are selected as a result of query
	*   -> Paging result could be something that wasn't expected
	*   -> It might cause bad performance
	* */
	@GetMapping("/api/v6/orders")
	public List<OrderFlatDto> ordersV6() {
		return orderQueryRepository.findAllByDto_flat();
	}


//	@Data
	@Getter
	private class OrderDto {

		private Long orderId;
		private String name;
		private LocalDateTime dorderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDto> orderItems;

		public OrderDto(Order order) {
			orderId = order.getId();
			name = order.getMember().getName();
			dorderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			address = order.getDelivery().getAddress();

			orderItems = order.getOrderItems().stream()
					.map(orderItem -> new OrderItemDto(orderItem))
					.collect(Collectors.toList());
			/*order.getOrderItems().stream().forEach(o -> o.getItem().getName());
			orderItems = order.getOrderItems();*/
		}
	}

	@Getter
	static class OrderItemDto{

		private String itemName;
		private int orderPrice;
		private int count;

		public OrderItemDto(OrderItem orderItem) {

			itemName = orderItem.getItem().getName();
			orderPrice = orderItem.getOrderPrice();
			count = orderItem.getCount();
		}
	}
}
