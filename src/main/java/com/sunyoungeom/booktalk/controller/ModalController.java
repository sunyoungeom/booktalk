package com.sunyoungeom.booktalk.controller;

import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.exception.UserException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ModalController {
    @GetMapping("/edit/{field}")
    public String getModal(Model model, @PathVariable(name = "field")  String field) {
        model.addAttribute("fieldValue", field);
        if (field.equals("nickname")) {
        model.addAttribute("field", "닉네임");
        } else if (field.equals("password")) {
            model.addAttribute("field", "비밀번호");
        } else {
            throw new UserException(UserErrorCode.IMMUTABLE_USER_FIELD.getMessage());
        }
        return "fragments/modal :: modal";
    }
}
