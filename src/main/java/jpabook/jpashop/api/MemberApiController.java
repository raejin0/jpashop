package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController // @Controller, @ResponseBody: 데이터를 json이나 xml에로 보내기 위함
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;

	@GetMapping("/api/v1/members")
	public List<Member> membersV1() {
		return memberService.findMembers();
	}

	@GetMapping("/api/v2/members")
	public Result memberV2() {
		List<Member> findMembers = memberService.findMembers();

		List<MemberDto> collect = findMembers.stream()
				.map(m -> new MemberDto(m.getName()))
				.collect(Collectors.toList());

		return new Result(collect.size(), collect);
	}

	@Data
	@AllArgsConstructor
	static class Result<T> {
		private int count;
		private T data;
	}

	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String name;
	}

	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
		// @RequestBody: json(또는 다른 api)으로 온 body를 Member에 mapping해서 넣어줌
		// @Valid: 해당 클래스에 @NotEmpty 등의 항목들을 체크해준다.

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

		Member member = new Member();
		member.setName(request.getName());

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	// update와 create의 스펙이 다른 경우가 많아서 DTP를 새로 만듬
	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(
			@PathVariable("id") Long id,                        // response DTO for update
			@RequestBody @Valid UpdateMemberRequest request){   // request DTO for update

		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);

		return new UpdateMemberResponse(id, findMember.getName());

	}

	@Data
	static class UpdateMemberRequest {
		private String name;

	}


	/*
	* Entity의 경우 lombok 사용을 최대한 자제하고
	* DTO의 경우 데이터만 왔다 갔다 하는 거기 때문에 막쓴다.
	* */
	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse {
		private Long id;
		private String name;
	}

	@Data
	static class CreateMemberRequest {
		@NotEmpty
		private String name;
	}


	@Data
	static class CreateMemberResponse {
		private Long id;

		public CreateMemberResponse(Long id) {
			this.id = id;
		}
	}

}
