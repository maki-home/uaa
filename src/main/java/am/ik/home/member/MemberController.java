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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        model.addAttribute("apps", apps);
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
    String edit(@AuthenticationPrincipal(expression = "member") Member member,
                @Validated MemberForm form, BindingResult result, Model model,
                RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("memberForm", form);
            return "edit";
        }
        Member updated = new Member();
        BeanUtils.copyProperties(form, updated);
        updated.setMemberId(member.getMemberId());
        if (member.getRoles().contains(MemberRole.ADMIN)) {
            updated.setRoles(member.getRoles());
        }
        memberService.save(updated, form.getRawPassword());
        attributes.addFlashAttribute("updated", true);
        return "redirect:/?edit";
    }

}
