package com.example.demo.controller;

import com.example.demo.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;
}
