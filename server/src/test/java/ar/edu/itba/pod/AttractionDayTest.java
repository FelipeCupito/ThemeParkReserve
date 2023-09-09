package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.AttractionDay;
import ar.edu.itba.pod.server.models.Pass;
import ar.edu.itba.pod.server.models.Status;
import org.junit.Before;
import org.junit.Test;
import services.Park;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AttractionDayTest {
    private AttractionDay attractionDay;
    private Pass testPass1, testPass2, testPass3;
    @Before
    public void setup(){
        attractionDay = new AttractionDay(10); // con 10 slots

        testPass1 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
        testPass2 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
        testPass3 = new Pass(UUID.randomUUID().toString(), Park.PassType.UNLIMITED, 1);
    }

    // Probar un valor de capacidad válido y normal
    @Test
    public void testSetCapacityNormalValue() {
        attractionDay.setCapacity(20);
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(1).getCapacity());
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(2).getCapacity());
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(3).getCapacity());
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(4).getCapacity());
        assertEquals(20, (int) attractionDay.getSingleSlotAvailability(0).getCapacity());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testSetCapacityShouldFailOnSecondAttempt() {
        attractionDay.setCapacity(20);
        attractionDay.setCapacity(10); // Debería fallar aquí
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCapacityInvalidCapacity() {
        attractionDay.setCapacity(-1); // comprobamos que lanza excepción con capacidad negativa
    }

    @Test
    public void testSetCapacityWithReservationsUnderCapacity() {
        // aquí deberíamos agregar algunas reservas, por debajo de la capacidad que vamos a establecer,
        // y comprobar que todas se confirman
        attractionDay.addReservation(testPass1, 2);
        attractionDay.addReservation(testPass2, 2);
        attractionDay.addReservation(testPass3, 2);
        attractionDay.setCapacity(1);

        assertEquals(testPass1, attractionDay.getReservation(2,testPass1).getPass());
        assertEquals(Status.CONFIRMED, attractionDay.getReservation(2,testPass1).getStatus());
        assertEquals(testPass2, attractionDay.getReservation(3,testPass2).getPass());
        assertEquals(Status.CONFIRMED, attractionDay.getReservation(3,testPass2).getStatus());
        assertEquals(testPass3, attractionDay.getReservation(4,testPass3).getPass());
        assertEquals(Status.CONFIRMED, attractionDay.getReservation(4,testPass3).getStatus());
    }

}
