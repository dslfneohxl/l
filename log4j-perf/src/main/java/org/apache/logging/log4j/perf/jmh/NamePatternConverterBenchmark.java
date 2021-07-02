package org.apache.logging.log4j.perf.jmh;

import org.apache.logging.log4j.core.AbstractLogEvent;
import org.apache.logging.log4j.core.pattern.LoggerPatternConverter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * Tests Log4j2 NamePatternConverter's performance.<br>
 *
 * How to run these benchmarks:<br>
 *
 * Single thread:<br>
 * <pre>java -jar log4j-perf/target/benchmarks.jar ".*NamePatternConverterBenchmark.*" -f 1 -wi 2 -i 3 -r 3s -jvmArgs '-server -XX:+AggressiveOpts'</pre>
 *
 * Multiple threads (for example, 4 threads):<br>
 * <pre>java -jar log4j-perf/target/benchmarks.jar ".*NamePatternConverterBenchmark.*" -f 1 -wi 4 -i 20 -t 4 -si true</pre>
 *
 * Usage help:<br>
 * <pre>java -jar log4j-perf/target/benchmarks.jar -help</pre>
 */
public class NamePatternConverterBenchmark {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        final String[] classNames = new String[] {
                "org.bogus.hokus.pokus.Clazz1",
                "com.bogus.hokus.pokus.Clazz2",
                "edu.bogus.hokus.pokus.a.Clazz3",
                "de.bogus.hokus.b.Clazz4",
                "jp.bogus.c.Clazz5",
                "cn.d.Clazz6"
        };

        final LoggerPatternConverter converter = LoggerPatternConverter.newInstance(new String[] {"1."});

        AbstractLogEvent getEvent() {
            int idx = (int) Math.random() * classNames.length;
            return new BenchmarkLogEvent(classNames[idx]);
        }

        StringBuilder getDestination() {
            return new StringBuilder();
        }
    }

    @Benchmark
    public void benchNamePatternConverter(ExecutionPlan plan) {
        plan.converter.format(plan.getEvent(), plan.getDestination());
    }

    private static class BenchmarkLogEvent extends AbstractLogEvent {
        private final String loggerName;

        BenchmarkLogEvent(String loggerName) {
            this.loggerName = loggerName;
        }

        @Override
        public String getLoggerName() {
            return loggerName;
        }
    }

}
