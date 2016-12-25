package am.ik.home;

import am.ik.home.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UaaController {

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @ResponseBody
    @GetMapping(path = "/userinfo")
    Object user(@AuthenticationPrincipal(expression = "member") Member member) {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", member.getMemberId());
        user.put("email", member.getEmail());
        Map<String, Object> name = new LinkedHashMap<>();
        name.put("givenName", member.getGivenName());
        name.put("familyName", member.getFamilyName());
        user.put("name", name);
        return user;
    }
}
