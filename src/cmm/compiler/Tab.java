package cmm.compiler;

/*--------------------------------------------------------------------------------
Tab   Symbol table for C--
===   ====================
The symbol table is a stack of scopes
- universe: contains predeclared names
- global scope: contains the globally declared names
- local scope: contains the local names of a procedure

The symbol table has methods for
- opening and closing scopes
- inserting and retrieving named objects
- checking of forward declarations
- utilities for converting strings to constants
--------------------------------------------------------------------------------*/

public class Tab {
	public Scope curScope;	         // current scope
	public int   curLevel;	         // nesting level of current scope

	public static Struct intType;    // predefined types
	public static Struct floatType;
	public static Struct charType;
	public static Struct boolType;
	public static Struct noType;
	public static Obj noObj;		     // predefined objects

	private Parser parser;           // for error messages

	//------------------ scope management ---------------------

	public void openScope() {
		// TODO
	}

	public void closeScope() {
		curScope = curScope.outer;
		curLevel--;
	}

	//------------- Object insertion and retrieval --------------

	// Create a new object with the given kind, name and type
	// and insert it into the current scope.
	public Obj insert(int kind, String name, Struct type) {
		// TODO
	}

	// Look up the object with the given name in all open scopes.
	// Report an error if not found.
	public Obj find(String name) {
		// TODO
	}

	// Retrieve a struct field with the given name from the fields of "type"
	public Obj findField(String name, Struct type) {
		// TODO
	}

	// Look up the object with the given name in the current scope.
	// Return noObj if not found.
	public Obj lookup(String name) {
		// TODO
	}

	//----------------- handling of forward declaration  -----------------

	// Check if parameters of forward declaration and actual declaration match
	public void checkForwardParams(Obj oldPar, Obj newPar) {
		// TODO
	}

	// Check if all forward declarations were resolved at the end of the program
	public void checkIfForwardsResolved(Scope scope) {
		// TODO
	}

	//---------------- conversion of strings to constants ----------------

	// Convert a digit string into an int
	public int intVal(String s) {
		// TODO
	}

	// Convert a string representation of a float constant into a float value
	public float floatVal(String s) {
		// TODO
	}

	// Convert a string representation of a char constant into a char value
	public char charVal(String s) {
		// TODO
	}

	//---------------- methods for dumping the symbol table --------------

	// Print a type
	public void dumpStruct(Struct type, int indent) {
		switch (type.kind) {
			case Struct.INT:
			  System.out.print("Int(" + type.size + ")"); break;
			case Struct.FLOAT:
			  System.out.print("Float(" + type.size + ")"); break;
			case Struct.CHAR:
			  System.out.print("Char(" + type.size + ")"); break;
			case Struct.BOOL:
				System.out.print("Bool(" + type.size + ")"); break;
			case Struct.ARR:
			  System.out.print("Arr[" + type.elements + "(" + type.size + ")] of ");
			  dumpStruct(type.elemType, indent);
			  break;
			case Struct.STRUCT:
			  System.out.println("Struct(" + type.size + ") {");
			  for (Obj o = type.fields; o != null; o = o.next) dumpObj(o, indent + 1);
			  for (int i = 0; i < indent; i++) System.out.print("  ");
			  System.out.print("}");
			  break;
			default:
			  System.out.print("None"); break;
		}
	}

	// Print an object
	public void dumpObj(Obj o, int indent) {
		for (int i = 0; i < indent; i++) System.out.print("  ");
		switch (o.kind) {
			case Obj.CON:
			  System.out.print("Con " + o.name);
			  if (o.type == Tab.floatType) System.out.print(" fVal=" + o.fVal);
			  else System.out.print(" val=" + o.val);
			  break;
			case Obj.VAR:
			  System.out.print("Var " + o.name + " adr=" + o.adr + " level=" + o.level);
			  if (o.isRef) System.out.print(" isRef");
			  break;
			case Obj.TYPE:
			  System.out.print("Type " + o.name);
			  break;
			case Obj.PROC:
			  System.out.println("Proc " + o.name + " size=" + o.size + " nPars=" + o.nPars + " isForw=" + o.isForward + " {");
			  dumpScope(o.locals, indent + 1);
			  System.out.print("}");
			  break;
			default:
			  System.out.print("None " + o.name);
			  break;
		}
		System.out.print(" ");
		dumpStruct(o.type, indent);
		System.out.println();
	}

	// Print all objects of a scope
	public void dumpScope(Obj head, int indent) {
		for (Obj o = head; o != null; o = o.next) dumpObj(o, indent);
	}

	//-------------- initialization of the symbol table ------------

	public Tab(Parser parser) {
		Obj o;
		this.parser = parser;
		curScope = new Scope();
		curScope.outer = null;
		curLevel = -1;

		// create predeclared types
		intType   = new Struct(Struct.INT);
		floatType = new Struct(Struct.FLOAT);
		charType  = new Struct(Struct.CHAR);
		boolType  = new Struct(Struct.BOOL);
		noType    = new Struct(Struct.NONE);
		noObj     = new Obj(Obj.VAR, "???", noType);

		// insert predeclared types into universe
		insert(Obj.TYPE, "int", intType);
		insert(Obj.TYPE, "float", floatType);
		insert(Obj.TYPE, "char", charType);
	}
}
