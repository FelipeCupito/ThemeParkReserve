package ar.edu.itba.pod.server;

import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import ar.edu.itba.pod.server.services.AdminService;
import ar.edu.itba.pod.server.services.NotificationService;
import ar.edu.itba.pod.server.services.ReservationService;
import com.google.protobuf.Empty;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import services.*;

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

        NotificationService notificationService = new NotificationService(attractionRepository, passRepository);
        AdminService adminService = new AdminService(attractionRepository, passRepository, reservationsRepository, notificationService);
        ReservationService reservationService = new ReservationService(attractionRepository, passRepository, reservationsRepository, notificationService);

        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor()
                .addService(adminService)
                .addService(reservationService)
                .addService(notificationService)
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

    @Test
    public void test() {
        var attractionName = "SpaceMountain";

        admin.addAttraction(Park.AttractionInfo.newBuilder()
                .setName(attractionName)
                .setOpenTime(timeToMinutes(9, 0))
                .setCloseTime(timeToMinutes(22, 0))
                .setSlotDuration(30)
                .build());

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

        assertArrayEquals(new Park.AttractionInfo[]{
                        Park.AttractionInfo.newBuilder()
                                .setName(attractionName)
                                .setOpenTime(timeToMinutes(9, 0))
                                .setCloseTime(timeToMinutes(22, 0))
                                .setSlotDuration(30)
                                .build()
                },
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
}