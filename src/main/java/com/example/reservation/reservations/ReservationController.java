package com.example.reservation.reservations;

import jakarta.validation.Valid;
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
    public ResponseEntity<List<Reservation>> getAllReservation(
            //required = false - необязательный параметр в запросе
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ) {
        log.info("Called getAllReservation");
        var filter = new ReservationSearchFilter(
                roomId,
                userId,
                pageSize,
                pageNumber
        );
        return ResponseEntity.ok(reservationService.searchAllByFilter (
                filter
        ));
    }


    @PostMapping
    public ResponseEntity<Reservation> createReservation(
        @RequestBody @Valid Reservation reservationToCreate
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
        @RequestBody @Valid Reservation reservationToUpdate
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
        reservationService.cancelReservation(id);
        return ResponseEntity.ok().build();

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
