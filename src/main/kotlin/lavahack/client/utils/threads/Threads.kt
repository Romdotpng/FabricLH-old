package lavahack.client.utils.threads

import lavahack.client.callback.Callback
import lavahack.client.utils.Stopwatch
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

private val indexer = AtomicInteger(0)

val factory get() = buildFactory(true, "LHF-Thread-%d")
val executor get() = Executors.newCachedThreadPool(factory)


fun buildFactory(
    daemon : Boolean,
    name : String
) = ThreadFactory { it0 ->
    Executors.defaultThreadFactory().newThread(it0).also { it1 ->
        it1.isDaemon = daemon
        it1.name = name.format(indexer.addAndGet(1))
    }
}

fun Callback.Delayed.handle(
    state : Boolean,
    delay : Long
) {
    if(state) {
        if(stopwatch.passed(delay, true)) {
            executor.submit(invoker)
        }
    } else {
        invoke()

        stopwatch.reset()
    }
}

fun delayedTask(
    delay : Long,
    block : () -> Unit
) {
    executor.submit {
        Thread.sleep(delay)

        block()
    }
}