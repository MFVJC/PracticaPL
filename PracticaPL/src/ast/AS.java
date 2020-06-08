package ast;

import java.util.List;
import ast.I.*;
import ast.T.*;
import javafx.util.Pair;
import ast.E.*;

public class AS {
	
	  //Metodo para la creacion de clases de expresiones
	  public E num(String num) {return new Num(num);}
	  public E or(E opnd1, E opnd2) {return new Or(opnd1,opnd2);}
	  public E and(E opnd1, E opnd2) {return new And(opnd1,opnd2);}
	  public E equal(E opnd1, E opnd2) {return new Equal(opnd1,opnd2);}
	  public E greaterThan(E opnd1, E opnd2) {return new GreaterThan(opnd1,opnd2);}
	  public E lessThan(E opnd1, E opnd2) {return new LessThan(opnd1,opnd2);}
	  public E greaterEqual(E opnd1, E opnd2) {return new GreaterEqual(opnd1,opnd2);}
	  public E lessEqual(E opnd1, E opnd2) {return new LessEqual(opnd1,opnd2);}
	  public E notEqual(E opnd1, E opnd2) {return new NotEqual(opnd1,opnd2);}
	  public E suma(E opnd1, E opnd2) {return new Suma(opnd1,opnd2);}
	  public E resta(E opnd1, E opnd2) {return new Resta(opnd1,opnd2);}
	  public E mul(E opnd1, E opnd2) {return new Mul(opnd1,opnd2);}
	  public E div(E opnd1, E opnd2) {return new Div(opnd1,opnd2);}
	  public E mod(E opnd1, E opnd2) {return new Mod(opnd1, opnd2);}
	  public E elev(E opnd1, E opnd2) {return new Elev(opnd1, opnd2);}
	  public E squareBracket(E opnd1, E opnd2) {return new SquareBracket(opnd1,opnd2);}
	  public E dot(E opnd1, E opnd2) {return new Dot(opnd1, opnd2);}
	  
	  public E basicTrue() {return new BasicTrue();}
	  public E basicFalse() {return new BasicFalse();}
	  public E iden(String iden) {return new Iden(iden);}
	
	  
	  public E llamadaFuncion(String iden, List<E> args) {return new LlamadaFuncion(iden, args);}
	  public E pointer(E opnd1) {return new Asterisk(opnd1);}
	  public E not(E opnd1) {return new Not(opnd1);}
	  
	  // esta función yo la quitaría
	  public E menos(E opnd1) {return new Menos(opnd1);}

	  //Metodos para la creacion de clases de instrucciones
	  public I instIf(E condicion, List<I> cuerpo_if, List<I> cuerpo_else) {return new InstIf(condicion, cuerpo_if, cuerpo_else);}
	  public I instWhile(E condicion, List<I> cuerpo) {return new InstWhile(condicion, cuerpo);};
	  public I instSwitch(E condicion, List<Pair<E, List<I>>> cases) {return new InstSwitch(condicion, cases);};
	  // a cambiar
	  public I instDeclaracion(boolean constant, Tipo tipo, E iden, List<E> tam, List<E> valor) {return new InstDeclaracion(constant, tipo, iden, tam, valor);};
	  public I instAsignacion(E iden, E valor) {return new InstAsignacion(iden, valor);};
	  public I instStruct(E iden, List<I> declaraciones) {return new InstStruct(iden, declaraciones);};
	  public I instDeclFun(Tipo tipoReturn, String tipo, E iden, List<Pair<String, E>> args, List<I> cuerpo, E ret) {return new InstDeclFun(tipoReturn,tipo, iden, args, cuerpo, ret);};
	  public I instCallProc(String iden, List<E> args) {return new InstCallProc(iden, args);}

	  public Tipo tipoInt() {return new TipoInt();}
	  public Tipo tipoBoolean() {return new TipoBoolean();}
	  public Tipo tipoPuntero(Tipo claseApuntada) {return new TipoPuntero(claseApuntada);}
	  public Tipo tipoStruct(String nombreStruct) {return new TipoStruct(nombreStruct);}

}
