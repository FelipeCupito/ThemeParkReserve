package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import ar.edu.itba.pod.server.services.AdminService;
import ar.edu.itba.pod.server.services.NotificationService;
import ar.edu.itba.pod.server.services.QueryService;
import ar.edu.itba.pod.server.services.ReservationService;
import com.google.protobuf.Empty;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import services.*;
import services.AdminServiceGrpc.AdminServiceBlockingStub;
import services.NotificationServiceGrpc.NotificationServiceBlockingStub;
import services.QueryServiceGrpc.QueryServiceBlockingStub;
import services.ReservationServiceGrpc.ReservationServiceBlockingStub;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class ServerTest {
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private AdminServiceGrpc.AdminServiceBlockingStub admin;
    private NotificationServiceGrpc.NotificationServiceBlockingStub notification;
    private ReservationServiceGrpc.ReservationServiceBlockingStub reservation;
    private QueryServiceGrpc.QueryServiceBlockingStub query;

    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();

        AttractionRepository attractionRepository = new AttractionRepository();
        PassRepository passRepository = new PassRepository();
        ReservationsRepository reservationsRepository = new ReservationsRepository();

        NotificationService notificationService = new NotificationService(attractionRepository, passRepository, reservationsRepository);
        AdminService adminService = new AdminService(attractionRepository, passRepository, reservationsRepository, notificationService);
        ReservationService reservationService = new ReservationService(attractionRepository, passRepository, reservationsRepository, notificationService);
        QueryService queryService = new QueryService(reservationsRepository, attractionRepository);

        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor()
                .addService(adminService)
                .addService(reservationService)
                .addService(notificationService)
                .addService(queryService)
                .build().start());

        var channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
        admin = AdminServiceGrpc.newBlockingStub(channel);
        notification = NotificationServiceGrpc.newBlockingStub(channel);
        reservation = ReservationServiceGrpc.newBlockingStub(channel);
        query = QueryServiceGrpc.newBlockingStub(channel);
    }

    private int timeToMinutes(int hours, int minutes) {
        return hours * 60 + minutes;
    }

    private String timeToString(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }

    Park.AttractionInfo[] attractions = {
            Park.AttractionInfo.newBuilder()
                    .setName("SpaceMountain")
                    .setOpenTime(timeToMinutes(9, 0))
                    .setCloseTime(timeToMinutes(22, 0))
                    .setSlotDuration(30)
                    .build(),
            Park.AttractionInfo.newBuilder()
                    .setName("TronLightcycle")
                    .setOpenTime(timeToMinutes(10, 0))
                    .setCloseTime(timeToMinutes(22, 0))
                    .setSlotDuration(15)
                    .build(),
    };

    private void addAttractions() {
        Arrays.stream(attractions).forEachOrdered(a -> admin.addAttraction(a));
    }

    Park.UUID generateUUID() {
        return Park.UUID.newBuilder().setValue(UUID.randomUUID().toString()).build();
    }

    List<Park.PassRequest> visitors = IntStream.range(0, 100).mapToObj(
                    i -> Park.PassRequest.newBuilder()
                            .setUserId(generateUUID())
                            .setDay(100)
                            .setType(Park.PassType.PASS_UNLIMITED)
                            .build()
            )
            .collect(Collectors.toList());

    private void addPasses() {
        visitors.forEach(v -> admin.addPass(v));
    }

    @Test
    public void simpleTest() {
        var attractionName = attractions[0].getName();

        addAttractions();

        // Cannot have two attractions with the same name
        assertThrows(Exception.class, () ->
                admin.addAttraction(Park.AttractionInfo.newBuilder()
                        .setName(attractionName)
                        .setOpenTime(timeToMinutes(10, 0))
                        .setCloseTime(timeToMinutes(16, 0))
                        .setSlotDuration(15)
                        .build())
        );

        Park.UUID uuid = Park.UUID.newBuilder().setValue("2af16ea7-4af1-47f6-bf46-8515de5a500f").build();

        admin.addPass(Park.PassRequest.newBuilder()
                .setUserId(uuid)
                .setDay(100)
                .setType(Park.PassType.PASS_HALF_DAY)
                .build());

        admin.addPass(Park.PassRequest.newBuilder()
                .setUserId(uuid)
                .setDay(101)
                .setType(Park.PassType.PASS_HALF_DAY)
                .build());

        assertArrayEquals(
                attractions,
                reservation.getAttractions(Empty.newBuilder().build()).getAttractionsList().toArray()
        );

        // Reservation should be pending because slot capacity was not set
        assertEquals(Park.ReservationType.RESERVATION_PENDING, reservation.addReservation(
                Park.ReservationInfo.newBuilder()
                        .setUserId(uuid)
                        .setDay(100)
                        .setSlot(timeToMinutes(10, 0))
                        .setAttractionName(attractionName)
                        .build()
        ).getType());

        admin.addSlotCapacity(Park.SlotCapacityRequest.newBuilder()
                .setDay(101)
                .setAttractionName(attractionName)
                .setCapacity(30)
                .build());

        // Reservation should be CONFIRMED because capacity was already set
        assertEquals(Park.ReservationType.RESERVATION_CONFIRMED, reservation.addReservation(
                Park.ReservationInfo.newBuilder()
                        .setUserId(uuid)
                        .setDay(101)
                        .setSlot(timeToMinutes(10, 0))
                        .setAttractionName(attractionName)
                        .build()
        ).getType());


        // TRY CONFIRMING RESERVATION FOR DAY 100:
        // Cannot confirm if attraction does not have capacity set
        assertThrows(Exception.class, () ->
                reservation.confirmReservation(Park.ReservationInfo.newBuilder()
                        .setAttractionName(attractionName)
                        .setSlot(timeToMinutes(10, 0))
                        .setDay(100)
                        .setUserId(uuid)
                        .build())
        );

        // Should confirm the pending reservation
        assertEquals(1,
                admin.addSlotCapacity(Park.SlotCapacityRequest.newBuilder()
                        .setDay(100)
                        .setAttractionName(attractionName)
                        .setCapacity(30)
                        .build()).getConfirmed()
        );

        // Should already be confirmed because capacity was set
        assertThrows(Exception.class, () ->
                reservation.confirmReservation(Park.ReservationInfo.newBuilder()
                        .setAttractionName(attractionName)
                        .setSlot(timeToMinutes(10, 0))
                        .setDay(100)
                        .setUserId(uuid)
                        .build())
        );
    }


    @Test
    public void queryTest() {
        addAttractions();
        addPasses();

        var spread = new int[]{10, 20, 40, 30};
        final var maxIndex = 2;
        final var startHour = 12;
        var index = 0;
        for (var count : spread) {
            final var finalIndex = index;
            visitors.stream().limit(count).forEach(v -> reservation.addReservation(
                    Park.ReservationInfo.newBuilder()
                            .setAttractionName(attractions[0].getName())
                            .setDay(100)
                            .setSlot(timeToMinutes(startHour + finalIndex, 0))
                            .setUserId(v.getUserId())
                            .build()
            ));
            index++;
        }

        var suggested = query.getSuggestedCapacity(Park.Day.newBuilder().setDay(100).build());
        var first = suggested.getSlots(0);
        assertEquals(attractions[0].getName(), first.getAttractionName());
        assertEquals(timeToMinutes(startHour + maxIndex, 0), first.getSlot());
        assertEquals(spread[maxIndex], first.getSuggestedCapacity());

        var second = suggested.getSlots(1);
        assertEquals(attractions[1].getName(), second.getAttractionName());
        assertEquals(attractions[1].getOpenTime(), second.getSlot());
        assertEquals(0, second.getSuggestedCapacity());
    }

    @Test
    public void testNotifications() {
        addAttractions();
        addPasses();

        final var reservationInfo = Park.ReservationInfo.newBuilder()
                .setUserId(visitors.get(0).getUserId())
                .setDay(100)
                .setSlot(timeToMinutes(12, 0))
                .setAttractionName(attractions[0].getName())
                .build();

        final var notificationRequest = Park.NotificationRequest.newBuilder()
                .setUserId(visitors.get(0).getUserId())
                .setDay(100)
                .setName(attractions[0].getName())
                .build();

        // Shouldn't be able to subscribe without pending reservations
        assertThrows(Exception.class, () -> notification.registerUser(notificationRequest));

        reservation.addReservation(reservationInfo);

        var notifications = notification.registerUser(notificationRequest);
        reservation.cancelReservation(reservationInfo);
        notifications.forEachRemaining(n -> System.out.println(n.getMessage()));
    }

    @Test
    public void concurrencyTest() throws InterruptedException, ExecutionException {
        final var count = 1000;

        addAttractions();

        List<UserWorker> workers = IntStream.range(0, count).mapToObj(
                i -> new UserWorker(new int[]{1}, generateUUID(), admin, notification, reservation, query)
        ).toList();

        var executorService = Executors.newFixedThreadPool(40);
        var results = executorService.invokeAll(workers);
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        for (var result : results) {
            result.get();
        }

        final var slotCapacity = 20;

        var result = Arrays.stream(attractions).map(
                a -> admin.addSlotCapacity(Park.SlotCapacityRequest.newBuilder()
                        .setAttractionName(a.getName())
                        .setDay(1)
                        .setCapacity(slotCapacity)
                        .build()
                )).reduce(
                (a, b) -> Park.ReservationsResponse.newBuilder()
                        .setConfirmed(a.getConfirmed() + b.getConfirmed())
                        .setCancelled(a.getCancelled() + b.getCancelled())
                        .setMoved(a.getMoved() + b.getMoved())
                        .build()
        ).get();

        assertTrue(result.getConfirmed() <= slotCapacity * attractions.length);
        System.out.printf("CANCELLED: %d\n", result.getCancelled());
        assertEquals(count, result.getConfirmed() + result.getCancelled() + result.getMoved());
    }
}

