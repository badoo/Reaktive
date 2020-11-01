### JMH Benchmarks

Run: ./gradlew :benchmarks:jmh:clean :benchmarks:jmh:jmh

If build fails then try `./gradlew --stop` command and then re-run the build
(an [issue](https://github.com/melix/jmh-gradle-plugin/issues/132) with jmh-gradle-plugin).

Performance:
```
Benchmark                                          Mode  Cnt     Score    Error  Units
c.b.r.b.j.concatmap.ConcatMapReaktive.emitter      avgt   10    92.041 ± 0.187  ms/op
c.b.r.b.j.concatmap.ConcatMapRxJava2.emitter       avgt   10   101.558 ± 0.244  ms/op
c.b.r.b.j.concatmap.ConcatMapFlow.emitter          avgt   10   157.162 ± 0.294  ms/op

c.b.r.b.j.concatmap.ConcatMapReaktive.iterable     avgt   10   101.847 ± 0.180  ms/op
c.b.r.b.j.concatmap.ConcatMapRxJava2.iterable      avgt   10   114.767 ± 0.123  ms/op
c.b.r.b.j.concatmap.ConcatMapFlow.iterable         avgt   10    97.916 ± 0.057  ms/op

c.b.r.b.j.filtermap.FilterMapReaktive.emitter      avgt   10   106.277 ± 0.179  ms/op
c.b.r.b.j.filtermap.FilterMapRxJava2.emitter       avgt   10    92.000 ± 0.121  ms/op
c.b.r.b.j.filtermap.FilterMapFlow.emitter          avgt   10   379.816 ± 0.347  ms/op

c.b.r.b.j.filtermap.FilterMapReaktive.iterable     avgt   10   102.410 ± 0.105  ms/op
c.b.r.b.j.filtermap.FilterMapRxJava2.iterable      avgt   10   111.155 ± 0.332  ms/op
c.b.r.b.j.filtermap.FilterMapFlow.iterable         avgt   10   306.474 ± 2.500  ms/op

c.b.r.b.j.flatmap.FlatMapReaktive.emitter          avgt   10    95.124 ± 0.066  ms/op
c.b.r.b.j.flatmap.FlatMapRxJava2.emitter           avgt   10   233.668 ± 0.316  ms/op
c.b.r.b.j.flatmap.FlatMapFlow.emitter              avgt   10  1658.398 ± 2.062  ms/op

c.b.r.b.j.flatmap.FlatMapReaktive.iterable         avgt   10    98.461 ± 0.301  ms/op
c.b.r.b.j.flatmap.FlatMapRxJava2.iterable          avgt   10    78.183 ± 0.185  ms/op
c.b.r.b.j.flatmap.FlatMapFlow.iterable             avgt   10  1636.532 ± 4.495  ms/op

c.b.r.b.j.scrabble.ScrabbleReaktive.play           avgt   10    65.805 ± 0.210  ms/op
c.b.r.b.j.scrabble.ScrabbleRxJava2Flowable.play    avgt   10    81.461 ± 0.811  ms/op
c.b.r.b.j.scrabble.ScrabbleRxJava2Observable.play  avgt   10    73.332 ± 0.425  ms/op
c.b.r.b.j.scrabble.ScrabbleFlow.play               avgt   10    64.422 ± 0.189  ms/op
```

<img src="https://raw.githubusercontent.com/badoo/Reaktive/master/assets/benchmarks_performance.png">

Memory consumption:
```
ScrabbleReaktive.play:·gc.alloc.rate.norm                        avgt    8  151267014.250 ±      295.145    B/op
ScrabbleRxJava2Flowable.play:·gc.alloc.rate.norm                 avgt    8  144068457.089 ±      377.571    B/op
ScrabbleRxJava2Observable.play:·gc.alloc.rate.norm               avgt    8  134947756.262 ±      319.042    B/op
ScrabbleFlow.play:·gc.alloc.rate.norm                            avgt    8  236901936.161 ±      305.922    B/op
```

<img src="https://raw.githubusercontent.com/badoo/Reaktive/master/assets/benchmarks_memory.png">
