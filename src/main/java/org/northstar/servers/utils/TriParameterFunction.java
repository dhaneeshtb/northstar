package org.northstar.servers.utils;



@FunctionalInterface
public interface TriParameterFunction<T,U,V,W> {
    W handle(T p1, U p2, V p3);
}