class UserWorker implements Callable<Void> {
    private int[] daysToBuy;
    private Park.UUID userId;
    private AdminServiceBlockingStub admin;
    private NotificationServiceBlockingStub notification;
    private ReservationServiceBlockingStub reservation;
    private QueryServiceBlockingStub query;

    public UserWorker(int[] daysToBuy, Park.UUID userId, AdminServiceBlockingStub admin, NotificationServiceBlockingStub notification, ReservationServiceBlockingStub reservation, QueryServiceBlockingStub query) {
        this.daysToBuy = daysToBuy;
        this.userId = userId;
        this.admin = admin;
        this.notification = notification;
        this.reservation = reservation;
        this.query = query;
    }

    private int randInt(int start, int end) {
        return (int) (Math.random() * (end - start)) + start;
    }

    private boolean tryToReserve(int day, String attraction, int slot) {
        reservation.addReservation(Park.ReservationInfo.newBuilder()
                .setUserId(userId)
                .setDay(day)
                .setSlot(slot)
                .setAttractionName(attraction)
                .build());

        return true;
    }

    public void run() {
        var countByDay = Arrays.stream(daysToBuy).boxed().collect(Collectors.groupingBy(d -> d, Collectors.counting()));

        for (var day : countByDay.keySet()) {
            admin.addPass(Park.PassRequest.newBuilder()
                    .setDay(day)
                    .setUserId(userId)
                    .setType(Park.PassType.PASS_UNLIMITED)
                    .build());
        }

        for (var day : countByDay.keySet()) {
            for (var i = 0; i < countByDay.get(day); i++) {
                var attractions = reservation.getAttractions(Empty.newBuilder().build()).getAttractionsList();
                var randomAttraction = attractions.get(randInt(0, attractions.size())).getName();
                var availability = reservation.getSlotRangeAvailability(Park.SlotRangeRequest.newBuilder()
                        .setAttractionName(randomAttraction)
                        .setDay(day)
                        .setSlot1(0)
                        .setSlot2(23 * 60 + 59)
                        .build()).getSlotsList();

                var successfulSlot = availability.stream()
                        .filter(s -> tryToReserve(day, s.getAttractionName(), s.getSlot()))
                        .findFirst()
                        .orElseThrow();

//                System.out.println(successfulSlot);
            }
        }
    }

    @Override
    public Void call() throws Exception {
        run();
        return null;
    }
}