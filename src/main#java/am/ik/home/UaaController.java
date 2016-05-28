package am.ik.home;

import am.ik.home.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.json.Json;
import javax.json.JsonObject;

@Controller
@RequiredArgsConstructor
public class UaaController {

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @ResponseBody
    @GetMapping(path = "/user")
    JsonObject user(@AuthenticationPrincipal(expression = "member") Member member) {
        return Json.createObjectBuilder()
                .add("id", member.getMemberId())
                .add("name", Json.createObjectBuilder()
                        .add("givenName", member.getGivenName())
                        .add("familyName", member.getFamilyName()))
                .add("email", member.getEmail())
                .build();
    }
}
