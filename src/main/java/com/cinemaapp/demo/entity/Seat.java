package com.cinemaapp.demo.entity;

import lombok.*;

import javax.persistence.*;

import java.util.Objects;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat implements Comparable<Seat>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private Long seatRow;
    private Long seatNumber;
    @ManyToMany(mappedBy = "availableSeats")
    private Set<Screening> screenings;
    @ManyToMany(mappedBy = "selectedSeats")
    private Set<Booking> bookings;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return Objects.equals(ID, seat.ID) && Objects.equals(seatRow, seat.seatRow) && Objects.equals(seatNumber, seat.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, seatRow, seatNumber);
    }

    @Override
    public int compareTo(Seat seat) {
        if(seatRow.equals(seat.seatRow) && seatNumber.equals(seat.seatNumber))
            return 0;
        if(seatRow>seat.seatRow)
            return 1;
        else
            if(seatRow<seat.seatRow)
                return -1;
            else
                if(seatNumber>seat.seatNumber)
                    return 1;
                else return -1;
    }
}
