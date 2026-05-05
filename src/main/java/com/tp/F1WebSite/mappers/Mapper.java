package com.tp.F1WebSite.mappers;

public interface Mapper<A, B> {

    B mapTo(A a);

    A mapFrom(B b);
}
