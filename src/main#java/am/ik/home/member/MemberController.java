package am.ik.home.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/")
    String home(@AuthenticationPrincipal(expression = "member") Member member, Model model) {
        model.addAttribute("member", member);
        return "home";
    }

    @GetMapping(path = "/", params = "edit")
    String edit(@AuthenticationPrincipal(expression = "member.memberId") String memberId, Model model) {
        MemberForm form = new MemberForm();
        memberRepository.findOne(memberId).ifPresent(member -> {
            BeanUtils.copyProperties(member, form);
        });
        model.addAttribute("memberForm", form);
        return "edit";
    }

    @PostMapping(path = "/", params = "edit")
    String edit(@AuthenticationPrincipal(expression = "member.memberId") String memberId,
                @Validated MemberForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            return "edit";
        }
        Member updated = new Member();
        BeanUtils.copyProperties(form, updated);
        updated.setMemberId(memberId);
        memberService.save(updated, form.getRawPassword());
        return "redirect:/?edit";
    }

}
