package com.example.demo.controller;

import com.example.demo.entity.MailStatus;
import com.example.demo.form.MailForm;
import com.example.demo.entity.Mail;
import com.example.demo.repository.MailRepo;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.MailService;
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
    private final MailRepo mailRepo;
    private final UserRepo userRepo;

    @GetMapping("/admin/mails")
    public String listAttendance(Model model,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "3") int size){

        try {
            List<MailForm> mails = new ArrayList<MailForm>();
            Pageable paging = PageRequest.of(page - 1, size);

            Page<MailForm> pageTuts;
            if (keyword == null) {
//                pageTuts = mailRepo.findAll(paging);
                pageTuts = mailService.findAllMailPaginable(paging);
            } else {
//                pageTuts = mailRepo.findByTitleContainingIgnoreCase(keyword, paging);
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
    public String viewAttendanceForm(@PathVariable int id, Model model) {
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


    @GetMapping("/admin/mail/edit/{id}")
    public String editAttendanceForm(@PathVariable int id, Model model) {
        model.addAttribute("SENT",MailStatus.SENT);
        model.addAttribute("FAILED",MailStatus.FAILED);
        model.addAttribute("PENDING",MailStatus.PENDING);
        model.addAttribute("DRAFT",MailStatus.DRAFT);
        model.addAttribute("mail", mailService.getMailById(id));
        model.addAttribute("mailList", userRepo.getAllEmail());

        return "admin_mail_edit";
    }
    @PostMapping("/admin/mail/edit/{id}")
    public String updateMail(@PathVariable int id,
                                   @Valid @ModelAttribute("mail") MailForm mailForm,
                                   BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "admin_attendance_edit";
        }
        // get student from database by id
        mailForm.setMailId(id);
        mailService.updateMail(mailForm);

//        User exUser = userService.getUserById(id);
//        exUser.setId(id);
//        exUser.setFirstName(user.getFirstName());
//        exUser.setLastName(user.getLastName());
//        exUser.setEmail(user.getEmail());
//        exUser.setMale();
//        // save updated student object
//        userRepo.save(exUser);
        return "redirect:/admin/mails";
    }

}
