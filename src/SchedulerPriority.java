import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Iterator;

public class SchedulerPriority extends SchedulerBase implements Scheduler, Comparator<Process> {

    Platform platform;
    int contextSwitches = 0;
    PriorityQueue<Process> schedule = new PriorityQueue<Process>(1, this::compare);
    int loopCounter = 0;

    SchedulerPriority(Platform p) {
        platform = p;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        schedule.add(p);
    }

    @Override
    public Process update(Process cpu) {
        if(cpu == null) {
            cpu = (Process) schedule.remove();
            platform.log("Scheduled: " + cpu.getName());
            contextSwitches++;
        }
        if(!cpu.isBurstComplete()) {
            //cpu.update();
            //System.out.println(cpu.getName() + " has executed " + cpu.getElapsedBurst() + " times." );
            return cpu;
        }
        else if(!cpu.isExecutionComplete()) {
            //System.out.println("Burst done, execution not");
            //cpu.update();
            platform.log("Process " + cpu.getName() + " burst complete");
            //System.out.println(cpu.getName() + " has executed " + cpu.getElapsedBurst() + " times." );
            schedule.add(cpu);
            cpu = (Process) schedule.remove();
            platform.log("Scheduled: " + cpu.getName());
            contextSwitches++;
            return cpu;
        }
        else {
            contextSwitches++;
            platform.log("Process " + cpu.getName() + " burst complete");
            platform.log("Process " + cpu.getName() + " execution complete");
            if(schedule.isEmpty()) {
                return null;
            }
            else {
                contextSwitches++;
                cpu = (Process) schedule.remove();
                platform.log("Scheduled: " + cpu.getName());
                return cpu;
            }
        }
    }

    public int compare(Process p1, Process p2) {
        if(p1.getPriority() < p2.getPriority()) {
            return -1;
        }
        else if(p1.getPriority() > p2.getPriority()) {
            return 1;
        }
        else {
            return 0;
        }
    }

}
