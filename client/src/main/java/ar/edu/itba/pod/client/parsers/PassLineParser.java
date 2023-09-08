package ar.edu.itba.pod.client.parsers;

import ar.edu.itba.pod.client.parsers.exceptions.ParseException;
import services.Park.Pass;
import services.Park.PassType;
import services.Park.UUID;

public class PassLineParser extends CSVLineParser<Pass> {
    public PassLineParser() {
        super(3);
    }

    private PassType parsePassType(String string) throws ParseException {
        switch (string.toUpperCase()) {
            case "UNLIMITED":
                return PassType.UNLIMITED;
            case "THREE":
                return PassType.THREE;
            case "HALFDAY":
                return PassType.HALF_DAY;
            default:
                throw new ParseException(String.format("Invalid PassType \"%s\"", string));
        }
    }

    @Override
    Pass parseFields(String[] parts) throws ParseException {
        UUID id = UUID.newBuilder().setValue(new UUIDParser().parse(parts[0])).build();
        PassType passType = parsePassType(parts[1]);
        int day = new PositiveIntegerParser().parse(parts[2]);

        return Pass
                .newBuilder()
                .setId(id)
                .setDay(day)
                .setType(passType)
                .build();
    }
}
