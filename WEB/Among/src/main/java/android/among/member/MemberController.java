package android.among.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
	
	@RequestMapping(value="/member/insert.do", method=RequestMethod.POST)
	public String insert(String userID, String name, String password, String phone, String birth, String gender) {
		MemberDTO user = new MemberDTO(userID,name,password,phone,birth,gender);
		System.out.println(user);
		//service.insert(user);
		return "member";
	}
	
	@RequestMapping(value="/member/chk.do", method=RequestMethod.POST)
	public String chkID(String userID) {
		System.out.println(userID);
		if(service.chkID(userID)==null) {
			System.out.println("중복되지 않은 ID");
			return "중복되지 않은 ID";
		}else{
			System.out.println("존재하는 ID");
			return "존재하는 ID";
		}
	}
}
