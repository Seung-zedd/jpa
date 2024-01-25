package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    // model 역할: 컨트롤러에서 생성된 데이터를 담아 뷰로 전달할 때 사용하는 객체
    public String hello(Model model) {
        model.addAttribute("data", "hello!!!");
        return "hello";
    }
}
