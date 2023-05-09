package vn.core.accountant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaymentCalculationSettingController {

    @GetMapping(value = "/payment-calculation")
    public String config(Model model) {
        return "payment-calculation";
    }
}
