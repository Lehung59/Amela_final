package com.example.demo.controller;

import com.example.demo.constant.Constants;
import com.example.demo.entity.Mail;
import com.example.demo.entity.MailStatus;
import com.example.demo.form.MailForm;
import com.example.demo.form.SearchForm;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
    private final UserService userService;

    @GetMapping("/admin/mails")
    public String listMails(Model model,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(defaultValue = Constants.PAGE) int page,
                            @RequestParam(defaultValue = Constants.SIZE) int size) {

        try {
            List<MailForm> mails = new ArrayList<MailForm>();
            Pageable paging = PageRequest.of(page - 1, size, Sort.by("mailId").descending());

            Page<MailForm> pageTuts;
            if (keyword == null) {
                pageTuts = mailService.findAllMailPaginable(paging);
            } else {
                pageTuts = mailService.findByTitleContainingIgnoreCase(keyword, paging);
                model.addAttribute("keyword", keyword);
            }

            mails = pageTuts.getContent();
            SearchForm searchForm = SearchForm.builder()
                    .keyword(keyword)
                    .size(size)
                    .page(page)
                    .build();
            model.addAttribute("searchForm", searchForm);
            model.addAttribute("mails", mails);
            model.addAttribute("currentPage", pageTuts.getNumber() + 1);
            model.addAttribute("totalItems", pageTuts.getTotalElements());
            model.addAttribute("totalPages", pageTuts.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("SENT", MailStatus.SENT);
            model.addAttribute("PENDING", MailStatus.PENDING);
            model.addAttribute("FAILED", MailStatus.FAILED);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

//        model.addAttribute("users",userService.getAllUser());
        return "admin_mails";
    }

    @GetMapping("/admin/mail/view/{id}")
    public String viewMailForm(@PathVariable int id, Model model) {
        model.addAttribute("SENT", MailStatus.SENT);
        model.addAttribute("FAILED", MailStatus.FAILED);
        model.addAttribute("PENDING", MailStatus.PENDING);
        model.addAttribute("DRAFT", MailStatus.DRAFT);
        model.addAttribute("mail", mailService.getMailById(id));
        return "admin_mail_view";
    }

    @DeleteMapping("/admin/mail/delete/{id}")
    public ResponseEntity<?> deleteMail(@PathVariable int id) {
        try{
            mailService.deleteMail(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete mail: " + e.getMessage());
        }
    }

    @GetMapping("admin/mail/insert")
    public String insertMail(Model model) {
        MailForm mailForm = new MailForm();
        model.addAttribute("mailForm", mailForm);
        model.addAttribute("mailList", userService.getAllEmail());
        return "admin_mail_add";
    }

    @PostMapping("/admin/mail/insert")
    public String saveMail(@Valid @ModelAttribute("mailForm") MailForm mailForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors= new HashMap<>();

            bindingResult.getFieldErrors().forEach(
                    error -> errors.put(error.getField(), error.getDefaultMessage())
            );
            redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());

            return "redirect:/admin/mail/insert";
        }

        mailService.saveMail(mailForm);

        return "redirect:/admin/mails";

    }
    @GetMapping("/admin/mail/insert/re")
    public String insertMailRe(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Your email has been saved");

        return "redirect:/admin/mails";
    }


    @GetMapping("/admin/mail/edit/{id}")
    public String editMailForm(@PathVariable int id, Model model,RedirectAttributes redirectAttributes) {
        MailForm mailForm = mailService.getMailById(id);

        model.addAttribute("SENT", MailStatus.SENT);
        model.addAttribute("FAILED", MailStatus.FAILED);
        model.addAttribute("PENDING", MailStatus.PENDING);
        model.addAttribute("DRAFT", MailStatus.DRAFT);
        model.addAttribute("mail", mailForm);
        model.addAttribute("mailList", userService.getAllEmail());
        if (mailForm.getStatus().equals(MailStatus.SENT)) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, "Your email has been sent");
            return "redirect:/admin/mail/view/" + id;
        }
        return "admin_mail_edit";
    }

    @PostMapping("/admin/mail/edit/{id}")
    public String updateMail(@PathVariable int id,
                             @Valid @ModelAttribute("mail") MailForm mailForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors= new HashMap<>();

            bindingResult.getFieldErrors().forEach(
                    error -> errors.put(error.getField(), error.getDefaultMessage())
            );
            redirectAttributes.addFlashAttribute(Constants.ERROR, errors.values());

            return "redirect:/admin/mail/edit/" + id;
        }
        MailForm mailInDb = mailService.getMailById(id);
        if (mailInDb.getStatus().equals(MailStatus.SENT)) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, "Your email has been sent");
            return "redirect:/admin/mail/view/" + id;
        }
        mailForm.setMailId(id);
        mailService.updateMail(mailForm);
        redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Email edited successfully");
        return "redirect:/admin/mails";
    }
    @PostMapping("/admin/mail/setdraft")
    public String draftMail(@RequestParam(required = false) LocalDate dateSend,
                            @RequestParam(required = false) LocalTime timeSend,
                            @RequestParam(required = false) String content,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String mailRecipient,
                            @RequestParam(required = false) Integer id,
                            RedirectAttributes redirectAttributes) {

        MailForm mailForm = MailForm.builder()
                .mailRecipient(mailRecipient)
                .dateSend(dateSend)
                .timeSend(timeSend)
                .status(MailStatus.DRAFT)
                .content(content)
                .title(title)
                .build();
        if(id == null){
            mailService.draftNewMail(mailForm);
            redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Your email has been saved");
            return "redirect:/admin/mails";
        }
        Optional<Mail> mail = mailService.checkExsist(id);

        if(mail.get().getStatus().equals(MailStatus.SENT)) {
            redirectAttributes.addFlashAttribute(Constants.ERROR, "Your email has been sent");
            return "redirect:/admin/mail/view/" + id;
        }
        mailForm.setMailId(id);
        mailService.draftExistMail(mailForm);
        redirectAttributes.addFlashAttribute(Constants.SUCCESS, "Your email has been saved as draft");
        return "redirect:/admin/mails";
    }

}
