package am.ik.home.app;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping(path = "apps")
@RequiredArgsConstructor
public class AppController {
    private final AppRepository appRepository;

    @GetMapping(params = "new")
    String newApp(Model model) {
        model.addAttribute("app", new App());
        return "apps/new";
    }

    @PostMapping(params = "new")
    String newApp(@Validated App app, BindingResult result) {
        if (result.hasErrors()) {
            return "apps/new";
        }
        app.setAppId(UUID.randomUUID().toString());
        app.setAppSecret(UUID.randomUUID().toString());
        appRepository.save(app);
        return "redirect:/";
    }

    @GetMapping(params = "edit")
    String edit(@RequestParam("appId") String appId, Model model) {
        App app = appRepository.findOne(appId);
        model.addAttribute("app", app);
        return "apps/edit";
    }

    @PostMapping(params = "edit")
    String edit(@RequestParam("appId") String appId,
                @RequestParam(name = "regenerateSecret", defaultValue = "false") boolean regenerateSecret,
                @Validated App app, BindingResult result,
                RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "apps/edit";
        }
        app.setAppId(appId);
        if (regenerateSecret) {
            app.setAppSecret(UUID.randomUUID().toString());
        }
        appRepository.save(app);
        attributes.addAttribute("appId", appId);
        return "redirect:/apps?edit";
    }

    @DeleteMapping
    String delete(@RequestParam("appId") String appId) {
        appRepository.delete(appId);
        return "redirect:/";
    }
}
