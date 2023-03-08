import java.awt.Color;
import java.lang.Thread;
import java.awt.Font;

class Animation {

    int width;
    int height;
    Simulator sim;
    int roundType;
    AnimatedJob[][] schedule;
    double[] locations;

    static int COLOR_FRAME = 1500;
    static double MACHINE_SIZEX_PREDEFINED = 75;
    static double MACHINE_SIZEY_PREDEFINED = 50;
    static double MACHINE_SIZEX = MACHINE_SIZEX_PREDEFINED;
    static double MACHINE_SIZEY = MACHINE_SIZEY_PREDEFINED;
    static double JOB_SIZEX = MACHINE_SIZEX;
    static double JOB_SIZEY = MACHINE_SIZEY;
    static int LONGEST_JOB_LIST = 0;

    public Animation(int width, int height, Simulator sim, int roundType) {
        this.width = width;
        this.height = height;
        this.sim = sim;
        this.roundType = roundType;
        this.schedule = new AnimatedJob[sim.machines.length][sim.allJobs.getSize()];
        this.locations = new double[sim.machines.length * 2];
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(-20, 1300);
        StdDraw.setYscale(0, height + 30);
    }

    public void drawMachines() {
        MACHINE_SIZEX = MACHINE_SIZEX_PREDEFINED;
        MACHINE_SIZEY = MACHINE_SIZEY_PREDEFINED;
        int machineNum = sim.machines.length;
        double x = MACHINE_SIZEX;
        double y = height / 2;
        if (machineNum == 1) {
            drawMachine(sim.machines[0], x, y, MACHINE_SIZEX, MACHINE_SIZEY);
            locations[0] = x;
            locations[1] = y;
        } else {
            double scalingFactor = (machineNum * 100) / MACHINE_SIZEY;
            double shifingFactor = height / (machineNum + 2);
            y = height - shifingFactor;
            MACHINE_SIZEX = MACHINE_SIZEX - scalingFactor;
            MACHINE_SIZEY = MACHINE_SIZEY - scalingFactor;
            for (Machine machine : sim.machines) {
                locations[machine.getID() * 2] = x;
                locations[(machine.getID() * 2) + 1] = y;
                drawMachine(machine, x, y, MACHINE_SIZEX, MACHINE_SIZEY);
                y = y - shifingFactor;
            }
        }
    }

    public void drawMachine(Machine m, double x, double y, double sizeX, double sizeY) {
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        StdDraw.filledRectangle(x, y, sizeX, sizeY);
        StdDraw.setPenColor(StdDraw.BLACK);
        if (sim.machines.length <= 10) {
            Font font = new Font("Arial", Font.BOLD, 16);
            StdDraw.setFont(font);
            StdDraw.text(x, y, "Machine: " + m.getID());
        } else {
            Font font = new Font("Arial", Font.BOLD, 12);
            StdDraw.setFont(font);
            StdDraw.text(x, y, "Machine: " + m.getID());
        }
    }

    public void drawJobs() {
        findLongestJobList();
        for (int i = 0; i < sim.machines.length; i++) {
            double x = locations[i * 2];
            double y = locations[(i * 2) + 1];
            double widthToEdge = width - (2 * MACHINE_SIZEX) - 30;
            double space = widthToEdge / LONGEST_JOB_LIST;
            if (space * 0.4 > MACHINE_SIZEX) {
                space = MACHINE_SIZEX / 0.4;
            }
            JOB_SIZEX = space * 0.4;
            JOB_SIZEY = (2.0 / 3.0) * JOB_SIZEX;
            if (JOB_SIZEY > MACHINE_SIZEY) {
                JOB_SIZEY = MACHINE_SIZEY;
            }
            int jobListLength = sim.machines[i].jobList.getSize();
            for (int j = 0; j < jobListLength; j++) {
                Job job = sim.machines[i].jobList.getJob(j);
                if (j == 0) {
                    AnimatedJob aniJob = new AnimatedJob(job, x + MACHINE_SIZEX + 0.6 * space, y, JOB_SIZEX,
                            JOB_SIZEY);
                    this.schedule[i][j] = aniJob;
                    drawJob(job, x + MACHINE_SIZEX + 0.6 * space, y, JOB_SIZEX,
                            JOB_SIZEY);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.line(x + MACHINE_SIZEX, y, x + MACHINE_SIZEX + 0.2 * space, y);
                    x = x + MACHINE_SIZEX + 0.6 * space;
                } else {
                    AnimatedJob aniJob = new AnimatedJob(job, x + JOB_SIZEX + 0.6 * space, y, JOB_SIZEX,
                            JOB_SIZEY);
                    this.schedule[i][j] = aniJob;
                    drawJob(job, x + JOB_SIZEX + 0.6 * space, y, JOB_SIZEX,
                            JOB_SIZEY);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.line(x + JOB_SIZEX, y, x + JOB_SIZEX + 0.2 * space, y);
                    x = x + JOB_SIZEX + 0.6 * space;
                }
            }
        }
    }

