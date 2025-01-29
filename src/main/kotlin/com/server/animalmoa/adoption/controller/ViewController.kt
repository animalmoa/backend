package com.server.animalmoa.adoption.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ViewController {
    @GetMapping("/")
    fun mainPage(model: Model): String = "index"
}
