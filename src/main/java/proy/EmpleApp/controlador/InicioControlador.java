package proy.EmpleApp.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Atiende la página inicial y muestra el menú principal de EmpleApp. */
@Controller
public class InicioControlador {
    @GetMapping({"/", "/index"})
    public String inicio() { return "index"; }
}
