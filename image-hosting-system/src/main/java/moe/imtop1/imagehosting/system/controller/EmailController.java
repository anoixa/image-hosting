package moe.imtop1.imagehosting.system.controller;

import moe.imtop1.imagehosting.system.domain.vo.ValidateCodeVo;
import moe.imtop1.imagehosting.system.service.IEmailService;
import moe.imtop1.imagehosting.system.service.impl.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shuomc
 */
@RestController
@RequestMapping("/email")
public class EmailController {
}
