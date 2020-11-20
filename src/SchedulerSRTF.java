import java.util.LinkedList;
import java.util.Queue;

public class SchedulerSRTF extends SchedulerBase implements Scheduler {

    Platform platform;
    int contextSwitches = 0;
    LinkedList<Process> schedule = new LinkedList<Process>();
    int loopCounter = 0;

    SchedulerSRTF(Platform p) {
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
            if(schedule.size() != 0) {
                int smallestSlot = findSmallest();
                int timeRemaining = schedule.get(smallestSlot).getTotalTime() - schedule.get(smallestSlot).getElapsedTotal();
                int cpuTime = cpu.getTotalTime() - cpu.getElapsedTotal();
                if (timeRemaining > cpuTime) {
                    return cpu;
                } else {
                    contextSwitches++;
                    schedule.add(cpu);
                    Process toReturn = schedule.remove(smallestSlot);
                    platform.log("Preemptively removed: " + cpu.getName());
                    platform.log("Scheduled: " + toReturn.getName());
                    contextSwitches++;
                    return toReturn;
                }
            }
            else {
                return cpu;
            }
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
                int remainingTime1 = schedule.get(i).getTotalTime() - schedule.get(i).getElapsedTotal();
                int remainingTime2 = schedule.get(smallestSlot).getTotalTime() - schedule.get(smallestSlot).getElapsedTotal();
                if(remainingTime1 < remainingTime2) {
                    smallestSlot = i;
                }
            }
        }
        return smallestSlot;
    }


}
