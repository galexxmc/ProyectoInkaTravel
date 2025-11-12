package com.inkatravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Este controlador solo "atrapa" las redirecciones de Mercado Pago
 * y las envía al frontend de Angular.
 */
@Controller
public class RedirectController {

    /**
     * Atrapa la redirección de pago exitoso.
     * @return Redirige al frontend (localhost:4200).
     */
    @GetMapping("/pago-exitoso")
    public String redirectPagoExitoso() {
        // Redirige al navegador a la URL de tu frontend
        return "redirect:http://localhost:4200/pago-exitoso";
    }

    /**
     * Atrapa la redirección de suscripción exitosa.
     * @return Redirige al frontend.
     */
    @GetMapping("/suscripcion-exitosa")
    public String redirectSuscripcionExitosa() {
        return "redirect:http://localhost:4200/suscripcion-exitosa";
    }

    @GetMapping("/pago-fallido")
    public String redirectPagoFallido() {
        return "redirect:http://localhost:4200/pago-fallido";
    }

    @GetMapping("/pago-pendiente")
    public String redirectPagoPendiente() {
        return "redirect:http://localhost:4200/pago-pendiente";
    }
}