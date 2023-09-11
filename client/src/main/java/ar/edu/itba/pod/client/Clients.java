package ar.edu.itba.pod.client;

import services.AdminServiceGrpc;
import services.AdminServiceGrpc.AdminServiceBlockingStub;
import services.NotificationServiceGrpc;
import services.NotificationServiceGrpc.NotificationServiceBlockingStub;
import services.QueryServiceGrpc;
import services.QueryServiceGrpc.QueryServiceBlockingStub;
import services.ReservationServiceGrpc;
import services.ReservationServiceGrpc.ReservationServiceBlockingStub;

public class Clients {
    AdminServiceBlockingStub adminService;
    NotificationServiceBlockingStub notificationService;
    ReservationServiceBlockingStub reservationService;
    QueryServiceBlockingStub queryService;

    private Clients(AdminServiceBlockingStub adminService, NotificationServiceBlockingStub notificationService, ReservationServiceBlockingStub reservationService, QueryServiceBlockingStub queryService) {
        this.adminService = adminService;
        this.notificationService = notificationService;
        this.reservationService = reservationService;
        this.queryService = queryService;
    }

    public static Clients fromChannel(io.grpc.Channel channel) {
        var adminService = AdminServiceGrpc.newBlockingStub(channel);
        var notificationService = NotificationServiceGrpc.newBlockingStub(channel);
        var reservationService = ReservationServiceGrpc.newBlockingStub(channel);
        var queryService = QueryServiceGrpc.newBlockingStub(channel);

        return new Clients(adminService, notificationService, reservationService, queryService);
    }
}
