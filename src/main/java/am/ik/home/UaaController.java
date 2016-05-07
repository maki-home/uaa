package am.ik.home;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.json.Json;
import javax.json.JsonObject;

@Controller
public class UaaController {
    @GetMapping("/")
    String home(@AuthenticationPrincipal(expression = "member") Member member, Model model) {
        model.addAttribute("member", member);
        return "home";
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @ResponseBody
    @GetMapping(path = "/user")
    JsonObject user(@AuthenticationPrincipal(expression = "member") Member member) {
        return Json.createObjectBuilder()
                .add("name", Json.createObjectBuilder()
                        .add("givenName", member.getGivenName())
                        .add("familyName", member.getFamilyName()))
                .add("email", member.getEmail())
                .build();
    }
}
