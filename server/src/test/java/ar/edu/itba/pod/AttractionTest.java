package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.Attraction;
import ar.edu.itba.pod.server.models.Pass;
import ar.edu.itba.pod.server.models.Utils;
import org.junit.Before;
import org.junit.Test;
import services.Park;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class AttractionTest {

    private Attraction attraction;

    @Before
    public void setUp() {
        attraction = new Attraction("Roller Coaster", 800, 900, 15);
    }

    @Test
    public void testConstruction() {
        assertEquals("Roller Coaster", attraction.getAttraction().getName());
        assertEquals(800, attraction.getAttraction().getOpenTime());
        assertEquals(900, attraction.getAttraction().getCloseTime());
        assertEquals(15, attraction.getAttraction().getMinutesPerSlot());
    }

    @Test
    public void testSetDayCapacity() {
        attraction.setDayCapacity(1, 20);
        assertEquals(20, attraction.getSingleSlotAvailability(1, 800).getResponse().getCapacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDayCapacityWithInvalidDay() {
        attraction.setDayCapacity(Utils.DAYS_OF_YEAR + 1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSingleSlotAvailabilityWithInvalidDay() {
        attraction.getSingleSlotAvailability(Utils.DAYS_OF_YEAR + 1, 600);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSingleSlotAvailabilityWithInvalidMinutes() {
        attraction.getSingleSlotAvailability(1, 1400);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSlotsAvailabilityByRangeWithInvalidStartMinutes() {
        attraction.getSlotsAvailabilityByRange(1, 1400, 900);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddReservationWithInvalidMinutes() {
        Pass pass = new Pass(UUID.randomUUID().toString(), Park.PassType.HALF_DAY, 1);
        attraction.addReservation(pass, 1, 1400);
    }

    @Test
    public void testCalculateSlotsPerDay() {
        Attraction attraction1 = new Attraction("Test Attraction", 0, 900, 120);
        assertEquals(8, attraction1.calculateSlotsPerDay());

        Attraction attraction2 = new Attraction("Test Attraction", 0, 800, 100);
        assertEquals(8, attraction2.calculateSlotsPerDay());

        Attraction attraction3 = new Attraction("Test Attraction", 0, 100, 100);
        assertEquals(1, attraction3.calculateSlotsPerDay());

        Attraction attraction4 = new Attraction("Test Attraction", 540, 600, 40);
        assertEquals(2, attraction4.calculateSlotsPerDay());
    }

    @Test
    public void testGetSlotIndex() {
        assertEquals(0, attraction.getSlotIndex(800));
        assertEquals(0, attraction.getSlotIndex(805));

        assertEquals(1, attraction.getSlotIndex(815));
        assertEquals(1, attraction.getSlotIndex(820));

        assertEquals(2, attraction.getSlotIndex(830));
        assertEquals(2, attraction.getSlotIndex(835));

        assertEquals(3, attraction.getSlotIndex(845));
        assertEquals(3, attraction.getSlotIndex(850));

        assertEquals(4, attraction.getSlotIndex(860));
        assertEquals(4, attraction.getSlotIndex(865));


        assertEquals(5, attraction.getSlotIndex(875));
        assertEquals(5, attraction.getSlotIndex(880));

        assertEquals(6, attraction.getSlotIndex(890));
        assertEquals(6, attraction.getSlotIndex(900));


        assertThrows(IllegalArgumentException.class, () -> attraction.getSlotIndex(795));
        assertThrows(IllegalArgumentException.class, () -> attraction.getSlotIndex(905));
    }

    @Test
    public void testGetSlotIndicesInRange() {
        List<Integer> result = attraction.getSlotIndicesInRange(800, 830);
        assertEquals(3, result.size());
        assertTrue(result.contains(0));
        assertTrue(result.contains(1));
        assertTrue(result.contains(2));

        result = attraction.getSlotIndicesInRange(800, 900);
        assertEquals(7, result.size());

        result = attraction.getSlotIndicesInRange(830, 875);
        assertEquals(4, result.size());


        assertThrows(IllegalArgumentException.class, () -> attraction.getSlotIndicesInRange(795, 805));
        assertThrows(IllegalArgumentException.class, () -> attraction.getSlotIndicesInRange(805, 795));
    }

}
