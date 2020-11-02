### JMH Benchmarks

Run: ./gradlew :benchmarks:jmh:clean :benchmarks:jmh:jmh

If build fails then try `./gradlew --stop` command and then re-run the build
(an [issue](https://github.com/melix/jmh-gradle-plugin/issues/132) with jmh-gradle-plugin).

Performance:
```
Benchmark                                           Mode  Cnt   Score   Error  Units
c.b.r.b.j.concatmap.ConcatMapReaktive.emitter      thrpt   12  10.713 ± 0.166  ops/s
c.b.r.b.j.concatmap.ConcatMapRxJava2.emitter       thrpt   12   9.859 ± 0.021  ops/s
c.b.r.b.j.concatmap.ConcatMapFlow.emitter          thrpt   12   6.284 ± 0.075  ops/s

c.b.r.b.j.concatmap.ConcatMapReaktive.iterable     thrpt   12   9.614 ± 0.020  ops/s
c.b.r.b.j.concatmap.ConcatMapRxJava2.iterable      thrpt   12   8.713 ± 0.008  ops/s
c.b.r.b.j.concatmap.ConcatMapFlow.iterable         thrpt   12  10.051 ± 0.168  ops/s

c.b.r.b.j.filtermap.FilterMapReaktive.emitter      thrpt   12   9.406 ± 0.009  ops/s
c.b.r.b.j.filtermap.FilterMapRxJava2.emitter       thrpt   12  10.863 ± 0.015  ops/s
c.b.r.b.j.filtermap.FilterMapFlow.emitter          thrpt   12   2.626 ± 0.003  ops/s

c.b.r.b.j.filtermap.FilterMapReaktive.iterable     thrpt   12   9.764 ± 0.012  ops/s
c.b.r.b.j.filtermap.FilterMapRxJava2.iterable      thrpt   12   9.046 ± 0.018  ops/s
c.b.r.b.j.filtermap.FilterMapFlow.iterable         thrpt   12   3.314 ± 0.010  ops/s

c.b.r.b.j.flatmap.FlatMapReaktive.emitter          thrpt   12  10.504 ± 0.018  ops/s
c.b.r.b.j.flatmap.FlatMapRxJava2.emitter           thrpt   12   4.277 ± 0.010  ops/s
c.b.r.b.j.flatmap.FlatMapFlow.emitter              thrpt   12   0.598 ± 0.002  ops/s

c.b.r.b.j.flatmap.FlatMapReaktive.iterable         thrpt   12  10.182 ± 0.022  ops/s
c.b.r.b.j.flatmap.FlatMapRxJava2.iterable          thrpt   12  12.834 ± 0.012  ops/s
c.b.r.b.j.flatmap.FlatMapFlow.iterable             thrpt   12   0.608 ± 0.002  ops/s

c.b.r.b.j.scrabble.ScrabbleReaktive.play           thrpt   12  15.437 ± 0.039  ops/s
c.b.r.b.j.scrabble.ScrabbleRxJava2Flowable.play    thrpt   12  12.235 ± 0.086  ops/s
c.b.r.b.j.scrabble.ScrabbleRxJava2Observable.play  thrpt   12  13.916 ± 0.092  ops/s
c.b.r.b.j.scrabble.ScrabbleFlow.play               thrpt   12  15.411 ± 0.042  ops/s
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
