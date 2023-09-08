package ar.edu.itba.pod.client;

public interface Client<P> {
    void run(P properties);
}
