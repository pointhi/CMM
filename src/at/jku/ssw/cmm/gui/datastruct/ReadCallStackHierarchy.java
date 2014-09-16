package at.jku.ssw.cmm.gui.datastruct;

import java.util.ArrayList;

import at.jku.ssw.cmm.CMMwrapper;
import at.jku.ssw.cmm.compiler.Obj;
import at.jku.ssw.cmm.compiler.Struct;
import at.jku.ssw.cmm.gui.treetable.DataNode;
import at.jku.ssw.cmm.gui.treetable.TreeTableDataModel;
import at.jku.ssw.cmm.interpreter.memory.Memory;
import at.jku.ssw.cmm.interpreter.memory.MethodContainer;

/**
 * Reads the call stack for all global and local variables to be displayed in the tree table
 * 
 * @author fabian
 *
 */
public class ReadCallStackHierarchy {
	
	public static TreeTableDataModel readSymbolTable( CMMwrapper compiler ){
		
		//Create root node
		DataNode node = new DataNode("root", "void", "", new ArrayList<DataNode>());
		
		//Add global variables
		node = readVariables(compiler.getSymbolTable().curScope.locals, node, Memory.getGlobalPointer());
		
		//Read local variables recursively
		getNextAddress( compiler, Memory.getFramePointer(), node );
		
		//Create tree table model
		TreeTableDataModel model = new TreeTableDataModel( node );
		
		return model;
	}
	
	private static void getNextAddress( CMMwrapper compiler, int address, DataNode node ){
		
		String name;

		int methodID = Memory.loadInt(address-8);
		name = MethodContainer.getMethodName(methodID);

		DataNode funcNode = new DataNode( name + "()", "", "line " + Memory.loadInt(address - 16), new ArrayList<DataNode>() );
		readVariables( ReadSymbolTable.findNodeByName(compiler.getSymbolTable().curScope.locals, name).locals, funcNode, address );
		node.add(funcNode);
		
		if( name == "main" )
			return;
		else
			getNextAddress( compiler, Memory.loadInt(address-4), node );
	}
	
	private static DataNode readVariables( Obj obj, DataNode node, int address ){
		
		while( obj != null ){
				if( obj.type.kind == Struct.INT ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "int", Memory.loadInt(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "int", Memory.loadInt(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "int", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.CHAR ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "char", Memory.loadChar(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "char", Memory.loadChar(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "char", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.FLOAT ){
					if( obj.kind == Obj.VAR && !obj.isRef ){
						node.add(new DataNode(obj.name, "float", Memory.loadFloat(address + obj.adr), null));
					}
					else if( obj.kind == Obj.VAR && obj.isRef ){
						node.add(new DataNode(obj.name, "float", Memory.loadFloat(Memory.loadInt(address + obj.adr)), null));
					}
					else{
						node.add(new DataNode(obj.name, "float", obj.val, null));
					}
				}
				if( obj.type.kind == Struct.ARR ){
					node.add(readArray(obj, address + obj.adr));
				}
				if( obj.type.kind == Struct.STRUCT ){
					DataNode n = readVariables( obj.type.fields, new DataNode(obj.name, "struct", "", new ArrayList<DataNode>()), address + obj.adr );
					node.add(n);
				}
				if( obj.type.kind == Struct.STRING ){
					node.add(new DataNode(obj.name, "string", "", null));
				}
			
			//Next symbol table node
			obj = obj.next;
		}
		return node;
	}
	
	private static DataNode readArray( Obj count, int address ){
		
		int length = count.type.elements;
		int size = count.type.size / count.type.elements;
		
		System.out.println("Reading array...");
		
		DataNode node = new DataNode( count.name, "array", "", new ArrayList<DataNode>() );
		
		for( int i = 0; i < length; i++ ){
			
			Object value = "";
			String typeName = "";
			
			if( count.kind == Struct.INT ){
				typeName = "int";
				value = Memory.loadInt(address + size * i);
			}
			else if( count.kind == Struct.CHAR ){
				typeName = "char";
				value = Memory.loadChar(address + size * i);
			}
			else if( count.kind == Struct.FLOAT ){
				typeName = "float";
				value = Memory.loadFloat(address + size * i);
			}
			else if( count.kind == Struct.BOOL ){
				typeName = "bool";
			}
			else if( count.kind == Struct.ARR ){
				node.add(readArray(count.type.fields, address + address + size * i));
			}
			else if( count.kind == Struct.STRUCT ){
				DataNode n = readVariables( count.type.fields, new DataNode(count.name, "struct", "", new ArrayList<DataNode>()), address + count.adr );
				node.add(n);
			}
			
			node.add(new DataNode("" + i, typeName, "" + value, null));
		}
		
		return node;
		
	}
	
	public static DataNode createDataStructure() {
        DataNode root = new DataNode("R1", "R1", Integer.valueOf(10), null);
        return root;
    }
}