/*
 * $Id$
 *
 * Copyright 2005 David A. Hall
 */
 
package org.jdesktop.swingx.expression;
 
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import net.sf.jga.fn.BinaryFunctor;
import net.sf.jga.fn.Generator;
import net.sf.jga.fn.UnaryFunctor;
import net.sf.jga.fn.adaptor.ApplyUnary;
import net.sf.jga.fn.adaptor.Constant;
import net.sf.jga.fn.adaptor.ConstantUnary;
import net.sf.jga.fn.adaptor.Identity;
import net.sf.jga.fn.algorithm.Accumulate;
import net.sf.jga.fn.algorithm.TransformUnary;
import net.sf.jga.fn.arithmetic.Divides;
import net.sf.jga.fn.arithmetic.Plus;
import net.sf.jga.fn.comparison.Max;
import net.sf.jga.fn.comparison.Min;
import net.sf.jga.fn.property.GetProperty;
import net.sf.jga.fn.property.InvokeMethod;
import net.sf.jga.fn.property.InvokeNoArgMethod;
import net.sf.jga.parser.FunctorParser;
import net.sf.jga.parser.FunctorRef;
import net.sf.jga.parser.GeneratorRef;
import net.sf.jga.parser.ParseException;
import net.sf.jga.parser.UnaryFunctorRef;
import org.jdesktop.dataset.DataColumn;
import org.jdesktop.dataset.DataRow;
import org.jdesktop.dataset.DataSet;
import org.jdesktop.dataset.DataTable;
import org.jdesktop.dataset.DataValue;
 
public class Parser extends FunctorParser {
    private DataTable table;
 
    boolean inTableContext = false;
 
    // Functor that returns the list of rows for a given table
    private UnaryFunctor<DataTable,List/*<DataRow>*/> getRowsFn =
        new GetProperty<DataTable,List/*<DataRow>*/>(DataTable.class, "Rows");
 
    // Functor that returns the number of rows in a list
    private UnaryFunctor<DataTable,Integer> countRowsFn =
        new InvokeNoArgMethod<List/*<DataRow>*/,Integer>(List.class, "size").compose(getRowsFn);
 
    // Functor that returns an iterator over the rows in a list
    private UnaryFunctor<DataTable,Iterator/*<DataRow>*/> iterateTableFn =
        new InvokeNoArgMethod<List/*<DataRow>*/,Iterator/*<DataRow>*/>(List.class, "iterator")
            .compose(getRowsFn);
 
    // Functor that returns the value of a DataValue
    private UnaryFunctor<DataValue,?> getValueFn =
        new GetProperty<DataValue,Object>(DataValue.class, "Value");
    
    // =============================
    // DataSet specific entry points
    // =============================
 
    /**
     * Parses a computed column expression, for the given table.  
     */
    UnaryFunctor<DataRow,?> parseComputedColumn(DataTable table, String expression)
        throws ParseException
    {
        setCurrentTable(table);
        try {
            return parseUnary(expression, DataRow.class);
        }
        finally {
            setCurrentTable(null);
        }
    }
 
    /**
     * Parses an expression that computes a summary value for the collection of data
     * currently available in the bound dataset.
     */
    Generator<?> parseDataValue(String expression) throws ParseException {
        inTableContext = true;
        try {
            return parseGenerator(expression);
        }
        finally {
            inTableContext = false;
            setCurrentTable(null);
        }
    }
                                                    
    
    // =============================
    // FunctorParser extensions
    // =============================
    
 
    protected FunctorRef reservedWord(String name) throws ParseException {
//         System.out.println("reservedWord(\""+name+"\")");
 
        // If we're expecting a table but we don't know which one, then see if the name
        // is the name of a table.  If so, then this is the table we're expecting.
        if (table == null) {
            DataTable maybeTable = ((DataSet) getBoundObject()).getTable(name);
            if (maybeTable != null) {
                setCurrentTable(maybeTable);
                return new GeneratorRef(new Constant<DataTable>(table), DataTable.class);
            }
        }
        
        // Next, see if the name is the same as the current table then return a
        // constant reference to the table
        
        if (table != null && name.equals(table.getName()))
            return new GeneratorRef(new Constant<DataTable>(table), DataTable.class);
 
        // If we're expecting a table and we don't know which one yet, then
        // if the name is the name of a table, this must it
 
        if (table != null) {
            DataColumn col = table.getColumn(name);
            if (col != null) 
                return makeColumnRef(table, col);
        }
        
        return null;
    }
 
    /**
     * Allows for (not necessarily constant) predefined fields to be added to the grammar
     */
    protected FunctorRef reservedField(FunctorRef prefix, String name) throws ParseException {
//         System.out.println("reservedField("+prefix+", \""+name+"\")");
 
        if (isTableReference(prefix) && getReferencedTable(prefix).equals(table)) {
            DataColumn col = table.getColumn(name);
            if (col != null) {
                return makeColumnRef(table, col);
            }
        }                       
            
        return super.reservedField(prefix, name);
    }
 
