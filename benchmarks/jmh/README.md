### JMH Benchmarks

Run: ./gradlew :benchmarks:jmh:clean :benchmarks:jmh:jmh

If build fails then try `./gradlew --stop` command and then re-run the build
(an [issue](https://github.com/melix/jmh-gradle-plugin/issues/132) with jmh-gradle-plugin).

Latest results:
```
c.b.r.b.j.concatmap.ConcatMapFlow.run              avgt   10   15.563 ±  0.131  ms/op
c.b.r.b.j.concatmap.ConcatMapReaktive.run          avgt   10   28.118 ±  0.184  ms/op
c.b.r.b.j.concatmap.ConcatMapRxJava2.run           avgt   10   15.576 ±  0.214  ms/op
c.b.r.b.j.filtermap.FilterMapFlow.run              avgt   10   54.658 ±  0.930  ms/op
c.b.r.b.j.filtermap.FilterMapReaktive.run          avgt   10   54.968 ±  0.345  ms/op
c.b.r.b.j.filtermap.FilterMapRxJava2.run           avgt   10   58.880 ±  3.446  ms/op
c.b.r.b.j.flatmap.FlatMapFlow.run                  avgt   10  456.074 ± 12.841  ms/op
c.b.r.b.j.flatmap.FlatMapReaktive.run              avgt   10   28.285 ±  0.199  ms/op
c.b.r.b.j.flatmap.FlatMapRxJava2.run               avgt   10   16.222 ±  0.069  ms/op
c.b.r.b.j.scrabble.ScrabbleFlow.play               avgt   10   80.879 ±  2.509  ms/op
c.b.r.b.j.scrabble.ScrabbleReaktive.play           avgt   10   90.110 ±  2.463  ms/op
c.b.r.b.j.scrabble.ScrabbleRxJava2Flowable.play    avgt   10   95.889 ±  3.346  ms/op
c.b.r.b.j.scrabble.ScrabbleRxJava2Observable.play  avgt   10   88.702 ±  0.914  ms/op
```
