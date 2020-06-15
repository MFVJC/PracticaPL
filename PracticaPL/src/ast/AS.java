package ast;

import java.util.List;
import ast.I.*;
import ast.T.*;
import javafx.util.Pair;
import ast.E.*;

public class AS {
	
	  //Metodo para la creacion de clases de expresiones
	  public E num(String num,int fila,int columna) {return new Num(num,fila,columna);}
	  public E or(E opnd1, E opnd2,int fila,int columna) {return new Or(opnd1,opnd2,fila,columna);}
	  public E and(E opnd1, E opnd2,int fila,int columna) {return new And(opnd1,opnd2,fila,columna);}
	  public E equal(E opnd1, E opnd2,int fila,int columna) {return new Equal(opnd1,opnd2,fila,columna);}
	  public E greaterThan(E opnd1, E opnd2,int fila,int columna) {return new GreaterThan(opnd1,opnd2,fila,columna);}
	  public E lessThan(E opnd1, E opnd2,int fila,int columna) {return new LessThan(opnd1,opnd2,fila,columna);}
	  public E greaterEqual(E opnd1, E opnd2,int fila,int columna) {return new GreaterEqual(opnd1,opnd2,fila,columna);}
	  public E lessEqual(E opnd1, E opnd2,int fila,int columna) {return new LessEqual(opnd1,opnd2,fila,columna);}
	  public E notEqual(E opnd1, E opnd2,int fila,int columna) {return new NotEqual(opnd1,opnd2,fila,columna);}
	  public E suma(E opnd1, E opnd2,int fila,int columna) {return new Suma(opnd1,opnd2,fila,columna);}
	  public E resta(E opnd1, E opnd2,int fila,int columna) {return new Resta(opnd1,opnd2,fila,columna);}
	  public E mul(E opnd1, E opnd2,int fila,int columna) {return new Mul(opnd1,opnd2,fila,columna);}
	  public E div(E opnd1, E opnd2,int fila,int columna) {return new Div(opnd1,opnd2,fila,columna);}
	  public E mod(E opnd1, E opnd2,int fila,int columna) {return new Mod(opnd1, opnd2,fila,columna);}
	  public E elev(E opnd1, E opnd2,int fila,int columna) {return new Elev(opnd1, opnd2,fila,columna);}
	  
	  public E basicTrue(int fila,int columna) {return new BasicTrue(fila,columna);}
	  public E basicFalse(int fila,int columna) {return new BasicFalse(fila,columna);}
	  public E iden(String iden,int fila,int columna) {return new Iden(iden,fila,columna);}
	
	  
	  public E llamadaFuncion(E nombreFuncion, List<E> args,int fila,int columna) {return new LlamadaFuncion(nombreFuncion, args,fila,columna);}
	  public E squareBracket(E opnd1, E opnd2,int fila,int columna) {return new SquareBracket(opnd1,opnd2,fila,columna);}
	  public E dot(E opnd1, E opnd2,int fila,int columna) {return new Dot(opnd1, opnd2,fila,columna);}
	  public E dollar(E opnd1,int fila,int columna) {return new Dollar(opnd1,fila,columna);}
	  public E nnew(Tipo tipo, E tam,int fila,int columna) {return new New(tipo, tam,fila,columna);} //nnew para que no coincida con la palabra reservada
	  public E not(E opnd1,int fila,int columna) {return new Not(opnd1,fila,columna);}

	  //Metodos para la creacion de clases de instrucciones
	  public I instIf(E condicion, List<I> cuerpo_if, List<I> cuerpo_else,int fila,int columna) {return new InstIf(condicion, cuerpo_if, cuerpo_else,fila,columna);}
	  public I instWhile(E condicion, List<I> cuerpo,int fila,int columna) {return new InstWhile(condicion, cuerpo,fila,columna);};
	  public I instSwitch(E condicion, List<Pair<E, List<I>>> cases,int fila,int columna) {return new InstSwitch(condicion, cases,fila,columna);};
	  
	  public I instDeclaracion(boolean constant, Tipo tipo, E iden, List<E> valor,int fila,int columna) {return new InstDeclaracion(constant, tipo, iden, valor,fila,columna);};
	  public I instAsignacion(E iden, E valor,int fila,int columna) {return new InstAsignacion(iden, valor,fila,columna);};
	  public I instStruct(E iden, List<I> declaraciones,int fila,int columna) {return new InstStruct(iden, declaraciones,fila,columna);};

	  public I instDeclFun(Tipo tipoReturn, E iden, List<Pair<Tipo, E>> args, List<I> cuerpo, E ret,int fila,int columna) {return new InstDeclFun(tipoReturn, iden, args, cuerpo, ret,fila,columna);};
	  public I instCallProc(E iden, List<E> args,int fila,int columna) {return new InstCallProc(iden, args,fila,columna);}

	  public Tipo tipoInt() {return new TipoInt();}
	  public Tipo tipoBoolean() {return new TipoBoolean();}
	  public Tipo tipoPuntero(Tipo tipoApuntado,int fila,int columna) {return new TipoPuntero(tipoApuntado,fila,columna);}
	  public Tipo tipoStruct(String nombreStruct,int fila,int columna) {return new TipoStruct(nombreStruct,fila,columna);}
	  public Tipo tipoArray(Tipo tipoBase, E dimension,int fila,int columna) {return new TipoArray(tipoBase, dimension,fila,columna);}

}
