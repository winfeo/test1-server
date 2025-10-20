package com.example.reservation.reservations;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service //Бизнес-логика
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);


    private final ReservationRepository repository;
    private final ReservationMapper mapper;

    public ReservationService(
            ReservationRepository repository,
            ReservationMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }


    public Reservation getReservationByID(
            Long id
    ) {

        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation by id = " + id
                ));

        return mapper.toDomain(reservationEntity);
    }

    public List<Reservation> findAllReservations() {

        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    public Reservation createReservation(
            Reservation reservationToCreate
    ) {
        if (reservationToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        if (!reservationToCreate.endDate().isAfter(reservationToCreate.startDate())) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }

        var entityToSave = mapper.toEntity(reservationToCreate);
        entityToSave.setStatus(ReservationStatus.PENDING);

        var savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    public Reservation updateReservation (
            Long id,
            Reservation reservationToUpdate
    ) {

        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot modify reservation: status = " + reservationEntity.getStatus());
        }

        if (!reservationToUpdate.endDate().isAfter(reservationToUpdate.startDate())) {
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }

        var reservationToSave = mapper.toEntity(reservationToUpdate);
        reservationToSave.setId(reservationEntity.getId());
        reservationToSave.setStatus(ReservationStatus.PENDING);

        var updatedReservation = repository.save(reservationToSave);

        return mapper.toDomain(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        var reservation = repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        if (reservation.getStatus().equals(ReservationStatus.APPROVED)) {
            throw new IllegalStateException("Cannot cancel approved reservation.");
        }
        if (reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot cancel the reservation. Reservation was already cancelled");
        }

        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Successfully cancelled reservation: id = {}", id);

        //repository.deleteById(id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Cannot approve reservation: status = " + reservationEntity.getStatus());
        }

        var isConflict = isReservationConflict(
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate()
        );
        if (isConflict) {
            throw new IllegalStateException("Cannot approve reservation because of date conflict" );
        }

        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);

        return mapper.toDomain(reservationEntity);
    }

    private boolean isReservationConflict (
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Long> conflictingIds = repository.findConflictReservationIds(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );

        if (conflictingIds.isEmpty()){
            return false;
        }
        log.info("Conflict with ids = {}", conflictingIds);
        return true;
    }

}
