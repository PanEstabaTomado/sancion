package dsy1103.bibliotecaam.sancion.config;

import dsy1103.bibliotecaam.sancion.model.Sancion;
import dsy1103.bibliotecaam.sancion.repository.SancionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner{
    private final SancionRepository sancionRepository;

    @Override
    public void run(String... args) {
        if (sancionRepository.count() > 0) {
            log.info(">>> Sanciones ya cargadas. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando mascotas iniciales...");
        sancionRepository.save(new Sancion(null, LocalDate.of(2006,7,20), 50000,          "No pago en el plazo de vencimiento de su prestamo.", true));
        sancionRepository.save(new Sancion(null, LocalDate.of(2009,12,19), 65000,          "No pago en el plazo de vencimiento de su prestamo.", false));
        sancionRepository.save(new Sancion(null, LocalDate.of(2010,2,21), 35000,          "No pago en el plazo de vencimiento de su prestamo.", false));
        log.info(">>> 3 Sanciones cargadas OK.");
    }
}
