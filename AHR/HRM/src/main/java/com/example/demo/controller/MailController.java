package com.example.demo.controller;

import com.example.demo.entity.MailStatus;
import com.example.demo.form.MailForm;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final UserService userService;

    @GetMapping("/admin/mails")
    public String listMails(Model model,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "3") int size){

        try {
            List<MailForm> mails = new ArrayList<MailForm>();
            Pageable paging = PageRequest.of(page - 1, size);

            Page<MailForm> pageTuts;
            if (keyword == null) {
                pageTuts = mailService.findAllMailPaginable(paging);
            } else {
                pageTuts = mailService.findByTitleContainingIgnoreCase(keyword, paging);
                model.addAttribute("keyword", keyword);
            }

            mails = pageTuts.getContent();

            model.addAttribute("mails", mails);
            model.addAttribute("currentPage", pageTuts.getNumber() + 1);
            model.addAttribute("totalItems", pageTuts.getTotalElements());
            model.addAttribute("totalPages", pageTuts.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("SENT", MailStatus.SENT);
            model.addAttribute("PENDING",MailStatus.PENDING);
            model.addAttribute("FAILED",MailStatus.FAILED);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

//        model.addAttribute("users",userService.getAllUser());
        return "admin_mails";
    }

    @GetMapping("/admin/mail/view/{id}")
    public String viewMailForm(@PathVariable int id, Model model) {
        model.addAttribute("SENT",MailStatus.SENT);
        model.addAttribute("FAILED",MailStatus.FAILED);
        model.addAttribute("PENDING",MailStatus.PENDING);
        model.addAttribute("DRAFT",MailStatus.DRAFT);

        model.addAttribute("mail", mailService.getMailById(id));
        return "admin_mail_view";
    }
    @GetMapping("/admin/mail/delete/{id}")
    public String deleteMail(@PathVariable int id) {
        mailService.deleteMail(id);
        return "redirect:/admin/mails";
    }
    @GetMapping("admin/mail/insert")
    public String insertMail(Model model){
        MailForm mailForm = new MailForm();
        model.addAttribute("mailForm", mailForm);
        model.addAttribute("mailList", userService.getAllEmail());
        return "admin_mail_add";
    }
    @PostMapping("/admin/mail/insert")
    public String saveMail(@Valid @ModelAttribute("mailForm") MailForm mailForm,
                                 BindingResult bindingResult)  {
//        List<Attendance> newObj = attendanceRepo.findByEmailAndDate(attendance.getUser().getEmail(), attendance.getDateCheck());
//        if(!newObj.isEmpty()){
//
////            ObjectError error = new ObjectError("attendance", "Đã tồn tại chấm công này");
//            bindingResult.rejectValue("dateCheck", "Đã tồn tại chấm công này");
////            bindingResult.addError(error);
//        }

        if(bindingResult.hasErrors()){

            return "redirect:/admin/mail/insert";
        }

        mailService.saveMail(mailForm);
        return "redirect:/admin/mails";

    }

    @GetMapping("/admin/mail/edit/{id}")
    public String editMailForm(@PathVariable int id, Model model) {
        model.addAttribute("SENT",MailStatus.SENT);
        model.addAttribute("FAILED",MailStatus.FAILED);
        model.addAttribute("PENDING",MailStatus.PENDING);
        model.addAttribute("DRAFT",MailStatus.DRAFT);
        model.addAttribute("mail", mailService.getMailById(id));
        model.addAttribute("mailList", userService.getAllEmail());

        return "admin_mail_edit";
    }
    @PostMapping("/admin/mail/edit/{id}")
    public String updateMail(@PathVariable int id,
                                   @Valid @ModelAttribute("mail") MailForm mailForm,
                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "admin_mail_edit";
        }
        mailForm.setMailId(id);
        mailService.updateMail(mailForm);

        return "redirect:/admin/mails";
    }
    @PostMapping("/admin/mail/draft")
    public String draftMail( @Valid @ModelAttribute("mail") MailForm mailForm,
                             @RequestParam(value = "id") int id ) {
        mailForm.setMailId(id);
        mailService.draftMail(mailForm);

        return "redirect:/admin/mails";
    }

}
