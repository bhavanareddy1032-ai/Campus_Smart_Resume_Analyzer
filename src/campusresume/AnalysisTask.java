package campusresume;

public class AnalysisTask extends Thread {
    private final String taskName;

    public AnalysisTask(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println("Running " + taskName + " on thread " +
                           Thread.currentThread().getName());
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
