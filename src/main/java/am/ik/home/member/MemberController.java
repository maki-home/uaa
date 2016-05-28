package am.ik.home.member;

import am.ik.home.app.App;
import am.ik.home.app.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final AppRepository appRepository;

    @GetMapping("/")
    String home(Model model) {
        List<App> apps = appRepository.findAll();
        List<Member> members = memberRepository.findAll();
        model.addAttribute("apps", apps);
        model.addAttribute("members", members);
        return "home";
    }

    @GetMapping(path = "/", params = "new")
    String newMember(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/new";
    }

    @PostMapping(path = "/", params = "new")
    String newMember(@Validated MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "members/new";
        }
        Member member = new Member();
        BeanUtils.copyProperties(form, member);
        memberService.save(member, form.getRawPassword());
        return "redirect:/";
    }

    @GetMapping(path = "/", params = "edit")
    String editLogin(@AuthenticationPrincipal(expression = "member.memberId") String memberId, Model model) {
        MemberForm form = new MemberForm();
        memberRepository.findOne(memberId).ifPresent(member -> {
            model.addAttribute("member", member);
            BeanUtils.copyProperties(member, form);
        });
        model.addAttribute("memberForm", form);
        return "members/edit";
    }


    @GetMapping(path = "/", params = {"edit", "memberId"})
    String edit(@RequestParam("memberId") String memberId, Model model) {
        return editLogin(memberId, model);
    }

    @PostMapping(path = "/", params = "edit")
    String edit(@RequestParam("memberId") String memberId,
                @Validated MemberForm form, BindingResult result, Model model,
                RedirectAttributes attributes) {
        Member member = memberRepository.findOne(memberId).get();
        if (result.hasErrors()) {
            model.addAttribute("member", member);
            return "members/edit";
        }
        Member updated = new Member();
        BeanUtils.copyProperties(form, updated);
        updated.setMemberId(memberId);
        memberService.save(updated, form.getRawPassword());
        attributes.addFlashAttribute("updated", true);
        attributes.addAttribute("memberId", memberId);
        return "redirect:/?edit";
    }

    @DeleteMapping
    String delete(@RequestParam("memberId") String memberId) {
        memberService.delete(memberId);
        return "redirect:/";
    }

}
