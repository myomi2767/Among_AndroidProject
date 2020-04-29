package android.among.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {
	@Autowired
	MemberService service;
	@RequestMapping(value="/member/login", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	public String login() {
		return "member/login";
	}
	
	@RequestMapping(value="/member/insert", method=RequestMethod.GET, produces="application/json;charset=utf-8")
	public @ResponseBody String insert(MemberDTO user) {
		System.out.println(user);
		service.insert(user);
		return "";
	}
}
