package com.segundito.app.controller.web;

import com.segundito.app.service.CategoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private final CategoriaService categoriaService;

    @Autowired
    public CustomErrorController( CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("errorMessage", "Ha ocurrido un error al procesar su solicitud");

        return "error/error";
    }
}