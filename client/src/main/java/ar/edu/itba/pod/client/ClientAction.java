package ar.edu.itba.pod.client;

import java.io.IOException;

public interface ClientAction {
	void run(Clients clients) throws IOException;
}
