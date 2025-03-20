package financeapp.command;

public class TimerCommandDecorator implements Command {
    private Command command;

    public TimerCommandDecorator(Command command) {
        this.command = command;
    }

    public void execute() {
        long start = System.currentTimeMillis();
        command.execute();
        long end = System.currentTimeMillis();
        System.out.println("Command executed in " + (end - start) + " ms");
    }
}
