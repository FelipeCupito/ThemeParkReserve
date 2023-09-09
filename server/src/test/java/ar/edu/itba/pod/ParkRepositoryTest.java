package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.ParkRepository;
import org.junit.Before;
import org.junit.Test;
import services.Park;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ParkRepositoryTest {
    private ParkRepository parkRepository;

    @Before
    public void setUp() {
        parkRepository = new ParkRepository();
    }

    /////////////////////
    //  Add Attraction //
    ////////////////////
    @Test
    public void testAddValidAttraction() {
        parkRepository.addAttraction("Roller Coaster", 800, 900, 15);
    }

    @Test
    public void testAddAttractionAlreadyExists() {
        parkRepository.addAttraction("Roller Coaster", 800, 900, 15);
        assertThrows(IllegalArgumentException.class, () -> parkRepository.addAttraction("Roller Coaster", 800, 900, 15));
    }

    @Test
    public void testAddAttractionWithInvalidOpenTime() {
        assertThrows(IllegalArgumentException.class, () -> parkRepository.addAttraction("Roller Coaster", -100, 900, 15));
    }

    @Test
    public void testAddAttractionWithInvalidCloseTime() {
        assertThrows(IllegalArgumentException.class, () -> parkRepository.addAttraction("Roller Coaster", 800, 2500, 15));
    }

    @Test
    public void testAddAttractionWithNonPositiveSlotMinutes() {
        assertThrows(IllegalArgumentException.class, () -> parkRepository.addAttraction("Roller Coaster", 800, 900, 0));
        assertThrows(IllegalArgumentException.class, () -> parkRepository.addAttraction("Roller Coaster", 800, 900, -15));
    }

    @Test
    public void testAddAttractionWithImpossibleSlot() {
        assertThrows(IllegalArgumentException.class, () -> parkRepository.addAttraction("Roller Coaster", 800, 810, 15));
    }


    /////////////////
    //  Add Pass   //
    /////////////////
    @Test
    public void testAddPass_success() {
        String uuid = UUID.randomUUID().toString();
        parkRepository.addPass(uuid, Park.PassType.UNLIMITED, 1);
        //TODO:check
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddPass_duplicateUUID() {
        String uuid = UUID.randomUUID().toString();
        parkRepository.addPass(uuid, Park.PassType.UNLIMITED, 1);
        parkRepository.addPass(uuid, Park.PassType.THREE, 2); //mismo UUID, falla
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddPass_invalidType() {
        String uuid = UUID.randomUUID().toString();
        parkRepository.addPass(uuid, null, 1); //tipo nulo es inválido, falla
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddPass_negativeDay() {
        String uuid = UUID.randomUUID().toString();
        //-1 no es un día válido, falla
        parkRepository.addPass(uuid, Park.PassType.UNLIMITED, -1);
    }

    @Test
    public void setAttractionCapacityTest() {
        parkRepository.addAttraction("Test Attraction", 10, 100, 50);
        parkRepository.setAttractionCapacity("Test Attraction", 100, 25);
    }

    @Test
    public void setAttractionCapacityInvalidName() {
        parkRepository.addAttraction("Test Attraction", 10, 100, 50);
        assertThrows(IllegalArgumentException.class, () -> parkRepository.setAttractionCapacity("Test Attraction2", 100, 25));
        assertThrows(IllegalArgumentException.class, () -> parkRepository.setAttractionCapacity(null, 100, 25));
    }

    @Test
    public void setAttractionCapacityInvalidDay() {
        parkRepository.addAttraction("Test Attraction", 10, 100, 50);
        assertThrows(IllegalArgumentException.class, () -> parkRepository.setAttractionCapacity("Test Attraction", 100, -1));
    }


    @Test
    public void testAddAttraction() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parkRepository.addAttraction("Ride1", 800, 900, 15);
            parkRepository.addAttraction("Ride1", 800, 900, 20); // Intenta agregar una atracción con el mismo nombre dos veces.
        });
        assertEquals("Attraction already exists", exception.getMessage()); // Verifica si el mensaje de la excepción es el correcto.
    }

    @Test
    public void testAddPass() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            String uuid = UUID.randomUUID().toString();
            parkRepository.addPass(uuid, Park.PassType.HALF_DAY, 5);
            parkRepository.addPass(uuid, Park.PassType.HALF_DAY, 3);
        });
        assertEquals("Pass already exists", exception.getMessage());
    }

    @Test
    public void testSetAttractionCapacity() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            parkRepository.setAttractionCapacity("RideNotExists", 30, 2021);
        });
        String expectedMessage = "Attraction does not exist";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetAttractions() {
        parkRepository.addAttraction("Ride1", 800, 900, 15);
        parkRepository.addAttraction("Ride2", 800, 900, 15);
        assertEquals(2, parkRepository.getAttractions().size()); // Esperamos tener 2 atracciones en el parque.
    }

}
