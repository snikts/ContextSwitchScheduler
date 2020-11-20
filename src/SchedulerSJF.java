import java.util.LinkedList;
import java.util.Queue;

public class SchedulerSJF extends SchedulerBase implements Scheduler {

    Platform platform;
    int contextSwitches = 0;
    LinkedList<Process> schedule = new LinkedList<Process>();
    int loopCounter = 0;

    SchedulerSJF(Platform p) {
        platform = p;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        //contextSwitches = contextSwitches + 1; // add to context switch for new process
        schedule.add(p);
        contextSwitches++;
        //platform.log("Scheduled: " + p.getName());
    }

    @Override
    public Process update(Process cpu) {
        if(cpu == null) {
            int smallestSlot = findSmallest();
            cpu = schedule.remove(smallestSlot);
            platform.log("Scheduled: " + cpu.getName());
            contextSwitches++;
        }
        if(!cpu.isBurstComplete()) {
            return cpu;
        }
        else if(!cpu.isExecutionComplete()) {
            platform.log("Process " + cpu.getName() + " burst complete");
            schedule.add(cpu);
            int smallestSlot = findSmallest();
            cpu = schedule.remove(smallestSlot);
            platform.log("Scheduled: " + cpu.getName());
            contextSwitches++;
            return cpu;
        }
        else {
            //contextSwitches++;
            platform.log("Process " + cpu.getName() + " burst complete");
            platform.log("Process " + cpu.getName() + " execution complete");
            if(schedule.isEmpty()) {
                return null;
            }
            else {
                contextSwitches++;
                int smallestSlot = findSmallest();
                cpu = schedule.remove(smallestSlot);
                platform.log("Scheduled: " + cpu.getName());
                return cpu;
            }
        }
    }

    private int findSmallest() {
        int smallestSlot = 1000;
        for (int i = 0; i < schedule.size(); i++) {
            if(smallestSlot == 1000) {
                smallestSlot = i;
            }
            else {
                if(schedule.get(i).getBurstTime() < schedule.get(smallestSlot).getBurstTime()) {
                    smallestSlot = i;
                }
            }
        }
        return smallestSlot;
    }


}
