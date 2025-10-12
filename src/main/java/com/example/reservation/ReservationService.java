package com.example.reservation;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service //Бизнес-логика
public class ReservationService {

    private final Map<Long, Reservation> reservationMap = Map.of(
            1L, new Reservation(
                    1L,
                    100L,
                    40L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED
            ),
            2L, new Reservation(
                    2L,
                    101L,
                    41L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED
            ),
            3L, new Reservation(
                    3L,
                    103L,
                    42L,
                    LocalDate.now(),
                    LocalDate.now().plusDays(5),
                    ReservationStatus.APPROVED
            )
    );

    public Reservation getReservationByID(
            Long id
    ) {
        if(!reservationMap.containsKey(id)){
            throw new NoSuchElementException("Not found reservation by id = " + id);
        }
        return reservationMap.get(id);
    }

    public List<Reservation> findAllReservations() {
        return reservationMap.values().stream().toList();
    }
}
