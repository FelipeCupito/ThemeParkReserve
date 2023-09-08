package ar.edu.itba.pod.client.properties.parsers;

import services.Park.Pass;

public class PassLineParser extends CSVLineParser<Pass> {
	public PassLineParser() {
		super(3);
	}

	@Override
	Pass parseFields(String[] parts) {
		// TODO: implement
		throw new UnsupportedOperationException("Unimplemented method 'parseFields'");
		//return Pass.newBuilder().build();
	}
}
