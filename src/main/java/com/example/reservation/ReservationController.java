package com.example.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //Обработчик HTTP-запросов
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;


    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/{id}")
    public Reservation getReservationByID(
            @PathVariable("id") Long id
    ){
        log.info("Called getReservationByID: id = {}", id);
        return reservationService.getReservationByID(id);
    }


    @GetMapping()
    public List<Reservation> getAllReservation(){
        log.info("Called getAllReservation");
        return reservationService.findAllReservations();
    }


    @PostMapping
    public Reservation createReservation(
        @RequestBody Reservation reservationToCreate
    ) {
        log.info("Called createReservation");
        return reservationService.createReservation(reservationToCreate);
    }
}
