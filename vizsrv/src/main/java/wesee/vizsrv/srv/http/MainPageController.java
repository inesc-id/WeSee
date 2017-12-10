package wesee.vizsrv.srv.http;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainPageController {
    @RequestMapping("/greeting")
    public String index() {
        return "privet";
    }
}
