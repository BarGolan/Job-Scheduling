import java.io.*;

public class Run {

    /*
     * The main function runs the simulator with different input types. input can be
     * command line arguments (number of machines, policy (same one for all
     * machines, number of jobs, round type) or a file in the format attached with
     * this source code. the file details the same parameters as in the command line
     * arguments. in addition the file contains of initial scheduling of the jobs
     * onto the machines and it is possible to dectate a different policy for each
     * machine.
     * When invoked the simulator will run until reaching stable state
     * (Nash Equilibrium) or until reaching a predefined amount of rounds (currently
     * set to maximum 100 rounds)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("missing arguments");
        } else if (args.length == 1) {
            String fileName = args[0];
            readFromFile(fileName);
        } else if (args.length == 5) {
            int machineNum = Integer.parseInt(args[0]); // an int value between 1 - n
            int policy = Integer.parseInt(args[1]); // an int value between 0 - 3 (detailed policies' behavior are in
                                                    // class Machine under insert function)
            double speed = Double.parseDouble(args[2]); // a double value larger than 0
            int jobNum = Integer.parseInt(args[3]); // an int value between 1 - n
            int roundType = Integer.parseInt(args[4]); // an int value 1 (for SPT) or 2 (LPT)
            readFromCommandLine(machineNum, policy, speed, jobNum, roundType);
        } else {
            throw new IllegalArgumentException("too many arguments");
        }
    }

    public static void readFromFile(String fileName) {
        // reads input file
        StdIn.setInput(fileName);
        int fileType = StdIn.readInt(); // 1 for initial scheduling by policy, 2 for manual initial scheduling
        int roundType = StdIn.readInt();
        int machineNum = StdIn.readInt();
        int jobNum = StdIn.readInt();
        Simulator sim = new Simulator(machineNum);
        for (int i = 0; i < machineNum; i++) {
            double speed = StdIn.readDouble();
            int policy = StdIn.readInt();
            if (policy == 4) {
                int[] priorityList = new int[jobNum];
                for (int j = 0; j < jobNum; j++) {
                    priorityList[j] = StdIn.readInt();
                }
                sim.machines[i] = new Machine(i, policy, speed, priorityList);
            } else {
                sim.machines[i] = new Machine(i, policy, speed);
            }
        }
        // checks validity of input
        sim.checkValidity(jobNum);
        if (fileType == 1) {
            sim.addJobs2Sim1(jobNum, roundType);
        } else if (fileType == 2) {
            sim.addJobs2Sim2(jobNum);
        } else {
            throw new IllegalArgumentException(
                    "file type must be 1 (for initial policy scheduling) or 2 (for manual initial scheduling)");
        }
        // prints to console
        System.out.println(sim.toString());
        sim.runSimulator(roundType, fileName);
    }

    public static void readFromFile(String fileName, PrintStream p) {
        // reads input file
        StdIn.setInput(fileName);
        int fileType = StdIn.readInt(); // 1 for initial scheduling by policy, 2 for manual initial scheduling
        int roundType = StdIn.readInt();
        int machineNum = StdIn.readInt();
        int jobNum = StdIn.readInt();
        Simulator sim = new Simulator(machineNum);
        for (int i = 0; i < machineNum; i++) {
            double speed = StdIn.readDouble();
            int policy = StdIn.readInt();
            if (policy == 4) {
                int[] priorityList = new int[jobNum];
                for (int j = 0; j < jobNum; j++) {
                    priorityList[j] = StdIn.readInt();
                }
                sim.machines[i] = new Machine(i, policy, speed, priorityList);
            } else {
                sim.machines[i] = new Machine(i, policy, speed);
            }
        }
        // checks validity of input
        sim.checkValidity(jobNum);
        if (fileType == 1) {
            sim.addJobs2Sim1(jobNum, roundType);
        } else if (fileType == 2) {
            sim.addJobs2Sim2(jobNum);
        } else {
            throw new IllegalArgumentException(
                    "file type must be 1 (for initial policy scheduling) or 2 (for manual initial scheduling)");
        }
        // sets output stream to the given printstream
        System.setOut(p);
        // prints to console
        System.out.println(sim.toString());
        sim.runSimulator(roundType, fileName);
    }

    public static void readFromCommandLine(int machineNum, int policy, double speed, int jobNum, int roundType) {
        PrintStream console = System.out;
        try {
            PrintStream o = new PrintStream(new File("temp.txt"));
            System.setOut(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = "temp.txt";
        System.out.println("1");
        System.out.println(roundType);
        System.out.println(machineNum);
        System.out.println(jobNum);
        System.out.println();
        for (int i = 0; i < machineNum; i++) {
            System.out.println(speed + " " + policy);
        }
        System.out.println();
        for (int i = 0; i < jobNum; i++) {
            double processingTime = (Math.random() * 10 + 1);
            System.out.println(processingTime);
        }
        readFromFile(fileName, console);
    }

    public static void readFromCommandLine2(int machineNum, int policy, double speed, int jobNum, int roundType) {
        Simulator sim = new Simulator(machineNum);
        for (int i = 0; i < sim.machines.length; i++) {
            sim.machines[i] = new Machine(i, policy, speed);
        }
        sim.addJobs2SimRand(jobNum);
        System.out.println(sim.toString());
        sim.runSimulator(roundType);
    }
}
