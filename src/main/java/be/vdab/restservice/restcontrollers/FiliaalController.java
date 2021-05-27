package be.vdab.restservice.restcontrollers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/filialen")
public class FiliaalController {
    private final FiliaalService filiaalService;

    public FiliaalController(FiliaalService filiaalService) {
        this.filiaalService = filiaalService;
    }
    @GetMapping("{id}")
    Filiaal get(@PathVariable long id){
        return filiaalService.findById(id)
                .orElseThrow(FiliaalNietGevondenException::new);
    }
    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void filiaalNietGevonden(){
    }
    @DeleteMapping("{id}")
    void delete(@PathVariable long id){
        filiaalService.delete(id);
    }
    @PostMapping
    void post(@RequestBody Filiaal filiaal){
        filiaalService.create(filiaal);
    }
}
