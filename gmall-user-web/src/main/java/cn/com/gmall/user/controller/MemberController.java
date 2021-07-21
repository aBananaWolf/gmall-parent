package cn.com.gmall.user.controller;

import cn.com.gmall.beans.UmsMember;
import cn.com.gmall.service.user.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberController {
    //    @Reference
    private MemberService memberService;

    @GetMapping("/getMembers")
    public List<UmsMember> getMembers() {
        return memberService.getMembers();
    }
}
