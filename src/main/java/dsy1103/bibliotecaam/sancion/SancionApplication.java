package dsy1103.bibliotecaam.sancion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*
----------------------------- AVISO DE UTILIDAD -------------------------------
* * * * * ANTES DE INICIAR EL MICRO SERVICIO DE SANCION
* * * * * INICIALIZA EL MICRO SERVICIO DE PRESTAMO
* * * * * O TENDRAS PROBLEMAS AL CREAR Y MODIFICAR CUANDO LO USES.
 */
@SpringBootApplication
public class SancionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SancionApplication.class, args);
	}

}
