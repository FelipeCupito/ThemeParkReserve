package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.AttractionDay;
import ar.edu.itba.pod.server.models.Pass;
import ar.edu.itba.pod.server.models.Status;
import org.junit.Before;
import org.junit.Test;
import services.Park;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AttractionDayTest {
    private AttractionDay attractionDay;
    private Pass testPass1, testPass2, testPass3;
    @Before
    public void setup(){
        attractionDay = new AttractionDay(3);

        testPass1 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
        testPass2 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
        testPass3 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
    }

    @Test
    public void testSetCapacityNormalValue() {
        attractionDay.setCapacity(20);
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(1).getCapacity());
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(2).getCapacity());
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(0).getCapacity());
    }
    @Test
    public void testSetCapacityShouldFailOnSecondAttempt() {
        attractionDay.setCapacity(20);
        assertThrows(IllegalArgumentException.class, () -> attractionDay.setCapacity(10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCapacityInvalidCapacity() {
        attractionDay.setCapacity(-1);
    }

    @Test
    public void testSetCapacityWithReservationsUnderCapacity() {
        attractionDay.addReservation(testPass1, 0);
        attractionDay.addReservation(testPass2, 0);
        attractionDay.addReservation(testPass3, 0);
        attractionDay.setCapacity(1);

        assertEquals(testPass1, attractionDay.getReservation(0,testPass1).getPass());
        assertEquals(Status.CONFIRMED, attractionDay.getReservation(0,testPass1).getStatus());
        assertEquals(testPass2, attractionDay.getReservation(1,testPass2).getPass());
        assertEquals(Status.PENDING, attractionDay.getReservation(1,testPass2).getStatus());
        assertEquals(testPass3, attractionDay.getReservation(2,testPass3).getPass());
        assertEquals(Status.PENDING, attractionDay.getReservation(2,testPass3).getStatus());

        assertEquals(1, (int) testPass1.getReservationNumber());
        assertEquals(1, (int) testPass2.getReservationNumber());
        assertEquals(1, (int) testPass3.getReservationNumber());
    }

    @Test
    public void testSetCapacityWithReservationsUnderCapacityCanceled() {
        attractionDay.addReservation(testPass1, 1);
        attractionDay.addReservation(testPass2, 1);
        attractionDay.addReservation(testPass3, 1);
        attractionDay.setCapacity(1);

        assertEquals(testPass1, attractionDay.getReservation(1,testPass1).getPass());
        assertEquals(Status.CONFIRMED, attractionDay.getReservation(1,testPass1).getStatus());

        assertEquals(testPass2, attractionDay.getReservation(2,testPass2).getPass());
        assertEquals(Status.PENDING, attractionDay.getReservation(2,testPass2).getStatus());

        assertThrows(IllegalArgumentException.class, () -> attractionDay.getReservation(1,testPass3));

        assertEquals(1, (int) testPass1.getReservationNumber());
        assertEquals(1, (int) testPass2.getReservationNumber());
        assertEquals(0, (int) testPass3.getReservationNumber());
    }
}
