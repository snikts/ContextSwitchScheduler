import java.util.LinkedList;
import java.util.Queue;

public class SchedulerRR extends SchedulerBase implements Scheduler {

    Platform platform;
    int contextSwitches = 0;
    Queue schedule = new LinkedList<Process>();
    int loopCounter = 0;
    int quantum = 0;

    SchedulerRR(Platform p, int q) {
        platform = p;
        quantum = q;
    }

    @Override
    public int getNumberOfContextSwitches() {
        return contextSwitches;
    }

    @Override
    public void notifyNewProcess(Process p) {
        schedule.add(p);
        contextSwitches++;
    }

    @Override
    public Process update(Process cpu) {
        if(cpu == null) {
            cpu = (Process) schedule.remove();
            platform.log("Scheduled: " + cpu.getName());
            contextSwitches++;
        }
        if((((cpu.getElapsedTotal() % quantum) != 0) && !cpu.isExecutionComplete()) || cpu.getElapsedTotal() == 0 ) {
            //cpu.update();
            //System.out.println(cpu.getName() + " has executed " + cpu.getElapsedBurst() + " times." );
            if(cpu.isBurstComplete()) {
                contextSwitches++;
                platform.log("Process " + cpu.getName() + " burst complete");
            }
            return cpu;
        }
        else if(!cpu.isExecutionComplete()) {
            //System.out.println("Burst done, execution not");
            //cpu.update();
            if(cpu.isBurstComplete()) {
                platform.log("Process " + cpu.getName() + " burst complete");
                //contextSwitches++;
            }
            if((cpu.getElapsedTotal() % quantum) == 0) {
                platform.log("Time quantum complete for process " + cpu.getName());
                //contextSwitches++;
            }
            contextSwitches++;
            //System.out.println(cpu.getName() + " has executed " + cpu.getElapsedBurst() + " times." );
            schedule.add(cpu);
            cpu = (Process) schedule.remove();
            platform.log("Scheduled: " + cpu.getName());
            return cpu;
        }
        else {
            contextSwitches++;
            //platform.log("Process " + cpu.getName() + " burst complete");
            platform.log("Process " + cpu.getName() + " execution complete");
            if(schedule.isEmpty()) {
                contextSwitches++;
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

}
