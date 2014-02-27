package interfaceGUI;

public class ThreadTimer extends Thread {

	private int delay;
	private Runnable action;
	private int actions;
	private boolean forever = false;
	public static int FOREVER = -1;
	
	public ThreadTimer(int interval, Runnable run, int times){
		delay = interval;
		action = run;
		actions = times;
		forever = times == -1;
		this.start();
	}
	
	public void run(){
		while (actions > 0 || forever){
			try {
				Thread.sleep(delay);
			}
			catch (Exception e){
				return;
			}
			action.run();
			if (!forever){
				actions--;
			}
		}	
		return;
	}
	
}
