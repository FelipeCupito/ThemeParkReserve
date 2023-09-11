package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;
import services.Park.PassRequest;
import services.Park.PassType;
import services.Park.UUID;

public class PassLineParser extends CSVLineParser<PassRequest> {
    public PassLineParser() {
        super(3);
    }

    private PassType parsePassType(String string) throws ParseException {
        switch (string.toUpperCase()) {
            case "UNLIMITED":
                return PassType.PASS_UNLIMITED;
            case "THREE":
                return PassType.PASS_THREE;
            case "HALFDAY":
                return PassType.PASS_HALF_DAY;
            default:
                throw new ParseException(String.format("Invalid PassType \"%s\"", string));
        }
    }

    @Override
    PassRequest parseFields(String[] parts) throws ParseException {
        UUID id = new UUIDParser().parse(parts[0]);
        PassType passType = parsePassType(parts[1]);
        int day = new PositiveIntegerParser().parse(parts[2]);

        return PassRequest
                .newBuilder()
                .setUserId(id)
                .setDay(day)
                .setType(passType)
                .build();
    }
}
