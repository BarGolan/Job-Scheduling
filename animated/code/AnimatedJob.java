public class AnimatedJob {

    Job job;
    int jobID;

    double sizeX;
    double sizeY;
    double x;
    double y;

    public AnimatedJob(Job job, double x, double y, double sizeX, double sizeY) {
        this.job = job;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.x = x;
        this.y = y;
        this.jobID = job.getID();
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public int getJobID() {
        return this.jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public double getSizeX() {
        return sizeX;
    }

    public void setSizeX(double sizeX) {
        this.sizeX = sizeX;
    }

    public double getSizeY() {
        return sizeY;
    }

    public void setSizeY(double sizeY) {
        this.sizeY = sizeY;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString() {
        return this.job.toString2();
    }
}
