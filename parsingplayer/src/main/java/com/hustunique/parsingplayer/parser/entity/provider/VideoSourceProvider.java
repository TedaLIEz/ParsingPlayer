package com.hustunique.parsingplayer.parser.entity.provider;

/**
 * Created by JianGuo on 1/29/17.
 * Interface providing video info in different qualities.
 */
interface VideoSourceProvider<V> {
    String provideSource(V v);
}
