package ar.edu.itba.pod;

import ar.edu.itba.pod.server.models.ParkRepository;
import org.junit.Before;
import org.junit.Test;
import services.Park;

import java.util.UUID;

import static org.junit.Assert.assertThrows;

public class AdminServiceTest {
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
    public void testAddPass_invalidDay() {
        String uuid = UUID.randomUUID().toString();
        //0 no es un día válido, falla
        parkRepository.addPass(uuid, Park.PassType.UNLIMITED, 0);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testAddPass_negativeDay() {
        String uuid = UUID.randomUUID().toString();
        //-1 no es un día válido, falla
        parkRepository.addPass(uuid, Park.PassType.UNLIMITED, -1);
    }

    //////////////////////////////
    //  Set Attraction Capacity //
    //////////////////////////////


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




}
