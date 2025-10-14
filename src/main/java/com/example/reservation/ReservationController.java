package com.example.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //Обработчик HTTP-запросов
@RequestMapping("/reservation")
public class ReservationController {

    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;


    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationByID(
            @PathVariable("id") Long id
    ){
        log.info("Called getReservationByID: id = {}", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(reservationService.getReservationByID(id));
    }


    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservation(){
        log.info("Called getAllReservation");
        return  ResponseEntity.ok(reservationService.findAllReservations());
    }


    @PostMapping
    public ResponseEntity<Reservation> createReservation(
        @RequestBody Reservation reservationToCreate
    ) {
        log.info("Called createReservation");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("test-header", "123")
                .body(reservationService.createReservation(reservationToCreate));
        //return reservationService.createReservation(reservationToCreate);
    }
}
