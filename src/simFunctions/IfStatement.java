package simFunctions;

public class IfStatement implements Runnable{

	public static final int EQUALS = 0;
	public static final int LESS_THAN = 1;
	public static final int GREATER_THAN = 2;
	public static final int GREATER_OR_EQUAL = 3;
	public static final int LESS_OR_EQUAL = 4;
	
	private boolean isTrue = false;
	
	public IfStatement(Class<?> compType, Object val1, Object val2, int typeOfComp, Runnable action){
		if (compType.equals(Integer.class)){
			int a = (int)val1;
			int b = (int)val2;
			switch (typeOfComp){
			
			}
		}
	}
	
	@Override
	public void run() {
		if (isTrue){
			
		}
	}
	
}
