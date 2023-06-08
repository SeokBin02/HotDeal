package challenge18.hotdeal.cicdtest;

import challenge18.hotdeal.domain.product.dto.SelectProductResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "index";
    }
}
