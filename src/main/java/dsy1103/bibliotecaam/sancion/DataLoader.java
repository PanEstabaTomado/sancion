package dsy1103.bibliotecaam.sancion;

import dsy1103.bibliotecaam.sancion.model.Sancion;
import dsy1103.bibliotecaam.sancion.repository.SancionRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private SancionRepository sancionRepository;

    @Override
    public void run (String... args) throws Exception{
        Faker faker = new Faker();

        for (int i = 0; i < 6; i++) {
            Sancion sancion = new Sancion();
            sancion.setFecIniSancion(faker.timeAndDate().birthday());
            sancion.setMontoMulta(faker.number().numberBetween(0,9999));
            sancion.setMotivo(faker.lorem().sentence());
            sancion.setPagado(faker.bool().bool());
            sancion.setIdUsuario((long)faker.number().numberBetween(1,3));
            sancion.setIdLibro((long)faker.number().numberBetween(1,3));

            sancionRepository.save(sancion);
        }
    }
}
