package ar.edu.itba.pod.server;
import ar.edu.itba.pod.server.persistance.AttractionRepository;
import ar.edu.itba.pod.server.persistance.PassRepository;
import ar.edu.itba.pod.server.persistance.ReservationsRepository;
import ar.edu.itba.pod.server.services.AdminService;
import ar.edu.itba.pod.server.services.ReservationService;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.AdminServiceGrpc;

import java.io.IOException;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        logger.info(" Server Starting ...");

        AttractionRepository attractionRepository = new AttractionRepository();
        PassRepository passRepository = new PassRepository();
        ReservationsRepository reservationsRepository = new ReservationsRepository();

        int port = 50051;
        io.grpc.Server server = ServerBuilder
                .forPort(port)
                .addService(new AdminService(attractionRepository, passRepository, reservationsRepository))
                .addService(new ReservationService(attractionRepository, passRepository, reservationsRepository))
                .build();
        server.start();
        logger.info("Server started, listening on " + port);
        server.awaitTermination();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("Server shut down");
        }));
    }
}

