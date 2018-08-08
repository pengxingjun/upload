package com.web.controller;

import com.upload.helper.SystemValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "editor")
public class MainController {

    /*@RequestMapping(value = "finder", method = RequestMethod.GET)
    public ModelAndView execute(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/uefinder");
        modelAndView.addObject("fileServer", SystemValue.FILE_SERVER_ADDRESS);
        //modelAndView.addObject("mime","image");
        modelAndView.addObject("userId","1");
        return modelAndView;
    }*/
}
