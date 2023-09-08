package ar.edu.itba.pod.client.properties.parsers;

import ar.edu.itba.pod.client.properties.Parser;
import ar.edu.itba.pod.client.properties.exceptions.parser.ParseException;

public class DayOfYearParser implements Parser<Integer> {
	/**
	 * @param day day of the year in string starting at 1
	 * @return day of the year starting at 0
	 */
	@Override
	public Integer parse(String day) throws ParseException {
		return new IntegerInRangeParser(1, 365 + 1).parse(day) - 1;
	}
}
