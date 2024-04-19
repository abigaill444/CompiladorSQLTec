/*:-----------------------------------------------------------------------------
 *:                       INSTITUTO TECNOLOGICO DE LA LAGUNA
 *:                     INGENIERIA EN SISTEMAS COMPUTACIONALES
 *:                         LENGUAJES Y AUTOMATAS II           
 *: 
 *:                  SEMESTRE: ___________    HORA: ___________ HRS
 *:                                   
 *:               
 *:         Clase con la funcionalidad del Analizador Sintactico
 *                 
 *:                           
 *: Archivo       : SintacticoSemantico.java
 *: Autor         : Fernando Gil  ( Estructura general de la clase  )
 *:                 Grupo de Lenguajes y Automatas II ( Procedures  )
 *: Fecha         : 03/SEP/2014
 *: Compilador    : Java JDK 7
 *: Descripción   : Esta clase implementa un parser descendente del tipo 
 *:                 Predictivo Recursivo. Se forma por un metodo por cada simbolo
 *:                 No-Terminal de la gramatica mas el metodo emparejar ().
 *:                 El analisis empieza invocando al metodo del simbolo inicial.
 *: Ult.Modif.    :
 *:  Fecha      Modificó            Modificacion
 *:=============================================================================
 *: 22/Feb/2015 FGil                -Se mejoro errorEmparejar () para mostrar el
 *:                                 numero de linea en el codigo fuente donde 
 *:                                 ocurrio el error.
 *: 08/Sep/2015 FGil                -Se dejo lista para iniciar un nuevo analizador
 *:                                 sintactico.
 *: 20/FEB/2023 F.Gil, Oswi         -Se implementaron los procedures del parser
 *:                                  predictivo recursivo de leng BasicTec.
 *: 26/Feb/2023 Aranza Gassós,
                Franciso Woo,
                Samuel Alba         -Se comenzaron a agregar los procedures 
 *: 28/Feb/2023 Aranza Gassós,
                Franciso Woo,
                Samuel Alba         -Se finalizaron los procedures 
 *:-----------------------------------------------------------------------------
 */
package compilador;

import javax.swing.JOptionPane;

public class SintacticoSemantico {

    private Compilador cmp;
    private boolean analizarSemantica = false;
    private String preAnalisis;

    //--------------------------------------------------------------------------
    // Constructor de la clase, recibe la referencia de la clase principal del 
    // compilador.
    //
    public SintacticoSemantico(Compilador c) {
        cmp = c;
    }

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    // Metodo que inicia la ejecucion del analisis sintactico predictivo.
    // analizarSemantica : true = realiza el analisis semantico a la par del sintactico
    //                     false= realiza solo el analisis sintactico sin comprobacion semantica
    public void analizar(boolean analizarSemantica) {
        this.analizarSemantica = analizarSemantica;
        preAnalisis = cmp.be.preAnalisis.complex;

        // * * *   INVOCAR AQUI EL PROCEDURE DEL SIMBOLO INICIAL   * * *
        PROGRAMASQL();

    }

