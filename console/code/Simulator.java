public class Simulator {
    /**
     * Represents a simulator.
     * A Simulator has an array of machines, a chronoList (which is a list keeping
     * all the machines in an ascending order of their IDs)
     */
    Machine[] machines;
    List allJobs;

    public Simulator(int machineNum) {
        this.machines = new Machine[machineNum];
        this.allJobs = new List();
    }

    public void checkValidity(int jobNum) {
        for (Machine machine : this.machines) {
            machine.checkValidity();
        }
    }

    public void addJobs2Sim1(int numJobs, int roundType) {
        Machine pseudoMachine = new Machine(-1, 0, 1);
        double inf = Double.POSITIVE_INFINITY;
        Job pseudoJob = new Job(-1, inf, pseudoMachine);
        for (int i = 0; i < numJobs; i++) {
            double processingTime = StdIn.readDouble();
            Job newJob = new Job(i, processingTime, pseudoMachine);
            this.allJobs.addLast(newJob);
        }
        initialSchedule(pseudoMachine, roundType);
    }

    public void initialSchedule(Machine pseudoMachine, int roundType) {
        sortAllJobs(roundType);
        ListIterator iterator = this.allJobs.iterator();
        while (iterator.current != null) {
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = this.bestResponseJobInitial(iterator.current.job, pseudoMachine);
            if (curMachine != bestMachine) {
                this.moveIinitial(iterator.current.job, pseudoMachine, bestMachine);
            }
            iterator.next();
        }
        System.out.println();
    }

    public Machine bestResponseJobInitial(Job job, Machine pseudoMachine) {
        int startIndex = pseudoMachine.jobList.indexOf(job);
        Machine toMachine = pseudoMachine;
        double bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.moveIinitial(job, pseudoMachine, machine);
            if (job.completionTime < bestCompTime) {
                bestCompTime = job.completionTime;
                toMachine = job.runningMachine;
            }
            job.runningMachine.remove(job);
        }
        pseudoMachine.jobList.add(startIndex, job);
        job.setRunningMachine(pseudoMachine);
        this.setCompletionTime();
        return toMachine;
    }

    public void moveIinitial(Job job, Machine currentMachine, Machine destMachine) {
        currentMachine.remove(job);
        ListIterator iterator = currentMachine.jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
        destMachine.insert(job);
    }

    public void addJobs2Sim2(int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            double processingTime = StdIn.readDouble();
            int MachineID = StdIn.readInt();
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    public static void readFromCommandLine(int machineNum, int policy, double speed, int jobNum, int roundType) {
        Simulator sim = new Simulator(machineNum);
        for (int i = 0; i < sim.machines.length; i++) {
            sim.machines[i] = new Machine(i, policy, speed);
        }
        sim.addJobs2SimRand(jobNum);
        System.out.println(sim.toString());
        sim.runSimulator(roundType);
    }

    public void addJobs2SimRand(int numJobs) {
        for (int i = 0; i < numJobs; i++) {
            double processingTime = (Math.random() * 10 + 1);
            int MachineID = (int) (Math.random() * this.machines.length);
            Job newJob = new Job(i, processingTime, this.machines[MachineID]);
            this.allJobs.addLast(newJob);
        }
    }

    /*
     * runs the Simulaor FROM FILE with the required round type (LPT/SPT), where
     * SPT=1 and LPT=2
     */
    public void runSimulator(int lptOrSpt, String fileName) {
        sortAllJobs(lptOrSpt);
        boolean same = false;
        int rounds = 0;
        while (!same && rounds < 100) {
            same = runRound();
            rounds++;
        }
        if (rounds == 100 && !same) {
            System.out.println("Sim ended after reachning stopping condition " + rounds + " rounds.");
        } else {
            System.out.println("Sim reached stable state after " + rounds + " rounds.");
            double optimum = optimum1();
            double makeSpan = makeSpan();
            double quality = makeSpan / optimum;
            System.out.println("A lower bound for the minimal makespan is " + String.format("%.2f", optimum) + ".");
            System.out.println("The current makespan is " + String.format("%.2f", makeSpan) + ".");
            System.out.println("Scheduling quality is " + String.format("%.2f", quality) + ".");
            // double optimum = optimum2(fileName);
            // double sum = compTimeSum();
            // double quality = sum / optimum;
            // System.out.println(
            // "Optimum sum of completion time is " + String.format("%.2f", optimum) + ".");
            // System.out.println(
            // "The current sum of completion time is " + String.format("%.2f", sum) + ".");
            System.out.println();
        }
    }

    /*
     * runs the Simulaor FROM COMMAND LINE with the required round type (LPT/SPT),
     * where SPT = 1 and LPT = 2
     */
    public void runSimulator(int lptOrSpt) {
        sortAllJobs(lptOrSpt);
        boolean same = false;
        int rounds = 0;
        while (!same && rounds < 100) {
            same = runRound();
            rounds++;
        }
        if (rounds == 100 && !same) {
            System.out.println("Sim ended after reachning stopping condition " + rounds +
                    " rounds.");
        } else {
            System.out.println("Sim reached stable state after " + rounds + " rounds.");
            double optimum = optimum1();
            double makeSpan = makeSpan();
            double quality = makeSpan / optimum;
            System.out.println("A lower bound for the minimal makespan is " + String.format("%.2f", optimum) + ".");
            System.out.println("The current makespan is " + String.format("%.2f", makeSpan) + ".");
            System.out.println("Scheduling quality is " + String.format("%.2f", quality) + ".");
            System.out.println();
        }
    }

    private void sortAllJobs(int lptOrSpt) {
        if (lptOrSpt == 1) {
            allJobs.sort();
        } else if (lptOrSpt == 2) {
            allJobs.sortReverse();
        } else {
            throw new IllegalArgumentException("round type must be between 1 (for SPT) or 2 (for LPT)");
        }
    }

    private boolean runRound() {
        ListIterator iterator = this.allJobs.iterator();
        int[] same = new int[this.allJobs.getSize()];
        for (int i = 0; i < same.length; i++) {
            same[i] = 0;
        }
        while (iterator.current != null) {
            System.out.println("Started BRJ for job: " + iterator.current.job.getID());
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = this.bestResponseJob(iterator.current.job);
            if (curMachine != bestMachine) {
                this.move(iterator.current.job, bestMachine);
                System.out.println("finished BRJ, moved to: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + String.format("%.2f", iterator.current.job.completionTime));
                same[iterator.current.job.getID()] = 0;
            } else {
                System.out.println("finished BRJ, stayed at: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + String.format("%.2f", iterator.current.job.completionTime));
                same[iterator.current.job.getID()] = 1;
            }
            System.out.println();
            System.out.println(this.toString());
            iterator.next();
        }
        return checkStable(same);
    }

    private boolean checkStable(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 1)
                return false;
        }
        return true;
    }

    public Machine bestResponseJob(Job job) {
        Machine fromMachine = job.runningMachine;
        int startIndex = fromMachine.jobList.indexOf(job);
        Machine toMachine = job.runningMachine;
        double bestCompTime = job.completionTime;
        for (Machine machine : this.machines) {
            this.move(job, machine);
            if (job.completionTime < bestCompTime) {
                bestCompTime = job.completionTime;
                toMachine = job.runningMachine;
            }
        }
        job.runningMachine.remove(job);
        fromMachine.jobList.add(startIndex, job);
        job.setRunningMachine(fromMachine);
        this.setCompletionTime();
        return toMachine;
    }

    public void move(Job job, Machine destMachineID) {
        int currMachineID = job.runningMachine.getID();
        machines[currMachineID].remove(job);
        ListIterator iterator = machines[currMachineID].jobList.iterator();
        while (iterator.current != null) {
            iterator.current.job.setCompletionTime();
            iterator.next();
        }
        destMachineID.insert(job);
    }

    public void setCompletionTime() {
        for (Machine machine : machines) {
            machine.setCompletionTime();
        }
    }

    /*
     * computes a lower bound for the makespan. the makespan is the latest
     * completion time of a job as suppose to all of the jobs on all of the machines
     */
    private double optimum1() {
        double speedSum = speedSum();
        double ProcessingTimeSum = ProcessingTimeSum();
        return ProcessingTimeSum / speedSum;
    }

    private double speedSum() {
        double sum = 0;
        for (Machine machine : this.machines) {
            double curSpeed = machine.getSpeed();
            sum += curSpeed;
        }
        return sum;
    }

    private double ProcessingTimeSum() {
        double sum = 0;
        ListIterator iterator = this.allJobs.iterator();
        while (iterator.current != null) {
            sum += iterator.current.job.getProcessingTime();
            iterator.current = iterator.current.next;
        }
        return sum;
    }

    /*
     * computes the makespan after reaching stable state (NE)
     */
    public double makeSpan() {
        double max = 0;
        for (Machine machine : this.machines) {
            double curMaxCompTime = machine.jobList.getLast().job.getcompletionTime();
            if (curMaxCompTime > max) {
                max = curMaxCompTime;
            }
        }
        return max;
    }

    /*
     * computes the sum of all completion times after initial scheduling of jobs
     * onto the machines
     */
    public static double optimum2(String fileName) {
        StdIn.setInput(fileName);
        int roundType = StdIn.readInt();
        int machineNum = StdIn.readInt();
        int jobNum = StdIn.readInt();
        Simulator optimalSim = new Simulator(machineNum);
        for (int i = 0; i < machineNum; i++) {
            double speed = StdIn.readDouble();
            int policy = StdIn.readInt();
            if (policy == 4) {
                int[] priorityList = new int[jobNum];
                for (int j = 0; j < jobNum; j++) {
                    priorityList[j] = StdIn.readInt();
                }
                optimalSim.machines[i] = new Machine(i, policy, speed, priorityList);
            } else {
                optimalSim.machines[i] = new Machine(i, policy, speed);
            }
        }
        optimalSim.addJobs2Sim1(jobNum, 1);
        double sum = 0;
        for (Machine machine : optimalSim.machines) {
            ListIterator itr = machine.jobList.iterator();
            while (itr.current != null) {
                sum += itr.current.job.getcompletionTime();
                itr.current = itr.current.next;
            }
        }
        return sum;
    }

    /*
     * computes the sum of all completion times after reaching stable state (NE) of
     * jobs onto the machines
     */
    private double compTimeSum() {
        double sum = 0;
        for (Machine machine : this.machines) {
            ListIterator itr = machine.jobList.iterator();
            while (itr.current != null) {
                sum += itr.current.job.getcompletionTime();
                itr.current = itr.current.next;
            }
        }
        return sum;
    }

    public int checkFirstNull() {
        for (int i = 0; i < machines.length; i++) {
            if (machines[i].jobList.getFirst() == null)
                return i;
        }
        return -1;
    }

    public void testMove(Job job, Machine destMachineID) {
        try {
            this.move(job, destMachineID);
        } catch (Exception e) {
            System.out.println("Exception: No such job/machine exists.\n");
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Machine machine : machines) {
            s.append(machine.toString());
        }
        return s.toString();
    }
}
