package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.*;
import org.junit.Before;
import org.junit.Test;
import services.Park;

import java.util.*;

import static org.junit.Assert.*;

public class AttractionSlotTest {
    private AttractionSlot slot;
    private Pass testPass1, testPass2, testPass3;

    @Before
    public void setUp(){
        slot = new AttractionSlot();

        testPass1 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
        testPass2 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
        testPass3 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
    }

    @Test
    public void testSetCapacity_ConfirmAllReservations() {
        // Add 3 reservations
        slot.addReservation(testPass1);
        slot.addReservation(testPass2);
        slot.addReservation(testPass3);

        // check that all reservations are pending
        SlotAvailabilityResponse response = slot.getSingleSlotAvailability();
        assertEquals(3, response.getPending());

        // set capacity to 3
        Queue<Reservation> to_reassign = new LinkedList<>();
        to_reassign = slot.setCapacity(3, to_reassign);

        // check that all reservations are confirmed
        response = slot.getSingleSlotAvailability();
        assertEquals(0, response.getPending());
        assertEquals(3, response.getConfirmed());
        assertTrue(to_reassign.isEmpty());
    }

    @Test
    public void testSetCapacity_AddToReassign() {
        // Add 3 reservations
        slot.addReservation(testPass1);
        slot.addReservation(testPass2);
        slot.addReservation(testPass3);

        // check that all reservations are pending
        SlotAvailabilityResponse response = slot.getSingleSlotAvailability();
        assertEquals(3, response.getPending());

        // set capacity to 3
        Queue<Reservation> to_reassign = new LinkedList<>();
        to_reassign = slot.setCapacity(2, to_reassign);

        // check that all reservations are confirmed
        response = slot.getSingleSlotAvailability();
        assertEquals(1, response.getPending());
        assertEquals(2, response.getConfirmed());
        assertFalse(to_reassign.isEmpty());
        assertEquals(testPass3, to_reassign.peek().getPass());

    }

    @Test
    public void testSetCapacity_SaturateCapacity() {
        // Add 3 reservations
        slot.addReservation(testPass1);
        slot.addReservation(testPass2);
        slot.addReservation(testPass3);

        // check that all reservations are pending
        SlotAvailabilityResponse response = slot.getSingleSlotAvailability();
        assertEquals(3, response.getPending());

        // set capacity to 3
        Queue<Reservation> to_reassign = new LinkedList<>();
        to_reassign = slot.setCapacity(1, to_reassign);

        // check that all reservations are confirmed
        response = slot.getSingleSlotAvailability();
        assertEquals(2, response.getPending());
        assertEquals(1, response.getConfirmed());
        assertFalse(to_reassign.isEmpty());
        assertEquals(2, to_reassign.size());
        assertEquals(testPass2, Objects.requireNonNull(to_reassign.poll()).getPass());
        assertEquals(testPass3, Objects.requireNonNull(to_reassign.poll()).getPass());
    }

    @Test
    public void testAddReservation_ReservationAddedAsPending() {
        //initial check for size
        SlotAvailabilityResponse response = slot.getSingleSlotAvailability();
        assertEquals(0, response.getPending());
        assertEquals(0, response.getConfirmed());

        //add Reservations without capacity set
        Status status = slot.addReservation(testPass1);
        assertEquals(Status.PENDING, status);

        //check if reservation was added and set as pending
        response = slot.getSingleSlotAvailability();
        assertEquals(1, response.getPending());
    }

    @Test
    public void testAddReservation_ReservationAddedAsConfirmed() {
        //initial check for size
        SlotAvailabilityResponse response = slot.getSingleSlotAvailability();
        assertEquals(0, response.getPending());
        assertEquals(0, response.getConfirmed());

        //set Capacity greater than reservations
        Queue<Reservation> to_reassign = new LinkedList<>();
        to_reassign = slot.setCapacity(2, to_reassign);

        //add Reservation
        Status status = slot.addReservation(testPass1);
        assertEquals(Status.CONFIRMED, status);

        //check if reservation was added and set as confirmed
        response = slot.getSingleSlotAvailability();
        assertEquals(0, response.getPending());
        assertEquals(1, response.getConfirmed());
    }

    @Test
    public void testAddReservation_NoCapacityLeft() {
        //set Capacity
        Queue<Reservation> to_reassign = new LinkedList<>();
        to_reassign = slot.setCapacity(1, to_reassign);

        //add Reservations
        slot.addReservation(testPass1);
        assertThrows(IllegalArgumentException.class, () -> slot.addReservation(testPass2));
    }

    @Test
    public void testAddReservation_PassAlreadyExist() {
        //add reservation
        slot.addReservation(testPass1);

        //add the same reservation again - should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> slot.addReservation(testPass1));
    }
}
