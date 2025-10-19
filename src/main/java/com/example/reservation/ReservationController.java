package com.example.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
        @PathVariable("id") Long id,
        @RequestBody Reservation reservationToUpdate
    ) {
        log.info("Called updateReservation: id = {}, reservationToUpdate = {}",
                id, reservationToUpdate);
        var updated = reservationService.updateReservation(id, reservationToUpdate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updated);
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteReservation (
        @PathVariable("id") Long id
    ) {
        log.info("Called deleteReservation: id = {}", id);
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }

    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveReservation (
        @PathVariable("id") Long id
    ) {
        log.info("Called approveReservation: id = {}", id);
        var reservation = reservationService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }


}
