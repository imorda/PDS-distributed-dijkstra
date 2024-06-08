package solution

import internal.Environment

/**
 * Distributed Dijkstra algorithm implementation.
 * All functions are called from the single main thread.
 *
 * @author Timofey Belousov
 */

class DijkstraProcessImpl(private val env: Environment) : DijkstraProcess {
    private var distToRoot: Long? = null
    private var balance = 0
    private var children = 0
    private var parentId = -1

    data class MyMessage(val value: Long) :
        java.io.Serializable // >=0 = your new distToRoot = x. -1 = I am your son. -2 = I am not your son. -3 = I am NO LONGER your son


    override fun onMessage(senderPid: Int, message: Any) {
        if (message !is MyMessage) {
            throw IllegalArgumentException("message is not MyMessage")
        }

        if (message.value == -1L) {  // I am your son
            balance--
            children++
        } else if (message.value == -2L) { // I am not your son
            balance--

            if (balance == 0 && children == 0) {
                if (parentId == -1) { // Is root
                    env.finishExecution()
                    return
                }
                env.send(parentId, MyMessage(-3)) // Send status I am NO LONGER your son
                parentId = -1
            }
        } else if (message.value == -3L) {  // I am NO LONGER your son
            children--

            if (balance == 0 && children == 0) {
                if (parentId == -1) { // Is root
                    env.finishExecution()
                    return
                }
                env.send(parentId, MyMessage(-3)) // Send status I am NO LONGER your son
                parentId = -1
            }
        } else {
            var curDistToRoot = distToRoot
            if (curDistToRoot != null && message.value >= curDistToRoot) {  // Refuse calculating less effective route, send "done" immediately
                env.send(senderPid, MyMessage(-2))
                return
            }

            if (parentId != -1) {
                env.send(senderPid, MyMessage(-2))  // Don't change parents!!!
            } else {
                parentId = senderPid
                env.send(parentId, MyMessage(-1))  // Notify your new parent that it is a parent now
            }

            curDistToRoot = message.value

            for ((i, dist) in env.neighbours) {
                if (i == env.processId) {  // Nonsense
                    continue
                }

                if (dist < 0) {
                    throw IllegalArgumentException("Negative edges are unsupported")
                }

                env.send(i, MyMessage(curDistToRoot + dist))
                balance++
            }

            if (balance == 0 && children == 0) {
                env.send(parentId, MyMessage(-3))
                parentId = -1
            }
            distToRoot = curDistToRoot
        }
    }

    override fun getDistance(): Long? = distToRoot

    override fun onComputationStart() {
        distToRoot = 0
        parentId = -1
        children = 0

        for ((i, dist) in env.neighbours) {
            if (i == env.processId) {
                continue
            }

            if (dist < 0) {
                throw IllegalArgumentException("Negative edges are unsupported")
            }

            env.send(i, MyMessage(dist))
            balance++
        }

        if (balance == 0 && children == 0) {
            env.finishExecution()
        }
    }
}
