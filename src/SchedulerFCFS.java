import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

class SchedulerFCFS extends SchedulerBase implements Scheduler{

    Platform platform;
    int contextSwitches = 0;
    Queue schedule = new LinkedList<Process>();
    int loopCounter = 0;

    SchedulerFCFS(Platform p) {
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


//        while(!schedule.isEmpty() || cpu != null) {
//            loopCounter = loopCounter+1;
//            if (cpu == null && schedule.isEmpty()) {
//                return null;
//            }
//            else if(cpu == null && !schedule.isEmpty()) {
//                cpu = (Process)schedule.remove();
//                contextSwitches++;
//            }
//            else {
//                cpu.update();
//                while (!cpu.isBurstComplete()) {
//                    cpu.update();
//                }
//                platform.log("Process " + cpu.getName() + " burst complete");
//                if (cpu.isExecutionComplete()) {
//                    platform.log("Process " + cpu.getName() + " execution complete");
//                    if(!schedule.isEmpty()) {
//                        cpu = (Process) schedule.remove();
//                        contextSwitches++;
//                    }
//                    else {
//                        return null;
//                    }
////                    return (Process) schedule.remove();
//                }
//                else {
//                    schedule.add(cpu);
//                    contextSwitches++;
//                    platform.log("Scheduled: " + cpu.getName());
//                    if(!schedule.isEmpty()) {
//                        cpu = (Process) schedule.remove();
//                        contextSwitches++;
//                    }
//                    else {
//                        return null;
//                    }
//                }
//
////                return (Process) schedule.remove();
//            }
//        }
//        return null;
    }
}