    /**
     * Allows for function-style names to be added to the grammar.
     */
    protected FunctorRef reservedFunction(String name, FunctorRef[] args) throws ParseException{
//         System.out.println("reservedFunction(\""+name+"\", "+Arrays.toString(args)+")");
 
        if (inTableContext) {
            assert table != null;
            assert args.length == 1;
            assert args[0].getReferenceType() == FunctorRef.UNARY_FN;
            assert args[0].getArgType(0).equals(DataRow.class);
            assert Comparable.class.isAssignableFrom(args[0].getReturnType());
 
            // count doesn't require iterating over the rows of the table: having the
            // list of rows is good enough
            if ("count".equals(name))
                return new GeneratorRef(countRowsFn.bind(table), Integer.class);
 
            // All of the other summary functions require iterating over the list of
            // rows in the table.
            Class type = args[0].getReturnType();
            Generator<Iterator/*<DataRow>*/> iterateRows = iterateTableFn.bind(table);
            TransformUnary xform = new TransformUnary(((UnaryFunctorRef) args[0]).getFunctor());
            BinaryFunctor bf = null;
            
            if ("max".equals(name)) 
                bf = new Max(new net.sf.jga.util.ComparableComparator());
                
            else if ("min".equals(name))
                bf = new Min(new net.sf.jga.util.ComparableComparator());
 
            else if ("sum".equals(name)) {
                if (type.isPrimitive())
                    type = getBoxedType(type);
                
                if (Number.class.isAssignableFrom(type))
                    bf = new Plus(type);
                else {
                    String msg = "Unable to compute sum of type {0}";
                    Object[] msgargs = new Object[] { type.getName() };
                    throw new ParseException(MessageFormat.format(msg, msgargs));
                }
            }
            
            // The simpler summary functions only require an iteration, with no further
            // processing.
            if (bf != null) {
                Generator gen = new Accumulate(bf).generate(xform.generate(iterateRows));
                return new GeneratorRef(gen, type);
            }
 
            // Computing average in this way is more efficient than building an average
            // functor -- although the average of a zero length list may cause a math
            // exception
            if ("avg".equals(name)) {
                if (type.isPrimitive())
                    type = getBoxedType(type);
                
                BinaryFunctor add = new Plus(type);
                Generator sum = new Accumulate(add).generate(xform.generate(iterateRows));
                Generator count = countRowsFn.bind(table);
                Generator div = new Divides(type).generate(sum,count);
                return new GeneratorRef(div, type);
            }
        }
 
        return super.reservedFunction(name, args);
    }
 
    // ======================
    // implementation details
    // ======================
 
    
    /**
     * Sets the table against which column references will be created.  There can only be
     * a single table involved in any given expression.
     */
    private void setCurrentTable(DataTable table) throws ParseException {
        if (this.table != null && table != null)
            throw new ParseException("Parser is currently associated with table " +this.table);
 
        this.table = table;
    }
 
    /**
     * Builds a functor for a given table and column.  The functor takes a row in the
     * table and returns the value of the appropriate column.
     */
    private UnaryFunctorRef makeColumnRef(DataTable table, DataColumn column) {
        // Builds a functor that takes a Row, and returns an array consisting
        // of that row and the column we've been given
        ApplyUnary<DataRow> args =
            new ApplyUnary<DataRow>(new UnaryFunctor[]
                { new Identity<DataRow>(),new ConstantUnary<DataRow,DataColumn>(column) });
        
        // getValue(col,row) method as a functor
        InvokeMethod<DataTable,?> getValue =
            new InvokeMethod<DataTable,Object>(DataTable.class,"getValue",
                                               new Class[] {DataRow.class, DataColumn.class});
        
        // tie the two together.  The result is a functor that takes a row and returns
        // the value in the designated column
        UnaryFunctor<DataRow,?> value = getValue.bind1st(table).compose(args);
        
        // Return a parser description of the functor we've built.
        return new UnaryFunctorRef(value, DataRow.class, ARG_NAME[0], column.getType());
    }
    
    
    /**
     * returns true if the functor reference is one that returns a specific data table.
     */
    private boolean isTableReference(FunctorRef ref) {
        return ref != null && ref.getReturnType().equals(DataTable.class) &&
               ref.getReferenceType() == FunctorRef.CONSTANT;
    }
 
    
    /**
     * returns the specific table to which the given functor refers.  Assumes isTableReference(ref),
     * will throw ClassCastException if not
     */
    private DataTable getReferencedTable(FunctorRef ref) {
        return (DataTable)((GeneratorRef) ref).getFunctor().gen();
    }
}
