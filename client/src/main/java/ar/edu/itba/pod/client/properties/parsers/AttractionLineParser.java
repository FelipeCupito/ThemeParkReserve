package ar.edu.itba.pod.client.properties.parsers;

import services.Park.Attraction;

public class AttractionLineParser extends CSVLineParser<Attraction> {
	public AttractionLineParser() {
		super(4);
	}

	@Override
	Attraction parseFields(String[] parts) {
		// TODO: implement
		throw new UnsupportedOperationException("Unimplemented method 'parseFields'");
		//return Attraction.newBuilder().build();
	}
}