    public void drawAnimatedJobs(AnimatedJob moving) {
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                AnimatedJob currAniJob = schedule[i][j];
                if (currAniJob != null) {
                    double x = currAniJob.getX();
                    double y = currAniJob.getY();
                    if (currAniJob != moving) {
                        drawJob(currAniJob.getJob(), x, y, JOB_SIZEX, JOB_SIZEY);
                        StdDraw.setPenColor(StdDraw.BLACK);
                        StdDraw.line(x - 1.5 * JOB_SIZEX, y, x - JOB_SIZEX, y);
                    } else {
                        drawJob(currAniJob.getJob(), x, y, JOB_SIZEX, JOB_SIZEY);
                    }
                }
            }
        }
    }

    public void drawJob(Job j, double x, double y, double sizeX, double sizeY) {
        StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.filledRectangle(x, y, sizeX, sizeY);
        StdDraw.setPenColor(StdDraw.BLACK);
        String s1 = "Job: " + j.getID();
        String s2 = "PT: " + String.format("%.2f", j.getProcessingTime());
        String s3 = "CT: " + String.format("%.2f", j.getcompletionTime());
        if (sim.machines.length <= 5) {
            Font font = new Font("Arial", Font.PLAIN, 12);
            StdDraw.setFont(font);
            StdDraw.text(x, y + 17, s1);
            StdDraw.text(x, y, s2);
            StdDraw.text(x, y - 17, s3);
        } else {
            Font font = new Font("Arial", Font.PLAIN, 10);
            StdDraw.setFont(font);
            StdDraw.text(x, y + 15, s1);
            StdDraw.text(x, y, s2);
            StdDraw.text(x, y - 15, s3);
        }
    }

    public void runAnimation() {
        sim.sortAllJobs(roundType);
        boolean same = false;
        int rounds = 0;
        StdDraw.enableDoubleBuffering();
        drawMachines();
        drawJobs();
        StdDraw.show();
        StdDraw.disableDoubleBuffering();
        while (!same && rounds < 100) {
            same = runRound();
            rounds++;
        }
        if (rounds == 100 && !same) {
            String s1 = "Sim ended after reachning stopping condition " + rounds + " rounds.";
            System.out.println(s1);
            printToScreen(185, 700, s1, StdDraw.BLACK, 18);
        } else {
            String s2 = "Sim reached stable state after " + rounds + " rounds.";
            System.out.println(s2);
            printToScreen(185, 700, s2, StdDraw.BLACK, 18);
            double optimum = sim.optimum1();
            double makeSpan = sim.makeSpan();
            double quality = makeSpan / optimum;
            String s3 = "A lower bound for the minimal makespan is " + String.format("%.2f", optimum) + ". ";
            System.out.println(s3);
            String s4 = "The current makespan is " + String.format("%.2f", makeSpan) + ". ";
            System.out.println(s4);
            String s5 = "Scheduling quality is " + String.format("%.2f", quality) + ". ";
            System.out.println(s5);
            printToScreen(430, 670, s3 + s4 + s5, StdDraw.BLACK, 16);
            System.out.println();
        }
    }

    private boolean runRound() {
        ListIterator iterator = sim.allJobs.iterator();
        int[] same = new int[sim.allJobs.getSize()];
        for (int i = 0; i < same.length; i++) {
            same[i] = 0;
        }
        while (iterator.current != null) {
            int currID = iterator.current.job.getID();
            System.out.println("Started BRD for job: " + currID);
            // color the frame of the job in green
            colorFrame(currID, StdDraw.GREEN);
            Machine curMachine = iterator.current.job.runningMachine;
            Machine bestMachine = sim.bestResponseJob(iterator.current.job);
            if (curMachine != bestMachine) {
                AnimatedJob newLocation = animatedMove(iterator.current.job, bestMachine);
                colorFrame(currID, StdDraw.RED);
                removeFromSchedule(iterator.current.job);
                sim.move(iterator.current.job, bestMachine);
                insertToSchedule(newLocation, bestMachine);
                switchPics();
                colorFrame(currID, StdDraw.RED);
                unColorFrame(currID);
                System.out.println("finished BRD, moved to: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + String.format("%.2f", iterator.current.job.completionTime));
                same[iterator.current.job.getID()] = 0;
            } else {
                System.out.println("finished BRD, stayed at: " + iterator.current.job.runningMachine.ID
                        + " <mach | time> " + String.format("%.2f", iterator.current.job.completionTime));
                same[iterator.current.job.getID()] = 1;
                unColorFrame(currID);
            }
            System.out.println();
            System.out.println(sim.toString());
            iterator.next();
        }
        return sim.checkStable(same);
    }

    public void switchPics() {
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledRectangle(width / 2, height / 2, width, height);
        // fadeOut(width / 2, height / 2, width, height);
        StdDraw.enableDoubleBuffering();
        drawMachines();
        drawJobs();
        StdDraw.show();
        StdDraw.disableDoubleBuffering();
    }

    public void fadeOut(double x, double y, double halfWidth, double halfHeight) {
        float alpha = (float) 0;
        while (alpha < 1) {
            Color c = new Color((float) 1, (float) 1, (float) 1, alpha);
            StdDraw.setPenColor(c);
            StdDraw.filledRectangle(x, y, halfHeight, halfHeight);
            alpha += 0.05;
        }
    }

    public void fadeIn(double x, double y, double halfWidth, double halfHeight) {
        float alpha = (float) 1;
        while (alpha > 0) {
            Color c = new Color((float) 1, (float) 1, (float) 1, alpha);
            StdDraw.setPenColor(c);
            StdDraw.filledRectangle(x, y, halfHeight, halfHeight);
            alpha -= 0.01;
        }
    }

    public void colorFrame(int ID, Color c) {
        AnimatedJob curr = findInSchedule(ID);
        if (curr == null) {
            throw new IllegalArgumentException("cannot find a job with this ID in this simulator.");
        }
        StdDraw.setPenColor(c);
        StdDraw.rectangle(curr.getX(), curr.getY(), curr.getSizeX(), curr.getSizeY());
        StdDraw.rectangle(curr.getX(), curr.getY(), curr.getSizeX() + 2, curr.getSizeY() + 2);
        try {
            Thread.sleep(COLOR_FRAME);
        } catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    public void unColorFrame(int ID) {
        AnimatedJob curr = findInSchedule(ID);
        if (curr == null) {
            throw new IllegalArgumentException("cannot find a job with this ID in this simulator.");
        }
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.rectangle(curr.getX(), curr.getY(), curr.getSizeX(), curr.getSizeY());
        StdDraw.rectangle(curr.getX(), curr.getY(), curr.getSizeX() + 2, curr.getSizeY() + 2);
        try {
            Thread.sleep(COLOR_FRAME);
        } catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    public AnimatedJob animatedMove(Job job, Machine toMachine) {
        double destXCoor = 0;
        double destYCoor = 0;
        int destMchineID = toMachine.getID();
        if (schedule[destMchineID][0] == null) {
            double x = locations[destMchineID * 2];
            double y = locations[destMchineID * 2 + 1];
            destXCoor = x + MACHINE_SIZEX + 1.5 * JOB_SIZEX;
            destYCoor = y;
        } else {
            Job last = toMachine.jobList.getLast().job;
            AnimatedJob aniLast = findInSchedule(last);
            double x = aniLast.getX();
            double y = aniLast.getY();
            destXCoor = x + 2.5 * JOB_SIZEX;
            destYCoor = y;
        }
        return new AnimatedJob(job, destXCoor, destYCoor, JOB_SIZEX, JOB_SIZEY);
    }

    public AnimatedJob findInSchedule(int ID) {
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                if (schedule[i][j] != null) {
                    if (schedule[i][j].getJobID() == ID) {
                        return schedule[i][j];
                    }
                }
            }
        }
        return null;
    }

    public AnimatedJob findInSchedule(Job job) {
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                if (schedule[i][j] != null) {
                    if (schedule[i][j].getJob() == job) {
                        return schedule[i][j];
                    }
                }
            }
        }
        System.out.println("not in schedule");
        return null;
    }

    public void removeFromSchedule(Job job) {
        Machine currentMachine = job.runningMachine;
        int i = currentMachine.getID();
        int j = currentMachine.jobList.indexOf(job);
        schedule[i][j] = null;
    }

    public void insertToSchedule(AnimatedJob aniJob, Machine dest) {
        int i = dest.getID();
        int j = dest.jobList.indexOf(aniJob.getJob());
        schedule[i][j] = aniJob;
    }

    public void findLongestJobList() {
        LONGEST_JOB_LIST = 0;
        for (Machine machine : sim.machines) {
            int currSize = machine.jobList.getSize();
            if (currSize > LONGEST_JOB_LIST) {
                LONGEST_JOB_LIST = currSize;
            }
        }
    }

    public void printToScreen(double x, double y, String s, Color fontColor, int fontSize) {
        StdDraw.setPenColor(fontColor);
        Font font = new Font("Arial", Font.BOLD, fontSize);
        StdDraw.setFont(font);
        StdDraw.text(x, y, s);
    }

    public static void printArray(double[] arr) {
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            str += " " + arr[i];
        }
        System.out.println(str);
    }

    public static void printArray(AnimatedJob[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + "  ");
            }
            System.out.println();
        }
    }

}