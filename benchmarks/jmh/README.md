### JMH Benchmarks

Run: ./gradlew :benchmarks:jmh:clean :benchmarks:jmh:jmh

If build fails then try `./gradlew --stop` command and then re-run the build
(an [issue](https://github.com/melix/jmh-gradle-plugin/issues/132) with jmh-gradle-plugin).

Performance:
```
Benchmark                                          Mode  Cnt     Score    Error  Units
c.b.r.b.j.concatmap.ConcatMapReaktive.emitter      avgt    8   103.097 ±  4.609  ms/op
c.b.r.b.j.concatmap.ConcatMapRxJava2.emitter       avgt    8    87.609 ±  2.274  ms/op
c.b.r.b.j.concatmap.ConcatMapFlow.emitter          avgt    8   106.142 ±  1.771  ms/op

c.b.r.b.j.concatmap.ConcatMapReaktive.iterable     avgt    8    86.155 ±  1.459  ms/op
c.b.r.b.j.concatmap.ConcatMapRxJava2.iterable      avgt    8   102.483 ±  4.564  ms/op
c.b.r.b.j.concatmap.ConcatMapFlow.iterable         avgt    8   118.394 ±  2.843  ms/op

c.b.r.b.j.filtermap.FilterMapReaktive.emitter      avgt    8   108.263 ±  3.073  ms/op
c.b.r.b.j.filtermap.FilterMapRxJava2.emitter       avgt    8    85.092 ±  1.365  ms/op
c.b.r.b.j.filtermap.FilterMapFlow.emitter          avgt    8   109.349 ±  2.918  ms/op

c.b.r.b.j.filtermap.FilterMapReaktive.iterable     avgt    8   101.609 ±  3.603  ms/op
c.b.r.b.j.filtermap.FilterMapRxJava2.iterable      avgt    8    98.549 ±  1.870  ms/op
c.b.r.b.j.filtermap.FilterMapFlow.iterable         avgt    8   122.631 ±  2.853  ms/op

c.b.r.b.j.flatmap.FlatMapReaktive.emitter          avgt    8    95.816 ±  2.847  ms/op
c.b.r.b.j.flatmap.FlatMapRxJava2.emitter           avgt    8   270.999 ±  8.355  ms/op
c.b.r.b.j.flatmap.FlatMapFlow.emitter              avgt    8  1847.138 ± 24.037  ms/op

c.b.r.b.j.flatmap.FlatMapReaktive.iterable         avgt    8   119.224 ±  5.981  ms/op
c.b.r.b.j.flatmap.FlatMapRxJava2.iterable          avgt    8    76.765 ±  1.906  ms/op
c.b.r.b.j.flatmap.FlatMapFlow.iterable             avgt    8  1874.016 ± 49.353  ms/op

c.b.r.b.j.scrabble.ScrabbleReaktive.play           avgt    8    82.414 ±  3.572  ms/op
c.b.r.b.j.scrabble.ScrabbleRxJava2Flowable.play    avgt    8   101.706 ±  6.522  ms/op
c.b.r.b.j.scrabble.ScrabbleRxJava2Observable.play  avgt    8    89.607 ±  5.796  ms/op
c.b.r.b.j.scrabble.ScrabbleFlow.play               avgt    8    81.062 ±  4.399  ms/op
```

Memory consumption:
```
ScrabbleReaktive.play:·gc.alloc.rate.norm                        avgt    8  154974354.398 ±      347.089    B/op
ScrabbleRxJava2Flowable.play:·gc.alloc.rate.norm                 avgt    8  144411114.635 ±      417.309    B/op
ScrabbleRxJava2Observable.play:·gc.alloc.rate.norm               avgt    8  134947769.672 ±      379.649    B/op
ScrabbleFlow.play:·gc.alloc.rate.norm                            avgt    8  262035986.160 ±      346.498    B/op
```
