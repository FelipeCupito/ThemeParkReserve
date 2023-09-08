package ar.edu.itba.pod.client.admin.actions;

import ar.edu.itba.pod.client.ClientAction;
import ar.edu.itba.pod.client.properties.PropertyManager;
import ar.edu.itba.pod.client.properties.exceptions.PropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlotsAction implements ClientAction {
    private static final Logger logger = LoggerFactory.getLogger(SlotsAction.class);

    String rideName;
    int dayOfYear;
    int amount;

    public SlotsAction(String rideName, int dayOfYear, int amount) {
        this.rideName = rideName;
        this.dayOfYear = dayOfYear;
        this.amount = amount;
    }

    public SlotsAction(PropertyManager properties) throws PropertyException {
        // TODO: check if parameter names are correct according to spec (spec is inconsistent)
        rideName = properties.getProperty("rideName");
        dayOfYear = properties.getDayOfYearProperty("dayOfYear");
        amount = properties.getPositiveIntProperty("amount");
    }

    @Override
    public void run() {
        // TODO: Implement

        logger.info(toString());
    }

    public String getRideName() {
        return rideName;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("SlotsAction { rideName: \"%s\", dayOfYear: %d, amount: %d }", rideName, dayOfYear,
                amount);
    }
}
