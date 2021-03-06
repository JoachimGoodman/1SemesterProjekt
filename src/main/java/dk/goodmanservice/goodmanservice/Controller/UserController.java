package dk.goodmanservice.goodmanservice.Controller;

import dk.goodmanservice.goodmanservice.Model.User;
import dk.goodmanservice.goodmanservice.Service.IService;
import dk.goodmanservice.goodmanservice.Service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
public class UserController {

    @Autowired
    private IService<User> US;

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public String login(@ModelAttribute User user, HttpSession session, Model model) {
        try {
            if (loginService.login(user)) {
                session.setAttribute("email", user.getEmail());
                session.setAttribute("firstname", user.getFirstName());
                session.setAttribute("lastname", user.getLastName());
                session.setAttribute("phone", user.getPhoneNumber());
                session.setAttribute("role", user.getRoleName());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("id", user.getId());
                session.setAttribute("level", user.getLevel());

                if (user.getRoleName().equals("customer")) {
                    return "redirect:/dashboard/customer";
                } else {
                    return "redirect:/dashboard/employee";
                }

            } else {
                session.invalidate();
                return "index";
            }
        } catch (SQLException e) {
            model.addAttribute("errorCode", e.getErrorCode());
            return "error";
        }
    }

    @PostMapping("/dashboard/brugere/create")
    public String opretBruger(@ModelAttribute User user, Model model) {
        try {
            model.addAttribute("message", US.create(user));
        } catch (SQLException e) {
            model.addAttribute("errorCode", e.getErrorCode());
            return "error";
        }
        return "redirect:/dashboard/brugere";
    }

    @GetMapping("/dashboard/brugere/edit/{id}")
    public String retBrugerForm(@PathVariable("id") int id, Model model) {
        try {
            model.addAttribute("user", US.findById(id));
            model.addAttribute("users", US.fetch("all"));
            model.addAttribute("roles", US.fetch("roles"));
            model.addAttribute("edit", true);
        } catch (SQLException e) {
            model.addAttribute("errorCode", e.getErrorCode());
            return "error";
        }
        return "/dashboard/brugere";
    }

    @PostMapping("/dashboard/brugere/edit/")
    public String retBruger(@ModelAttribute User user, RedirectAttributes model) {
        System.out.println(user.getRid());
        try {
            model.addFlashAttribute("user", US.edit(user));
            model.addFlashAttribute("edit", false);
        } catch (SQLException e) {
            model.addAttribute("errorCode", e.getErrorCode());
            return "error";
        }
        return "redirect:/dashboard/brugere";
    }

    @GetMapping("/dashboard/brugere/delete/{id}")
    public String sletBruger(@PathVariable("id") int id, Model model) {
        try {
           US.delete(id);
        } catch (SQLException e) {
            model.addAttribute("errorCode", e.getErrorCode());
            return "error";
        }
        return "redirect:/dashboard/brugere";
    }

    @GetMapping("/dashboard/brugere")
    public String brugere(Model model) {
        try {
            model.addAttribute("users", US.fetch("all"));
            model.addAttribute("roles", US.fetch("roles"));
            model.addAttribute("edit", false);
        } catch (SQLException e) {
            model.addAttribute("errorCode", e.getErrorCode());
            return "error";
        }
        return "dashboard/brugere";
    }





}