    //--------------------------------------------------------------------------
    private void emparejar(String t) {
        if (cmp.be.preAnalisis.complex.equals(t)) {
            cmp.be.siguiente();
            preAnalisis = cmp.be.preAnalisis.complex;
        } else {
            errorEmparejar(t, cmp.be.preAnalisis.lexema, cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Metodo para devolver un error al emparejar
    //--------------------------------------------------------------------------
    private void errorEmparejar(String _token, String _lexema, int numLinea) {
        String msjError = "";

        if (_token.equals("id")) {
            msjError += "Se esperaba un identificador";
        } else if (_token.equals("num")) {
            msjError += "Se esperaba una constante entera";
        } else if (_token.equals("num.num")) {
            msjError += "Se esperaba una constante real";
        } else if (_token.equals("literal")) {
            msjError += "Se esperaba una literal";
        } else if (_token.equals("oparit")) {
            msjError += "Se esperaba un operador aritmetico";
        } else if (_token.equals("oprel")) {
            msjError += "Se esperaba un operador relacional";
        } else if (_token.equals("opasig")) {
            msjError += "Se esperaba operador de asignacion";
        } else {
            msjError += "Se esperaba " + _token;
        }
        msjError += " se encontró " + (_lexema.equals("$") ? "fin de archivo" : _lexema)
                + ". Linea " + numLinea;        // FGil: Se agregó el numero de linea

        cmp.me.error(Compilador.ERR_SINTACTICO, msjError);
    }

    // Fin de ErrorEmparejar
    //--------------------------------------------------------------------------
    // Metodo para mostrar un error sintactico
    private void error(String _descripError) {
        cmp.me.error(cmp.ERR_SINTACTICO, _descripError);
    }

    // Fin de error
    //--------------------------------------------------------------------------
    //  *  *   *   *    PEGAR AQUI EL CODIGO DE LOS PROCEDURES  *  *  *  *
    //--------------------------------------------------------------------------
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889  
    private void PROGRAMASQL() {
        if (preAnalisis.equals("declare")
                || preAnalisis.equals("if")
                || preAnalisis.equals("while")
                || preAnalisis.equals("print")
                || preAnalisis.equals("assign")
                || preAnalisis.equals("select")
                || preAnalisis.equals("delete")
                || preAnalisis.equals("insert")
                || preAnalisis.equals("update")
                || preAnalisis.equals("create")
                || preAnalisis.equals("drop")
                || preAnalisis.equals("case")
                || preAnalisis.equals("end")){
            DECLARACION();
            SENTENCIAS();
            emparejar("end");
        } else {
            error("[PROGRAMASQL] Inicio de programa no válido. No. Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889  
    private void ACTREGS() {
        if (preAnalisis.equals("update")) {
            emparejar("update");
            emparejar("id");
            emparejar("set");
            IGUALACION();
            emparejar("where");
            EXPRCOND();
        } else {
            error("[ACTREGS] Error. Se esperaba update. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void COLUMNAS() {
        if (preAnalisis.equals("id")) {
            // COLUMNAS-> ID COLUMNAS'
            emparejar("id");
            COLUMNAS_();

        } else {
            error("[COLUMNAS] Error.Se esperaba id. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }

    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void COLUMNAS_() {
        if (preAnalisis.equals(",")) {
            //COLUMNAS' -> ,COLUMNAS
            emparejar(",");
            COLUMNAS();
        } else {
            //COLUMNAS-> empty
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void DECLARACION() {
        if (preAnalisis.equals("declare")) {
            //DECLARACION-> declare idvar TIPO DECLARACION 
            emparejar("declare");
            emparejar("idvar");
            TIPO();
            DECLARACION();
        } else {

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void DESPLIEGUE() {
        if (preAnalisis.equals("print")) {
            //DESPLIEGUE-> print EXPRARIT
            emparejar("print");
            EXPRARIT();
        } else {
            error("[DESPLIEGUE] Error.Se esperaba print No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void DELREG() {
        if (preAnalisis.equals("delete")) {
            // DELREG-> delete from id where EXPRCOND
            emparejar("delete");
            emparejar("from");
            emparejar("id");
            emparejar("where");
            EXPRCOND();
        } else {
            error("[DELREG] Error. Falta la palabra reservada delete No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRESIONES() {
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("("))
                 {
            //EXPRESIONES-> EXPRARIT EXPRESIONES'
            EXPRARIT();
            EXPRESIONES_();
        } else {
            error("[EXPRESIONES] Error.Se esperaba una expresión. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRESIONES_() {
        if (preAnalisis.equals(",")) {
            //EXPRESIONES -> , EXPRESIONES 
            emparejar(",");
            EXPRESIONES();
        } else {

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRARIT() {
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")) {

            //EXPRARIT-> OPERANDO EXPRARIT'
            OPERANDO();
            EXPRARIT_();

        } else if (preAnalisis.equals("(")) {
            //EXPRARIT -> (EXPRARIT) EXPRARIT'
            emparejar("(");
            EXPRARIT();
            emparejar(")");
            EXPRARIT_();
        } else {
              error("[EXPRARIT] Error.Se esperaba una expresión aritmética. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());

        }

    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRARIT_() {
        if (preAnalisis.equals("opsuma")) {
            //EXPRARIT' -> opsuma EXPRARIT
            emparejar("opsuma");
            EXPRARIT();
        } else if (preAnalisis.equals("opmult")) {
            //EXPRARIT' -> opmult EXPRARIT
            emparejar("opmult");
            EXPRARIT();
        } else {

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRCOND() {
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("(")) {
            //EXPRCOND -> EXPRREL EXPRLOG
            EXPRREL();
            EXPRLOG();
        } else {
            error("[EXPRCOND] Error, Se esperaba una expresión condicional. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRREL() {
        if (preAnalisis.equals("num")
                || preAnalisis.equals("num.num")
                || preAnalisis.equals("idvar")
                || preAnalisis.equals("literal")
                || preAnalisis.equals("id")
                || preAnalisis.equals("(")) {
            //EXPRREL-> EXPRARIT oprel EXPRARIT
            EXPRARIT();
            emparejar("oprel");
            EXPRARIT();
        } else {
            error("[EXPRREL] Error.Se esperaba una expresión relacional. No. de Línea: " + cmp.be.preAnalisis.getNumLinea());

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void EXPRLOG() {
        if (preAnalisis.equals("and")) {
            //EXPRLOG -> and EXPRREL
            emparejar("and");
            EXPRREL();
        } else if (preAnalisis.equals("or")) {
            //EXPRLOG -> or EXPRREL
            emparejar("or");
            EXPRREL();
        } else {

        }
    }
//--------------------------------------------------------------------------
// Aranza Abigail Gassós Salazar No. Control 19130889      

    private void ELIMTAB() {
        if (preAnalisis.equals("drop")) {
            //ELIMTAB -> drop table id
            emparejar("drop");
            emparejar("table");
            emparejar("id");
        } else {
            error("[ELIMTAB] Error. Falta palabra reservada drop No. de Línea: " + cmp.be.preAnalisis.getNumLinea());
        }
    }

//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
//Primeros(IFELSE) = {if}
    private void IFELSE() {
        if (preAnalisis.equals("if")) {
            emparejar("if");
            EXPRCOND();
            emparejar("begin");
            SENTENCIAS();
            emparejar("end");
            IFELSE_();
        } else {
            error("[IFELSE]. Se esperaba if. NUM LINEA: " + cmp.be.preAnalisis.numLinea);
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(IFELSE') = {else,empty}

    private void IFELSE_() {
        if (preAnalisis.equals("else")) {
            emparejar("else");
            emparejar("begin");
            SENTENCIAS();
            emparejar("end");
        } else {
            //VACIOOO
        }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
        //Primeros(IGUALACION) = {id}
    }

    private void IGUALACION() {
        if (preAnalisis.equals("id")) {
            emparejar("id");
            emparejar("opasig");
            EXPRARIT();
            IGUALACIONP();
        } else {
            error("[IGUALACION]. Se esperaba una asignacion valida NUM. LINEA: " + cmp.be.preAnalisis.numLinea);
        }

    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(IGUALACIONP) = {',', empty}

    private void IGUALACIONP() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            IGUALACION();

        } else {
            //VACIOOO
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(INSERCION) = {insert}

    private void INSERCION() {
        if (preAnalisis.equals("insert")) {
            emparejar("insert");
            emparejar("into");
            emparejar("id");
            emparejar("(");
            COLUMNAS();
            emparejar(")");
            emparejar("values");
            emparejar("(");
            EXPRESIONES();
            emparejar(")");

        } else {
            error("[INSERCION] Se esperaba una inserccion valida en la tabla" + cmp.be.preAnalisis.numLinea);
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(LISTADOS) = {',',empty}

    private void LISTAIDS() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            emparejar("id");
            LISTAIDS();
        } else {
            //VACIO
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(NULO) = {null,not,empty}

    private void NULO() {
        if (preAnalisis.equals("null")) {
            emparejar("null");
        } else if (preAnalisis.equals("not")) {
            emparejar("not");
            emparejar("null");
        } else {
            //VACIO
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985
    //Primeros(OPERANDO) = {num,num.num,idvar,literal,id}

    private void OPERANDO() {
        if (preAnalisis.equals("num")) {
            emparejar("num");
        } else if (preAnalisis.equals("num.num")) {
            emparejar("num.num");
        } else if (preAnalisis.equals("idvar")) {
            emparejar("idvar");
        }
          else if (preAnalisis.equals("literal")) {
            emparejar("literal");
        } else if (preAnalisis.equals("id")) {
            emparejar("id");
        } else {
            error("[OPERANDO]Se esperaba un dato valido NUM. LINEA" + cmp.be.preAnalisis.numLinea);
        }

    }
    
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985

       private void SENTENCIAS() {
        if (preAnalisis.equals("if") ||
            preAnalisis.equals("while") || 
            preAnalisis.equals("print") ||
            preAnalisis.equals("assign") ||
            preAnalisis.equals("select") ||
            preAnalisis.equals("delete") || 
            preAnalisis.equals("insert") ||
            preAnalisis.equals("update") || 
            preAnalisis.equals("create") ||
            preAnalisis.equals("drop") ||
            preAnalisis.equals("case")) {
            SENTENCIA();
            SENTENCIAS();
        } else {
            //VACIO
        }
    }
//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985

    private void SENTENCIA() {
        if (preAnalisis.equals("if")) {
            IFELSE();
        } else if (preAnalisis.equals("while")) {
            SENREP();
        } else if (preAnalisis.equals("print")) {
            DESPLIEGUE();
        } else if (preAnalisis.equals("assign")) {
            SENTASIG();
        } else if (preAnalisis.equals("select")) {
            SENTSELECT();
        } else if (preAnalisis.equals("delete")) {
            DELREG();
        } else if (preAnalisis.equals("insert")) {
            INSERCION();
        } else if (preAnalisis.equals("update")) {
            ACTREGS();
        } else if (preAnalisis.equals("create")) {
            TABLA();
        } else if (preAnalisis.equals("drop")){
            ELIMTAB();
        }
        else if (preAnalisis.equals("case")) {
            SELECTIVA();
        } else {
            error("[SENTENCIA]Se esperaba un tipo de sentencia valida NUM. LINEA" + cmp.be.preAnalisis.numLinea);
        }
    }

//-------------------------------------------------------------------------------
// Autor: Francisco Eduardo Woo Glz. #19130985

    private void SELECTIVA() {
        if (preAnalisis.equals("case")) {
            emparejar("case");
            SELWHEN();
            SELELSE();
            emparejar("end");
        } else {
            error("[SELECTIVA] Error. Se esperaba case. NO. LINEA: " + cmp.be.preAnalisis.numLinea);
        }
    }

//--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios No. Control 19130886
    //PRIMERO ( SELWHEN ) = { when }
    private void SELWHEN() {
        if (preAnalisis.equals("when")) {
            emparejar("when");
            EXPRCOND();
            emparejar("then");
            SENTENCIA();
            SELWHEN_();
        } else {
            error("[SELWHEN] Error.Se esperaba when. Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SELWHEN’ ) =  { PRIMEROS ( SELWHEN ) { when }, empty}
    private void SELWHEN_() {
        if (preAnalisis.equals("when")) {
            SELWHEN();
        } else {
            // empty
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SELELSE ) =  { else, empty }
    private void SELELSE() {
        if (preAnalisis.equals("else")) {
            emparejar("else");
            SENTENCIA();
        } else {
            //empty
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENREP ) =  { while }
    private void SENREP() {
        if (preAnalisis.equals("while")) {
            emparejar("while");
            EXPRCOND();
            emparejar("begin");
            SENTENCIAS();
            emparejar("end");

        } else {
            error("[SENREP]Error. Se esperaba while. Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENTASIG ) =  { assign }
    private void SENTASIG() {
        if (preAnalisis.equals("assign")) {
            emparejar("assign");
            emparejar("idvar");
            emparejar("opasig");
            EXPRARIT();
        } else {
            error("[SENTASIG] Error. Se esperaba assign. Linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENSELECT ) =  { select }
    private void SENTSELECT() {
        if (preAnalisis.equals("select")) {
            emparejar("select");
            emparejar("idvar");
            emparejar("opasig");
            emparejar("id");
            SENTSELECTC();
            emparejar("from");
            emparejar("id");
            emparejar("where");
            EXPRCOND();
        } else {
            error("[SENTSELECT]. Error. Se esperaba select. linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( SENSELECTC ) =  { ,, empty }
    private void SENTSELECTC() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            emparejar("idvar");
            emparejar("opasig");
            emparejar("id");
            SENTSELECTC();
        } else {
            // empty
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( TIPO ) =  { int, float, char }
    private void TIPO() {
        if (preAnalisis.equals("int")) {
            emparejar("int");
        }
        else if (preAnalisis.equals("float")) {
            emparejar("float");
        }
        else if (preAnalisis.equals("char")) {
            emparejar("char");
            emparejar("(");
            emparejar("num");
            emparejar(")");
        } else {
            error("[TIPO] Error.Se esperaba tipo de dato. linea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba Rios, No. Control 19130886
    //PRIMERO ( TABLA ) =  { create }
    private void TABLA() {
        if (preAnalisis.equals("create")) {
            emparejar("create");
            emparejar("table");
            emparejar("id");
            emparejar("(");
            TABLACOLUMNAS();
            emparejar(")");
        } else {
            error("[TABLA]Error. Se esperaba create. No. de Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba RIos, No. Control 19130886
    // PRIMERO ( TABLACOLUMNAS ) =  { id }
    private void TABLACOLUMNAS() {
        if (preAnalisis.equals("id")) {
            emparejar("id");
            TIPO();
            NULO();
            TABLACOLUMNAS_();
        } else {
            error("[TABLACOLUMNAS] Error. Se esperaba identificador. No. de Línea: " + cmp.be.preAnalisis.numLinea);
        }
    }

    //--------------------------------------------------------------------------
    // Autor: Samuel Alba RIos, No. Control 19130886
    // PRIMERO ( TABLACOLUMNASP ) =  { ,, empty }
    private void TABLACOLUMNAS_() {
        if (preAnalisis.equals(",")) {
            emparejar(",");
            TABLACOLUMNAS();
        } else {
            //empty
        }
    }
}
