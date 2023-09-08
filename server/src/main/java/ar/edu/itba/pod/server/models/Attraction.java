package ar.edu.itba.pod.server.models;

import services.Park;

import java.util.ArrayList;
import java.util.List;

public class Attraction {

    private final String name;
    private final int openTime;
    private final int closeTime;
    private final Integer minutesPerSlot;
    private Integer slotsPerDay;
    private final List<AttractionDay> days = new ArrayList<>(Utils.DAYS_OF_YEAR);

    public Attraction(String name, int openTime, int closeTime, int minutesPerSlot) {
        Utils.checkAttractionName(name);
        Utils.checkMinutes(openTime);
        Utils.checkMinutes(closeTime);
        Utils.checkMinutes(minutesPerSlot);
        if (openTime >= closeTime || minutesPerSlot > (closeTime - openTime)) {
            throw new IllegalArgumentException("Invalid times");
        }

        this.name = name;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.minutesPerSlot = minutesPerSlot;
        this.slotsPerDay = calculateSlotsPerDay();

        //TODO: mejorar
        for (int i = 0; i < Utils.DAYS_OF_YEAR; i++) {
            days.add(new AttractionDay(slotsPerDay));
        }
    }

    public void setDayCapacity(int day, int capacity){
        Utils.checkDay(day);
        this.days.get(day).setCapacity(capacity);
    }

    public SlotAvailabilityResponse getSingleSlotAvailability(Integer day, Integer minutes) {
        Utils.checkDay(day);
        int slotIndex = getSlotIndex(minutes);

        return days.get(day).getSingleSlotAvailability(slotIndex);
    }

    public List<SlotAvailabilityResponse> getSlotsAvailabilityByRange(Integer day, Integer startMinutes, Integer endMinutes) {
        Utils.checkDay(day);
        List<Integer> slotIndices = getSlotIndicesInRange(startMinutes, endMinutes);

        List<SlotAvailabilityResponse> responses = new ArrayList<>();
        for (Integer slotIndex : slotIndices) {
            responses.add( days.get(day).getSingleSlotAvailability(slotIndex) );
        }

        return responses;
    }


    private int calculateSlotsPerDay(){
        // Calcular la cantidad de minutos entre el horario de apertura y cierre
        int duration = closeTime - openTime;

        // Calcular la cantidad de slots completo sin tener en cuenta el último
        int slotsPerDay = duration / minutesPerSlot;

        // Compruebo si el último slot tiene la duración completa, si no lo agrego
        if (duration % minutesPerSlot > 0) {
            slotsPerDay++;
        }
        return slotsPerDay;
    }

    private int getSlotIndex(int minutes) {
        Utils.checkMinutes(minutes);
        if (minutes < openTime || minutes > closeTime) {
            throw new IllegalArgumentException("Invalid time");
        }

//        // Resta los minutos de apertura a los minutos actuales y divide por la duracion de cada slot. Esto te dirá en qué slot caen los minutos.
//        int slotIndex = (minutes - openTime) / minutesPerSlot;
//
//        // Check if the minutes correspond to the start of a slot. If not, then return the index of the previous slot.
//        if ((minutes - openTime) % minutesPerSlot != 0) {
//            slotIndex--;
//        }
//        return slotIndex;

        // Modificación necesaria en caso de que los minutos del día estén en el intervalo de tiempo de un slot.
        // Si no, el slot devuelto será el siguiente slot
        int mod = (minutes - openTime) % minutesPerSlot;
        int slotMinutes = mod < minutesPerSlot && mod >= 0 ? minutes-mod : minutes - mod - minutesPerSlot;

        return (slotMinutes - openTime) / minutesPerSlot;
    }

    private List<Integer> getSlotIndicesInRange(int startMinutes, int endMinutes) {
        Utils.checkMinutes(startMinutes);
        Utils.checkMinutes(endMinutes);
        if (startMinutes < openTime || endMinutes > closeTime || startMinutes >= endMinutes) {
            throw new IllegalArgumentException("Los tiempos proporcionados no son válidos");
        }

        int startSlotIndex = getSlotIndex(startMinutes);
        int endSlotIndex = getSlotIndex(endMinutes);

        List<Integer> slotIndices = new ArrayList<>();
        for (int i = startSlotIndex; i <= endSlotIndex; i++) {
            slotIndices.add(i);
        }

        return slotIndices;
    }

    public Status addReservation(Pass pass, Integer day, Integer minutes) {
        Utils.checkDay(day);
        int slotIndex = getSlotIndex(minutes);

        return days.get(day).addReservation(pass, slotIndex);
    }

    public void confirmReservation(Pass pass, Integer day, Integer minutes) {
        Utils.checkDay(day);
        int slotIndex = getSlotIndex(minutes);

        days.get(day).confirmReservation(pass, slotIndex);
    }

    public void cancelReservation(Pass pass, Integer day, Integer minutes) {
        Utils.checkDay(day);
        int slotIndex = getSlotIndex(minutes);

        days.get(day).cancelReservation(pass, slotIndex);
    }

    public Park.Attraction getAttraction() {
        return Park.Attraction.newBuilder()
                .setName(name)
                .setOpenTime(openTime)
                .setCloseTime(closeTime)
                .setMinutesPerSlot(minutesPerSlot)
                .build();
    }

    public static Park.AttractionList getParkAttractionList(List<Attraction> attractions) {
        Park.AttractionList.Builder builder = Park.AttractionList.newBuilder();
        for (Attraction attraction : attractions) {
            builder.addAttractions(attraction.getAttraction());
        }
        return builder.build();
    }

}
