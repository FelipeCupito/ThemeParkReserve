package ar.edu.itba.pod.client.book.actions;

import ar.edu.itba.pod.client.Clients;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookAction extends ReservationAction {
    private static final Logger logger = LoggerFactory.getLogger(BookAction.class);

    public BookAction(PropertyManager properties) throws PropertyException {
        super(properties);
    }

    @Override
    public void run(Clients clients) {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return String.format("BookAction %s", super.toString());
    }
}
