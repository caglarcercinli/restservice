package be.vdab.restservice.restcontrollers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/filialen")
@ExposesResourceFor(Filiaal.class)
public class FiliaalController {
    private final FiliaalService filiaalService;
    private final TypedEntityLinks.ExtendedTypedEntityLinks<Filiaal> links;

    FiliaalController(FiliaalService filiaalService, EntityLinks links) {
        this.filiaalService = filiaalService;
        this.links = links.forType(Filiaal.class, Filiaal::getId);
    }

    /*
        @GetMapping("{id}")
        Filiaal get(@PathVariable long id){
            return filiaalService.findById(id)
                    .orElseThrow(FiliaalNietGevondenException::new);
        }

     */
    @Operation(summary = "Een filiaal zoeken op id")
    @GetMapping("{id}")
    EntityModel<Filiaal> get(@PathVariable long id) {
        return filiaalService.findById(id)
                .map(filiaal -> EntityModel.of(filiaal,
                        links.linkToItemResource(filiaal),
                        links.linkForItemResource(filiaal)
                                .slash("werknemers").withRel("werknemers")))
                .orElseThrow(FiliaalNietGevondenException::new);
    }


    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void filiaalNietGevonden() {
    }

    @Operation(summary = "Een filiaal verwijderen")
    @DeleteMapping("{id}")
    void delete(@PathVariable long id) {
        filiaalService.delete(id);
    }

    /*
        @PostMapping
        void post(@RequestBody @Valid Filiaal filiaal){
            filiaalService.create(filiaal);
        }

     */
  
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    HttpHeaders create(@RequestBody @Valid Filiaal filiaal) {
        filiaalService.create(filiaal);
        var headers = new HttpHeaders();
        headers.setLocation(links.linkForItemResource(filiaal).toUri());
        return headers;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> verkeerdeData(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @Operation(summary = "Een filiaal wijzigen")
    @PutMapping("{id}")
    void put(@PathVariable long id, @RequestBody @Valid Filiaal filiaal) {
        filiaalService.update(filiaal.withId(id));
    }

    @Operation(summary = "Alle filialen zoeken")
    @GetMapping
    CollectionModel<EntityModel<FiliaalIdNaam>> findAll() {
        return CollectionModel.of(
                filiaalService.findAll().stream()
                        .map(filiaal ->
                                EntityModel.of(new FiliaalIdNaam(filiaal),
                                        links.linkToItemResource(filiaal)))
                        ::iterator,
                links.linkToCollectionResource()
        );
    }

    private static class FiliaalIdNaam {
        private final long id;
        private final String naam;

        FiliaalIdNaam(Filiaal filiaal) {
            id = filiaal.getId();
            naam = filiaal.getNaam();
        }

        public long getId() {
            return id;
        }

        public String getNaam() {
            return naam;
        }
    }
}
