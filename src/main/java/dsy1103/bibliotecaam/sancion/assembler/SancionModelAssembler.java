package dsy1103.bibliotecaam.sancion.assembler;
import dsy1103.bibliotecaam.sancion.controller.SancionController;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SancionModelAssembler implements RepresentationModelAssembler<SancionResponseDTO, EntityModel<SancionResponseDTO>> {
    @Override
    public EntityModel<SancionResponseDTO> toModel(SancionResponseDTO sancionDto){
        return EntityModel.of(sancionDto,
                linkTo(methodOn(SancionController.class).obtenerPorId(sancionDto.getIdSancion())).withSelfRel(),
                linkTo(methodOn(SancionController.class).obtenerTodas()).withRel("sanciones"));
    }
}
