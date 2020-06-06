package ast.E;

import ast.E.TipoE;

public class Num extends E {
  private String v;
  
  public Num(String v) {
   this.v = v;   
  }
  public String num() {return v;}
  public TipoE tipo() {return TipoE.NUM;}   
  public String toString() {return "{" + v + "}";}  
}
