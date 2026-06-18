package dsy1103.bibliotecaam.sancion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
----------------------------- AVISO DE UTILIDAD -------------------------------
* * * * * ANTES DE INICIAR EL MICRO SERVICIO DE SANCION
* * * * * INICIALIZA EL MICRO SERVICIO DE USUARIO Y LIBRO
* * * * * O TENDRAS PROBLEMAS AL CREAR Y MODIFICAR CUANDO LO USES.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SancionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SancionApplication.class, args);
	}

}
