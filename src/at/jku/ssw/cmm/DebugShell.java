package at.jku.ssw.cmm;

public class DebugShell {
	
	public static void out( State state, Area area, String msg ){
		if( state == State.ERROR || state == State.WARNING )//state == State.STAT && area == Area.COMPILER )
			System.out.println( State.getName(state) + Area.getName(area) + " " + msg );
	}
	
	public enum State{
		LOG,
		ERROR,
		WARNING,
		STAT;
		
		protected static String getName( State s ){
			switch( s ){
			case ERROR:
				return "[error]";
			case LOG:
				return "[log]";
			case WARNING:
				return "[warning]";
			case STAT:
				return "[Stat]";
			default:
				return "[]";
			}
		}
	}
	
	public enum Area{
		SYSTEM,
		GUI,
		COMPILER,
		INTERPRETER,
		DEBUGGER,
		DEBUGMODE,
		READVAR,
		ERROR;
		
		protected static String getName( Area s ){
			switch( s ){
			case SYSTEM:
				return "[System]";
			case GUI:
				return "[GUI]";
			case COMPILER:
				return "[Compiler]";
			case INTERPRETER:
				return "[Interpreter]";
			case DEBUGGER:
				return "[Debugger]";
			case DEBUGMODE:
				return "[DebugMode]";
			case READVAR:
				return "[UpdateVar]";
			case ERROR:
				return "[error]";
			default:
				return "[]";
			}
		}
	}
}